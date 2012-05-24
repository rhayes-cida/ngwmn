package gov.usgs.ngwmn.dm.parse;


import static org.junit.Assert.*;

import gov.usgs.ngwmn.dm.io.parse.USGS_StAXUtils;

import java.io.StringReader;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamReader;

import org.junit.*;
/**
 * @author ilinkuo
 *
 */
public class DataFlatteningFormatterTest {
	public static final String SIMPLE_EMPTY_FIRST_ROW = "<get>"
				+ "<Org code='USGS'>"
				+ "<Site><Identification code=''><agency code=''></agency></Identification><Type></Type></Site>"
				+ "<Site><Identification code='04'><agency code='03'></agency></Identification><Type>water</Type></Site>"
				+ "</Org>"
				+ "</get>";
	public static final String SINGLE_ROW_AMBIGUOUS_ATTRIBUTES = "<get>"
				+ "<Org>"
				+ "<Site><Identification code='04'><agency code='03'></agency></Identification><Type>water</Type></Site>"
				+ "</Org>"
				+ "</get>";
	public static final String SINGLE_ROW_AMBIGUOUS_COLUMNS = "<get>"
				+ "<Org>"
				+ "<Site><Type>01</Type><Identification><agency>USGS</agency><Type>02</Type></Identification><related><Identification><Type>03</Type><unique></unique></Identification></related></Site>"
				+ "</Org>"
				+ "</get>";
	public static final String TRIPLE_ROW_UNEVEN_DEPTH = "<get>"
				+ "<Org>"
				+ "<Site><Identification code='04'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
				+ "<Site><Identification code='05'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
				+ "</Org>"
				+ "<Org><OrgID>6</OrgID>"
				+ "<jinx>"
				+ "<Site><Identification code='06'><agency></agency><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
				+ "</jinx>"
				+ "</Org>"
				+ "</get>";
	public static final String TRIPLE_ROW_MULTIPLE_ORG = "<get>"
				+ "<Org><OrgID>5</OrgID>"
				+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
				+ "<Site><Identification code='02'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
				+ "</Org>"
				+ "<Org><OrgID>6</OrgID>"
				+ "<Site><Identification code='03'><agency></agency><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
				+ "</Org>"
				+ "</get>";
	public static final String DOUBLE_ROW_TWO_ELEMENTS_WITH_NEW_ELEMENTS = "<get>"
				+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
				+ "<Site><Identification code='02'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
				+ "</get>";
	public static final String DOUBLE_ROW_TWO_ELEMENTS_WITH_ATTRIBUTES = "<get>"
				+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
				+ "<Site><Identification code='02'><agency>USGS</agency></Identification><Type>water</Type></Site>"
				+ "</get>";
	public static final String DOUBLE_ROW_TWO_ELEMENTS_WITH_UNEQUAL_NESTING = "<get>"
				+ "<Site><Identification><agency></agency><Id>2172257</Id></Identification><Type>water</Type></Site>"
				+ "<Site><Identification><agency>USGS</agency></Identification><Type>water</Type></Site>"
				+ "</get>";
	public static final String DOUBLE_ROW_TWO_ELEMENTS_WITH_NESTING = "<get>"
				+ "<Site><Identification><agency>USGS</agency><Id>2172257</Id></Identification><Type>water</Type></Site>"
				+ "<Site><Identification><agency>USGS</agency><Id>2172258</Id></Identification><Type>water</Type></Site>"
				+ "</get>";
	public static final String DOUBLE_ROW_TWO_ELEMENTS = "<get><Site><Id>2172257</Id><Type>water</Type></Site><Site><Id>2172258</Id><Type>water</Type></Site></get>";
	public static final String SIMPLE_DOUBLE_ROW = "<get><Site>2172257</Site><Site>2172258</Site></get>";
	public static final String TOO_MANY_VALUES = "<get><Site><name>abc</name></Site><Site><id>def</id></Site></get>";
	
	public static Pattern newLinePattern = Pattern.compile("\n", Pattern.MULTILINE);
	public static String replaceNewLine(String original) {
		return newLinePattern.matcher(original).replaceAll("");
	}
	
	protected LoggingPrintStream out;
	
	@Before
	public void setUp() throws Exception {
		out = new LoggingPrintStream();
		out.disable();
		// out.enable(); uncomment this line to print the output
	}

