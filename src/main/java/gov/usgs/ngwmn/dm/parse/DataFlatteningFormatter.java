package gov.usgs.ngwmn.dm.parse;

import static gov.usgs.ngwmn.dm.parse.MimeType.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * DataFlatteningFormatter accepts an arbitrary XML Stream and renders it as
 * a Resultset-like HTML table. Basically, it treats children of the root
 * elements as rows, and each of the attributes and children as columns.
 *
 * The Formatter can be configured to recognize either elements at a certain
 * depth as rows, or recognize specified elements as rows. Use the methods
 * setRowElementName() for this. By default, the
 * depth level is set to 3, e.g. the grandchildren elements of the root element.
 *
 * CAVEAT: Assumes the first element found is complete.
 *
 * @author ilinkuo
 *
 */
public class DataFlatteningFormatter extends AbstractFormatter implements IFormatter {

	public static final Pattern       quoteLiteral            = Pattern.compile("\"");
	
	public static final String        DEFAULT_AUTHOR          = "USGS";
	// CONSTANTS
	public static final MimeType      DEFAULT_MIMETYPE        = HTML;
	public static final String        FLATTENING_PARAM        = "needsCompleteFirstRow";


	// ======================
	// PUBLIC UTILITY
	// ======================
	public static final Set<MimeType> acceptableTypes         = EnumSet.of(EXCEL, HTML, XHTML, XML, CSV, TAB);
	/**
	 * Returns true if the DataFlatteningFormatter class ( not the instance ) may accept the output type
	 * @param type
	 * @return
	 */
	public static boolean mayAccept(MimeType type) {
		return acceptableTypes.contains(type);
	}


	// ====================
	// CONFIGURATION FIELDS
	// ====================
	protected final Delimiters delims;
	protected final Set<String> ignoredAttributes;
	
	// TODO these do not need to be final used final to see if they are set more than once
	protected final String author;
	protected final boolean throwExceptionOnTooManyValues;
	
	protected boolean isSilent;                 // set true to not output error messages
	protected int     depthLevel;

	ParseState state;
	
	// ============
	// CONSTRUCTORS
	// ============
	public DataFlatteningFormatter() {
		this(DEFAULT_MIMETYPE, false);
	}

	public DataFlatteningFormatter(MimeType type, boolean throwExceptions) { //, boolean throwExceptions
		super(type);
		state                  = new ParseState();
		author                 = DEFAULT_AUTHOR;
		ignoredAttributes      = new HashSet<String>();
		throwExceptionOnTooManyValues = throwExceptions;
		
		switch (type) {
			case EXCEL:
				// TODO use configuration parameter
				delims = Delimiters.makeExcelDelimiter(author, new Date().toString());
				break;
			case HTML:
			case XHTML:
			case XML:
				delims = Delimiters.HTML_DELIMITERS;
				break;
			case CSV:
				delims = Delimiters.CSV_DELIMITERS;
				break;
			case TAB:
				delims = Delimiters.TAB_DELIMITERS;
				break;
			default:
				if (acceptableOutputTypes.contains(type)) {
					throw new UnsupportedOperationException(type + " is accepted but not yet implemented");
				}
				throw new IllegalArgumentException(type + " not accepted by " + DataFlatteningFormatter.class.getSimpleName());
		}
		acceptableOutputTypes = EnumSet.of(type);
		ignoreAttribute("schemaLocation");
		ignoreAttribute("encodingStyle");
		ignoreAttribute("xsi:schemaLocation");
		ignoreAttribute("xmlns:xsi");
		ignoreAttribute("xmlns");
	}


	// =====================
	// CONFIGURATION METHODS
	// =====================

	public DataFlatteningFormatter setRowElementName(String name) {
		if (depthLevel > 0) {
			throw new IllegalStateException("Can only set depthLevel or rowElementName, not both");
		}
		state.maxRowDepthLevel = 1000; // set the ROW_DEPTH_LEVEL so deep that it never triggers
		state.rowElementIdentifier = name;
		return this;
	}

	/**
	 * Set true to keep the information from elder elements when flattening
	 * @param isKeepElder
	 * @return 
	 */
	public DataFlatteningFormatter setKeepElderInfo(boolean isKeepElder) {
		state.isKeepElders = isKeepElder;
		return this;
	}
	
	// TODO only called by ZipFormatter nested call - no outside calls found within project
	@Override
	public boolean isNeedsCompleteFirstRow() {
		return true;
	}

	public DataFlatteningFormatter setIgnoreRowElement(boolean ignore) {
		state.ignoreRowElement = ignore;
		return this;
	}

	/**
	 * Set true to copy down all of elders information when flattening
	 * @param copyDown
	 * @return 
	 */
	// TODO only called from test classes
	protected DataFlatteningFormatter setCopyDown(boolean copyDown) {
		// You can't copy down elders info without keeping it, can you?
		state.isDoCopyDown = copyDown;
		if (copyDown) {
			state.isKeepElders = true;
		}
		return this;

	}

