package gov.usgs.ngwmn.dm.io.parse;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.stream.XMLStreamReader;

public class ParseState {
	
	public static final String        EMPTY_STRING            = "";
	public static final int           DEFAULT_ROW_DEPTH_LEVEL = 3;
	public static final String        NONSENSE_ROW_ELEMENT_IDENTIFIER = "!";
	
	// row state fields
	public boolean isInTarget;
	public boolean isProcessingHeaders;
	public boolean isKeepElders;             // true if elder element information is used to determine nestings
	public boolean isDoCopyDown;
	public boolean ignoreRowElement;         // false for backward compatibility
	public int     maxRowDepthLevel;
	public String  rowElementIdentifier;	// nonsense value by default so nothing can be matched
	
	// output fields - final protects them from being null
	public final Set<Element> targetColumnList;
	public final Set<Element> elderColumnList;
	public final Map<String, String> targetColumnValues;
	public final Map<String, String> elderColumnValues;
	public final Map<String, String> contentDefinedElements;
	

	// context tracks the parsing depth within the document
	private Stack<String> context; // TODO java.util.Stack has well documented issues
	private String targetElementContext;

	private boolean hasEncounteredTargetContent;
	private int row; // number of target row elements encountered
	
	/**
	 * Tracks the currently open target element until a child of that element is encountered
	 */
	private Element currentTargetColumn;
	/**
	 * Tracks the currently open elder element until a child of that element
	 * is encountered. An elder is an ancestor, uncle, or preceding sibling of a
	 * target element.
	 */
	private Element currentElder;


	/**
	 * @param dataFlatteningFormatter
	 */
	public ParseState() {
		isDoCopyDown           = true;
		targetElementContext   = EMPTY_STRING;
		maxRowDepthLevel       = DEFAULT_ROW_DEPTH_LEVEL;
		rowElementIdentifier   = NONSENSE_ROW_ELEMENT_IDENTIFIER;
		targetColumnList       = new LinkedHashSet<Element>();
		elderColumnList        = new LinkedHashSet<Element>();
		targetColumnValues     = new HashMap<String, String>();
		elderColumnValues      = new HashMap<String, String>();
		contentDefinedElements = new HashMap<String, String>();
		
		// initialize the context stack to avoid empty stack errors
		context = new Stack<String>();
		context.push("");
	}


	// --------------------
	// STATE UPDATE METHODS
	// --------------------
	/**
	 * Updates the state at the beginning of a StAX StartElement event. In
	 * addition, returns the display name of the element if it is not the
	 * same as the local name.
	 *
	 * @param localName
	 */
	public String startElementBeginUpdate(XMLStreamReader in) {
		String localName   = in.getLocalName();
		String displayName = localName;
		if ( isCurrentElementContentDefined(in) ) {
			displayName = localName + "-" + in.getAttributeValue(null, contentDefinedElements.get(localName))
			.replace(Element.SEPARATOR, '-');
		}
		// Bookkeeping: the top of the context stack always points to the current element
		String contextName = makeFullName(current(), displayName);
		context.push(contextName);
		if (isOnTargetRowStartOrEnd(localName)) {
			// "Correct" the context because we want to use shorter contexts
			// while within the target. This makes the within target context
			// a *relative* one, which allows us to properly handle the target
			// if it occurs at different places or different levels within
			// the document. Using the absolute context fails to handle
			// uneven hierarchies correctly.
			context.pop();
			context.push(localName);

			// Nonetheless, we want to remember the target element. In this
			// case, we use the full context rather than the abbreviated
			// one.
			targetElementContext = contextName;
			row++;

			// Reset the row parsing status
			isInTarget = true;
			isProcessingHeaders = isFirstRow();
			targetColumnValues.clear();
		}
		// only return displayName if it's actually different
		return (displayName.equals(localName))? null: displayName;
	}
	
	public boolean isCurrentElementContentDefined(XMLStreamReader in) {
		String localName = in.getLocalName();
		String contentAttribute = contentDefinedElements.get(localName);
		if (contentAttribute != null) {
			// This is a content defined element only if it
			// matches the local name and has a corresponding attribute value;
			return in.getAttributeValue(null, contentAttribute) != null;
		}
		return false;
	}