	@Test
	public void testSingleRowSimple() throws Exception {
		DataFlatteningFormatter formatter = makeDFFormatter();
		String testInput = "<get><Site>2172257</Site></get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Site</b></td></tr><tr><td>2172257</td></tr></table>", output);
	}

	private DataFlatteningFormatter makeDFFormatter() {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setCopyDown(false)
			.isSilent = true;
		return formatter;
	}
	
	@Test
	public void testSingleRowSimpleUnescapeXMLEntitiesXML() throws Exception {
		IFormatter formatter = makeDFFormatter();
		String testInput = "<get><Site>2172257&lt;&gt;&amp;&quot;&apos;</Site></get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Site</b></td></tr><tr><td>2172257<>&\"'</td></tr></table>", output);
	}
	
	@Test
	public void testSingleRowSimpleUnescapeXMLEntitiesCSV() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.CSV, false);
		formatter.setCopyDown(false)
			.isSilent = true;
		String testInput = "<get><Site>2172257-&lt;-&gt;-&amp;-&quot;-&apos;-</Site></get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("Site\"2172257-<->-&-\"\"-'-\"", output);
	}
	
	@Test
	public void testSingleRowSimpleUnescapeXMLEntitiesTAB() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.TAB, false);
		formatter.setCopyDown(false)
			.isSilent = true;
		String testInput = "<get><Site>2172257-&lt;-&gt;-&amp;-&quot;-&apos;-</Site></get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("Site2172257-<->-&-\"-'-", output);
	}
	
	@Test
	public void testSingleRowSimpleUnescapeXMLEntitiesEXCEL() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.EXCEL, false);
		formatter.setCopyDown(false)
			.isSilent = true;
		String testInput = "<get><Site>2172257-&lt;-&gt;-&amp;-&quot;-&apos;-</Site></get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertTrue(output.contains("2172257-&lt;-&gt;-&amp;-\"-'-"));
	}
	
	@Test
	public void testDoubleRowSimple() throws Exception {
		IFormatter formatter = makeDFFormatter();
		String testInput = SIMPLE_DOUBLE_ROW;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Site</b></td></tr><tr><td>2172257</td></tr><tr><td>2172258</td></tr></table>", output);
	}

	@Test
	public void testDoubleRowTwoElements() throws Exception {
		IFormatter formatter = makeDFFormatter();
		String testInput = DOUBLE_ROW_TWO_ELEMENTS;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Id</b></td><td><b>Type</b></td></tr><tr><td>2172257</td><td>water</td></tr><tr><td>2172258</td><td>water</td></tr></table>", output);
	}
	
	@Test
	public void testDoubleRowTwoElementsWithNesting() throws Exception {
		IFormatter formatter = makeDFFormatter();
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_NESTING;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>agency</b></td><td><b>Id</b></td><td><b>Type</b></td></tr><tr><td>USGS</td><td>2172257</td><td>water</td></tr><tr><td>USGS</td><td>2172258</td><td>water</td></tr></table>", output);
	}

	@Test
	public void testDoubleRowTwoElementsWithNestingUnequal() throws Exception {
		IFormatter formatter = makeDFFormatter();
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_UNEQUAL_NESTING;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>agency</b></td><td><b>Id</b></td><td><b>Type</b></td></tr><tr><td></td><td>2172257</td><td>water</td></tr><tr><td>USGS</td><td></td><td>water</td></tr></table>", output);
	}

	@Test
	public void testDoubleRowTwoElementsWithAttributes() throws Exception {
		IFormatter formatter = makeDFFormatter();
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_ATTRIBUTES;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>02</td><td>USGS</td><td></td><td></td><td>water</td></tr></table>", output);

	}
	
	@Test
	public void testDoubleRowTwoElementsWithAttributesAsData() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.TAB, false);
		formatter.setCopyDown(false)
				.isSilent = true;
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_ATTRIBUTES;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals(
				"code	agency	Id	level	Type"
				+ "01		2172257	admin	water" 
				+ "02	USGS			water", output);
	}
	
	@Test
	public void testDoubleRowTwoElementsWithNewElements() throws Exception {
		IFormatter formatter = makeDFFormatter();		
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_NEW_ELEMENTS;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals(
				"<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr></table>", output);
	}
	
	@Test
	public void testDoubleRowTwoElementsWithNewElementsAsData() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.TAB, false);
		formatter.setCopyDown(false)
			.isSilent = true;
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_NEW_ELEMENTS;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		String expectedOutput="code	agency	Id	level	Type"
			+ "01		2172257	admin	water" 
			+ "02	USGS			water	yes	good";
		assertEquals(expectedOutput, output);
	}


	// THIS TEST NEEDS TO BE CHANGED