	protected DataFlatteningFormatter ignoreAttribute(String attributeName) {
		ignoredAttributes.add(attributeName);
		return this;
	}

	// TODO only called from test classes
	protected DataFlatteningFormatter addContentDefinedElement(String elementName, String attributeName) {
		if (elementName != null ) {
			state.contentDefinedElements.put(elementName, attributeName);
		}
		return this;
	}

	// ==============
	// SERVICE METHOD
	// ==============
	/**
	 * @see gov.usgs.webservices.framework.formatter.AbstractFormatter#dispatch(javax.xml.stream.XMLStreamReader, java.io.Writer)
	 *
	 * Note that namespaces are ignored.
	 */
	@Override
	public void dispatch(XMLStreamReader in, Writer out) throws IOException {

		ErrorChecker checker = new ErrorChecker(throwExceptionOnTooManyValues);

		try {
			out.write(delims.sheetStart); // TODO this should be moved to start document
			boolean done = false;
			
			while ( ! done && in.hasNext() ) {
				int event = in.next();
				switch (event) {
					case XMLStreamConstants.START_DOCUMENT:
						break; // no start document handling needed
					case XMLStreamConstants.START_ELEMENT:
						startElement(in);
						break;
					case XMLStreamConstants.CHARACTERS:
						state.putChars(in.getText().trim());
						break;
					case XMLStreamConstants.ATTRIBUTE:
						// TODO may need to handle this later
						break;
					case XMLStreamConstants.END_ELEMENT:
						// this is where writing to the stream happens
						// before this it was all setup
						endElement(in, out, checker);
						break;
					case XMLStreamConstants.END_DOCUMENT:
						done = true;
						break;
					// TODO no default
				}
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			out.write(delims.sheetEnd); // TODO this should be moved to end document
			if ( ! isSilent ) {
				if ( checker.hasTooManyElderValuesError() ) 
					System.err.println("DataFlattener: Number of elder values exceeds number of headers");
				if ( checker.hasTooManyTargetValuesError() ) 
					System.err.println("DataFlattener: Number of target values exceeds number of headers");
			}
		}
		out.flush();

	}

	protected void startElement(XMLStreamReader in) {
		

		String  localName   = in.getLocalName();
		String  displayName = state.startElementBeginUpdate(in);

		if ( state.isTargetFound() && state.isInTarget ) {
			// PROCESS THE ELEMENT HEADERS
			// Read and record the column headers from the first row's elements.
			// Add columns for later rows, but they don't get headers because
			// we're streaming and can't go back to the column headers.
			state.addHeaderOrColumn(localName, displayName);
			processAttributeHeadersNamesValues(in, state.current(), 
					state.targetColumnList, state.targetColumnValues);
			
		} else if ( state.isKeepElders && ! state.isInTarget ) {
			state.addElderHeaderOrColumn(localName);
			processAttributeHeadersNamesValues(in, state.current(), 
					state.elderColumnList, state.elderColumnValues);
		}
	}

	protected void processAttributeHeadersNamesValues(XMLStreamReader in, String currentState, 
			Set<Element> elements, Map<String,String> values) {
		
		String  localName            = in.getLocalName();
		boolean isContentDefined     = state.isCurrentElementContentDefined(in);
		String  contentAttributeName = isContentDefined ? state.contentDefinedElements.get(localName) : null;
		
		// PROCESS/STORE ATTRIBUTE HEADERS AND NAME/VALUES
		for (int i=0; i<in.getAttributeCount(); i++) {
			String attLocalName = in.getAttributeLocalName(i);
			
			if ( ! ignoredAttributes.contains(attLocalName) 
			  && ! (isContentDefined && attLocalName.equals(contentAttributeName)) ) {
				String fullName = state.makeFullName(currentState, attLocalName);
				elements.add( new Element(fullName, attLocalName, null) );
				values.put(fullName, in.getAttributeValue(i).trim());
			}
		}
	}

	/**
	 * This method handles the writing to the OutputStream vi a Writer.
	 * It performs all the decoration for the given mimetype rows/element indicators.
	 * If the mimetype components where extracted from the parsing this would be a bit
	 * less complicated.
	 * 
	 * 
	 * @param in
	 * @param out
	 * @param state
	 * @param checker
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void endElement(XMLStreamReader in, Writer out, ErrorChecker checker) 
			throws IOException {
		
		String localName = in.getLocalName();

		// Write tag content
		boolean onTargetEnd = state.isOnTargetRowStartOrEnd(localName);
		if (onTargetEnd) {

			// TODO processing headers has a similar pattern to processing a row
			
			// OUTPUT HEADER row first, if not already done
			if (state.isProcessingHeaders) {
				// write out the columns headers first
				out.write(delims.headerRowStart);

				// preprocess to disambiguate common column headers
				if (state.isKeepElders) {
					updateQualifiedNames(state.elderColumnList, state.targetColumnList);
					//output the elder headers
					// we are making the assumption here that there are ALWAYS target columns, otherwise, it's a bit of a pain to coordinate.
					int count = writeHeaders(out, checker, false, state.elderColumnList);
					checker.updateElderHeaderCount(count);

				} else {
					updateQualifiedNames(state.targetColumnList);
				}
				
				int count = writeHeaders(out, checker, true, state.targetColumnList);
				checker.updateTargetHeaderCount(count);

				out.write(delims.headerRowEnd);
				// bookkeeping
				state.isProcessingHeaders = false;
			}
			
			// TODO it would seem that if the end call is to process headers then there is no data this round unless the headers are bound to the first row of data

			// OUTPUT DATA row only if there is content
			if ( state.hasTargetContent() ) {
				//avoid writing out (a new line) delimiter for the first row.
				// TODO this might be better if the bodyRowEnd handled the line separator
				if ( ! delims.bodyRowStart.equals("\n") || ! state.isFirstRow() ) {
					out.write(delims.bodyRowStart);
				}
				if (state.isKeepElders) {
					int count = writeValues(out, checker, false, state.elderColumnList, state.elderColumnValues);
					checker.updateElderValueCount(count);

					if ( ! state.isDoCopyDown ) {
						// clear ALL the elder values
						state.elderColumnValues.clear();
					}
				}

				int count = writeValues(out, checker, true, state.targetColumnList, state.targetColumnValues);
				checker.updateTargetValueCount(count);

				out.write(delims.bodyRowEnd);
			}
		}
		state.finishEndElement(onTargetEnd);
	}

	protected int writeHeaders(Writer out, ErrorChecker checker, boolean doLastHeader,
			Set<Element> elements) throws IOException {

		int count = 0;
		
		Iterator<Element> iter = elements.iterator();
		while ( iter.hasNext() ) {
			Element element = iter.next();
			if ( ! element.hasChildren ) {// don't output elements with child elements
				String cellEnd = (doLastHeader && !iter.hasNext()) ? delims.lastHeaderCellEnd : delims.headerCellEnd;
				out.write(delims.headerCellStart + element.displayName + cellEnd);
				count++;
			}
		}
		
		return count;
	}
	protected int writeValues(Writer out, ErrorChecker checker, boolean doLastBody,
			Set<Element> elements, Map<String,String> values) throws IOException {
		
		int count = 0;
		Iterator<Element> iter = elements.iterator();
		while ( iter.hasNext() ) {
			Element element = iter.next();
			if ( ! element.hasChildren ) {
				// don't output elements with child elements
				String value = values.get(element.fullName);
				value = (value != null)? value: "";
				String cellEnd = (doLastBody && !iter.hasNext()) ? delims.lastBodyCellEnd : delims.bodyCellEnd;
				out.write(delims.bodyCellStart + formatSimple(value) +  cellEnd);
				count++;
			}
		}
		return count;
	}


	protected void updateQualifiedNames(Set<Element> ... columnLists) {

		boolean hasDuplicates = true;
		while (hasDuplicates) { //for (int i = 0; hasDuplicates && i < 10; i++) { //Use this way to make sure we don't infinite loop?
			hasDuplicates = false;
			Set<String> allDisplayNames = new HashSet<String>();
			Set<String> duplicates = new HashSet<String>();
			// First create a collection of the duplicates. We only care about the
			// childless ones, however.
			for (Set<Element> columnList: columnLists) {
				for (Element element: columnList) {
					if (!element.hasChildren) {
						boolean isUnique = allDisplayNames.add(element.displayName);
						if (!isUnique) {
							duplicates.add(element.displayName);
							hasDuplicates = true;
						}
					}
				}
			}

			// now go through and update the qualified name
			for (Set<Element> columnList: columnLists) {
				for (Element element: columnList) {
					if (!element.hasChildren) {
						if (duplicates.contains(element.displayName)) {
							element.addParentToDisplayName(); //displayName = URIUtils.parseQualifiedName(element.fullName, element.displayName);
						}
					}
				}
			}
		}
	}

	// ===============
	// UTILITY METHODS
	// ===============

	protected String formatSimple(String value) {
		if (value == null) {
			return "";
		}
		switch (outputType) {
		case TAB:
			value = XMLUtils.unEscapeXMLEntities(value);
			value = value.replaceAll("[\n\r]", ParseState.EMPTY_STRING); //Handles newlines and carriage returns, 
			// so we don't break lines in the middle of a row
			return value;
		case CSV:
			// Currently handles commas and quotes. May need to handle carriage
			// returns and tabs later?
			value = XMLUtils.unEscapeXMLEntities(value);
			boolean hasQuotes = value.indexOf('"') >= 0;
			boolean isDoEncloseInQuotes = (value.indexOf(',')>=0) || hasQuotes;
			if (hasQuotes) {
				Matcher matcher = quoteLiteral.matcher(value);
				value = matcher.replaceAll("\"\""); // escape quotes by doubling them
			}
			return (isDoEncloseInQuotes)?  '"' + value + '"': value;
		case XML: // same as excel
		case EXCEL:
			return XMLUtils.quickTagContentEscape(value);
		}
		return value; // by default
	}

}