	/**
	 * Updates the state at the end of a StAX EndElement event
	 * @param onTargetEnd
	 */
	public void finishEndElement(boolean onTargetEnd) {
		String current = context.pop();
		boolean isElderElement = !isInTarget;
		if (isElderElement && isKeepElders) {
			if (isDoCopyDown) {
				clearAncestralDescendants(current);
			}
			// now backtracking. currentElder only tracks going down.
			currentElder = null;
		} else { // not a elder, in the target "row"
			currentTargetColumn = null;
		}
		if (onTargetEnd) {
			isInTarget = false; // exiting target
		}
	}

	// ----------------
	// STATE INDICATORS
	// ----------------
	public boolean isOnTargetRowStartOrEnd(String localName) {
		return context.size() == maxRowDepthLevel 
			|| rowElementIdentifier.equals(localName);
	}

	/**
	 * Returns true if at least one target has been found. The difference
	 * between isTargetFound() and hasEncounteredContent is that the the
	 * first is true immediately upon encountering the start tag and that
	 * the latter becomes true only when nonempty tag content or attribute
	 * content within the target is encountered.
	 *
	 * @return
	 */
	public boolean isTargetFound() {
		return (row > 0);
	}

	// ---------------
	// SERVICE METHODS
	// ---------------
	/**
	 * Convenience method for creating an element.
	 * @param localName
	 * @param displayName
	 * @return
	 */
	private Element makeElement(String localName, String displayName) {
		return new Element(current(), localName, displayName);
	}

	/**
	 * Adds a target header or column. Should only be called within a target
	 * @param localName
	 * @param displayName
	 */
	public void addHeaderOrColumn(String localName, String displayName) {
		if (isInTarget) {
			Element element = makeElement(localName, displayName);
			boolean isNew = targetColumnList.add(element);
			// Note that the previous column element has child tags so shouldn't
			// be output as data flattening isn't set to deal with document
			// style xml.
			if (isNew) {
				applyHasChildren(currentElder, true);
				applyHasChildren(currentTargetColumn, true);
				currentTargetColumn = element; // update

				// special case
				if (ignoreRowElement && localName.equals(rowElementIdentifier)) {
					element.hasChildren = true;
				}
			}
		}
	}

	private void applyHasChildren(Element element, boolean value) {
		if (element != null) {
			element.hasChildren = true;
		}
	}
	
	/**
	 * Adds an elder target header or column. Should only be called before a
	 * target is ever found. We can't deal with nontarget columns whose
	 * first appearance is after a target.
	 *
	 * @param localName
	 */
	public void addElderHeaderOrColumn(String localName) {
		if ( ! isTargetFound() ) {
			if (isKeepElders) {
				// TODO need to see if this needs a display name, and test
				Element element = makeElement(localName, null);
				boolean isNew = elderColumnList.add(element);
				// Note that the previous column element has child tags so shouldn't
				// be output as data flattening isn't set to deal with document
				// style xml.
				if (isNew) {
					applyHasChildren(currentElder, true);
					currentElder = element; // update
				}
			}
		}
	}

	public boolean hasTargetContent() {
		for (String value: targetColumnValues.values()) {
			if (hasEncounteredTargetContent) return hasEncounteredTargetContent;
			hasEncounteredTargetContent = (value != null && value.length() > 0);
		}
		return hasEncounteredTargetContent;
	}

	/**
	 * Clears all descendant values of an ancestor of the target, excluding
	 * the target itself.
	 *
	 * @param fullName
	 */
	private void clearAncestralDescendants(String fullName) {
		// it's not a direct ancestor of the target, so don't do anything
		if ( ! Element.isAncestorOf(fullName, targetElementContext) )  return;

		boolean isAncestorFound = false;
		for (Element elderColumn: elderColumnList) {
			String elementName = elderColumn.fullName;
			// Make use of the fact that elderColumnList is an ordered set
			// ordered by document order to know that once you've found an
			// ancestor, you can delete everything after it.
			if (isAncestorFound || Element.isAncestorOf(fullName, elementName)) {
				isAncestorFound = true;
				elderColumnValues.remove(elementName);
			}
		}
	}

	/**
	 * Store the character text using the current element name as key
	 * @param value
	 */
	public void putChars(String value) {
		if (isInTarget) {
			targetColumnValues.put(current(), value);
		} else if (isKeepElders) {
			elderColumnValues.put(current(), value);
		}
	}


	public String current() {
		return context.peek();
	}

	public boolean isFirstRow() {
		return row == 1;
	}
	
	public String makeFullName(String context, String name) {
		return (context.length() > 0)? context + Element.SEPARATOR + name: name;
	}


	public String pop() {
		return context.pop();
	}


}