//	@Test
//	public void testDoubleRowTwoElementsRowDepth3() throws IOException {
//		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
//		formatter.setDepthLevel(3);
//		String testInput = "<get>"
//			+ "<Org><OrgID>5</OrgID>"
//			+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
//			+ "<Site><Identification code='02'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
//			+ "</Org>"
//			+ "<Org><OrgID>6</OrgID>"
//			+ "<Site><Identification code='03'><agency></agency><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
//			+ "</Org>"
//			+ "</get>";
//		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
//				.createXMLStreamReader(new StringReader(testInput));
//		formatter.dispatch(inStream, out);
//		String output = replaceNewLine(out.getRecord());
//		assertEquals("<table><tr><td><b>OrgID</b></td></tr><tr><td>5</td></tr><tr><td></td><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td></td><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>6</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td>03</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
//	}
	
	@Test
	public void testTripleRowTwoElementsUsingElementName() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site")
			.setKeepElderInfo(false)
			.setCopyDown(false)
			.isSilent = true;
		String testInput = TRIPLE_ROW_MULTIPLE_ORG;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>03</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}
	
	@Test
	public void testTripleRowTwoElementsUsingElementNameUnevenDepth() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site")
			.setKeepElderInfo(false)
			.setCopyDown(false)
			.isSilent = true;
		String testInput = TRIPLE_ROW_UNEVEN_DEPTH;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>04</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>05</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>06</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}

	/**
	 * Elements and attributes may share the same local name but appear under
	 * different contexts. In those cases, the DataFlattenningFormatter is
	 * supposed to add enough context to disambiguate the column names.
	 */
	@Test
	public void testSimpleDisambiguationOfColumnLabels() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site")
			.setCopyDown(false)
			.isSilent = true;
		String testInput = SINGLE_ROW_AMBIGUOUS_ATTRIBUTES;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Identification/code</b></td><td><b>agency</b></td><td><b>agency/code</b></td><td><b>Type</b></td></tr><tr><td>04</td><td></td><td>03</td><td>water</td></tr></table>", output);
	}
	
	/**
	 * Elements and attributes may share the same local name but appear under
	 * different contexts. In those cases, the DataFlattenningFormatter is
	 * supposed to add enough context to disambiguate the column names.
	 */
	@Test
	public void testDepthDisambiguationOfColumnLabels() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site")
			.setCopyDown(false)
			.isSilent = true;
		String testInput = SINGLE_ROW_AMBIGUOUS_COLUMNS;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Site/Type</b></td><td><b>agency</b></td><td><b>Site/Identification/Type</b></td><td><b>related/Identification/Type</b></td><td><b>unique</b></td></tr><tr><td>01</td><td>USGS</td><td>02</td><td>03</td><td></td></tr></table>", output);
	}

	/**
	 * If the first target element has completely empty content, then the
	 * DataFlattenningFormatter assumes it was used only to provide headers for
	 * labelling. Note that Parent content is ignored when determining whether
	 * header info is used
	 */
	@Test
	public void testSkipFirstTargetIfAllEmpty() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site")
			.setKeepElderInfo(false)
			.setCopyDown(false)
			.isSilent = true;
		String testInput = SIMPLE_EMPTY_FIRST_ROW;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Identification/code</b></td><td><b>agency</b></td><td><b>agency/code</b></td><td><b>Type</b></td></tr><tr><td>04</td><td></td><td>03</td><td>water</td></tr></table>", output);	
	}

	@Test
	public void testdispatch_TooManyTargetValues() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(DataFlatteningFormatter.DEFAULT_MIMETYPE, true);
		formatter.setRowElementName("Site")
			.setKeepElderInfo(false)
			.setCopyDown(false);
		String testInput = TOO_MANY_VALUES;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		try {
			formatter.dispatch(inStream, out);
			fail("Expected an exception");
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("Too many elder values"));
		}

	}
	
	@Test
	public void testdispatch_TooManyElderValues() {
		// TODO
	}
	

}
