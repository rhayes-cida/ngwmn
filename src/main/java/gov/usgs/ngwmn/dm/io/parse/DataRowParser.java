package gov.usgs.ngwmn.dm.io.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class DataRowParser implements Parser {
	
	protected final ParseState          state;
	protected final List<Element>       headers;
	protected final Set<String>         ignoredAttributes;
	protected final Set<String>         ignoredElements;
	protected final Map<String, String> contentDefinedElements;
	protected final Map<String, String> constAdditionalCols;
	
	protected XMLStreamReader     reader;
//	protected long bytesRead;
	protected boolean eof;
	protected int rowCount;
	
	public DataRowParser() {
		state                  = new ParseState();
		ignoredAttributes      = new HashSet<String>();
		ignoredElements        = new HashSet<String>();
		headers				   = new LinkedList<Element>();
		contentDefinedElements = new HashMap<String, String>();
		constAdditionalCols    = new LinkedHashMap<String, String>();
	}
	
	public void setInputStream(InputStream is) {
		try {
			XMLInputFactory factory = StAXFactory.getXMLInputFactory();
			reader = factory.createXMLStreamReader(is);
		} catch (XMLStreamException e) {
			// TODO we might want this to be an IOExecption instead
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean done() {
		return eof;
	};
	
	/**
	 * Set true to keep the information from elder elements when flattening
	 * @param isKeepElder
	 * @return 
	 */
	public void setKeepElderInfo(boolean keepElder) {
		state.isKeepElders = keepElder;
	}
	public void setRowElementName(String name) {
		state.maxRowDepthLevel = 1000; // set the ROW_DEPTH_LEVEL so deep that it never triggers
		name = (name==null) ? "" : name;
		String names[] = name.split("\\|");
		List<String> ids = Arrays.asList(names);
		state.rowElementIds.clear();
		state.rowElementIds.addAll(ids);
	}
	public void setCopyDown(boolean copyDown) {
		state.isDoCopyDown = copyDown;
	}
	public void addIgnoreNames(Set<String> names) {
		if (names==null) return;
		ignoredElements.addAll(names);
	}
/*	
	@Override
	public long bytesParsed() {
		return bytesRead;
	}
*/
	public List<Element> headers() {
		if ( headers.isEmpty() ) {
			for (String constCol : constAdditionalCols.keySet()) {
				headers.add( new Element(constCol, constCol, constCol) );
			}
			for (Element element : state.targetColumnList) {
				if ( ! element.hasChildren && ! ignoredElements.contains(element.localName)) {
					headers.add( element );
				}
			}
		}
		return headers;
	}
	public Map<String, String> currentRow() {
		return state.targetColumnValues;
	}
	public Map<String, String> nextRow() throws IOException {
		boolean done = eof;
		
		try {
			state.targetColumnValues.clear();
			
			while ( ! done && reader.hasNext() ) {
				int event = reader.next();
				
//				if (event != XMLStreamConstants.END_DOCUMENT) {
//					updateBytes(event);
//				}
				
				switch (event) {
					case XMLStreamConstants.START_DOCUMENT:
						break; // no start document handling needed
					case XMLStreamConstants.START_ELEMENT:
						startElement(state);
						break;
					case XMLStreamConstants.CHARACTERS:
						state.putChars(reader.getText().trim());
						break;
					case XMLStreamConstants.ATTRIBUTE:
						// TODO may need to handle this later
						break;
					case XMLStreamConstants.END_ELEMENT:
						// this is where writing to the stream happens
						// before this it was all setup
						done = endElement(); // the end elements for elders will be handled on nextRow
						eof  = state.isContextEmpty();
						done = done || eof;
	//					endElement(in, out, state, checker);
						break;
					case XMLStreamConstants.END_DOCUMENT:
						done = eof = true;
						return null;
					// TODO no default
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (state.targetColumnValues.size()==0 && eof) {
			return null;
		}
		
		removeIngnoreElements();
		appendConstElements();
		rowCount++;

		// if currentRow is last Row then returns empty set
		return  currentRow();
	}

	public void removeIngnoreElements() {
		for (String name : ignoredElements) {
			state.targetColumnValues.remove(name);
		}
	}
	
	public void appendConstElements() {
		headers();
		for (String constCol : constAdditionalCols.keySet()) {
			state.targetColumnValues.put(constCol, constAdditionalCols.get(constCol));
		}
	}

	public void addConstColumn(String column, String value) {
		constAdditionalCols.put(column,value);
	}
	
/*	
	// TODO this is not accurate. I could not find access to accurate counts
	// this will miss XML headers and whitespace to name just a couple
	private void updateBytes(int event) {
		
		StringBuilder text = new StringBuilder();
		
		if ( reader.isCharacters() ) {
			text.append(reader.getText());
		} else {
			text.append('<');
			if ( reader.isEndElement() ) {
				text.append('/');
			}
			text.append(reader.getLocalName());
			if ( reader.isStartElement() ) {
				for (int a=0; a<reader.getAttributeCount(); a++) {
					text.append(' ')
						.append(reader.getAttributeLocalName(a))
						.append("=\"")
						.append(reader.getAttributeValue(a))
						.append('"');
				}
			}
			text.append('>');
		}
		bytesRead  += text.length();
	}
*/
	@SuppressWarnings("unchecked")
	private boolean endElement() {
		String localName = reader.getLocalName();
//		System.err.println("</"+localName+">");
		boolean onTargetEnd = state.isOnTargetRowStartOrEnd(localName);
		state.finishEndElement(onTargetEnd);

		if (onTargetEnd) {
			if (state.isProcessingHeaders) {		
				if (state.isKeepElders) {
					updateQualifiedNames(state.elderColumnList, state.targetColumnList);
				} else {
					updateQualifiedNames(state.targetColumnList);
				}
			}
			// if on target end but has no data then we will want to continue to the next record
			onTargetEnd &= state.hasTargetContent();
		}
		
		return onTargetEnd;
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
					if ( ! element.hasChildren ) { // looking for leaf nodes
						boolean isUnique = allDisplayNames.add(element.displayName);
						if ( ! isUnique ) {
							duplicates.add(element.displayName);
							hasDuplicates = true;
						}
					}
				}
			}

			// now go through and update the qualified name
			for (Set<Element> columnList: columnLists) {
				for (Element element: columnList) {
					if ( ! element.hasChildren ) { // looking for leaf nodes
						if (duplicates.contains(element.displayName)) {
							element.addParentToDisplayName(); //displayName = URIUtils.parseQualifiedName(element.fullName, element.displayName);
						}
					}
				}
			}
		}
	}
	
	protected void startElement(ParseState state) {
		String  localName   = reader.getLocalName();
//		System.err.println("<"+localName+">");
		String  displayName = state.startElementBeginUpdate(reader);

		if ( state.isTargetFound() && state.isInTarget ) {
			// PROCESS THE ELEMENT HEADERS
			// Read and record the column headers from the first row's elements.
			// Add columns for later rows, but they don't get headers because
			// we're streaming and can't go back to the column headers.
			state.addHeaderOrColumn(localName, displayName);
			processAttributeHeadersNamesValues(state.current(), state.targetColumnList, state.targetColumnValues);
			
		} else if ( state.isKeepElders && ! state.isInTarget ) {
			state.addElderHeaderOrColumn(localName);
			processAttributeHeadersNamesValues(state.current(), state.elderColumnList, state.elderColumnValues);
		}
	}
	
	protected void processAttributeHeadersNamesValues(String currentState, Set<Element> elements, Map<String,String> values) {
		
		String  localName            = reader.getLocalName();
		boolean isContentDefined     = isCurrentElementContentDefined();
		String  contentAttributeName = isContentDefined ? contentDefinedElements.get(localName) : null;
		
		// PROCESS/STORE ATTRIBUTE HEADERS AND NAME/VALUES
		for (int i=0; i<reader.getAttributeCount(); i++) {
			String attLocalName = reader.getAttributeLocalName(i);
			
			if ( ! ignoredAttributes.contains(attLocalName) 
			  && ! (isContentDefined && attLocalName.equals(contentAttributeName)) ) {
				String fullName = makeFullName(currentState, attLocalName);
				elements.add( new Element(fullName, attLocalName, null) );
				values.put(fullName, reader.getAttributeValue(i).trim());
			}
		}
	}
	protected boolean isCurrentElementContentDefined() {
		String localName = reader.getLocalName();
		String contentAttribute = contentDefinedElements.get(localName);
		if (contentAttribute != null) {
			// This is a content defined element only if it
			// matches the local name and has a corresponding attribute value;
			return reader.getAttributeValue(null, contentAttribute) != null;
		}
		return false;
	}
	protected String makeFullName(String context, String name) {
		return (context.length() > 0)? context + Element.SEPARATOR + name: name;
	}
}
