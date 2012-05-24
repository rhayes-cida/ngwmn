package gov.usgs.ngwmn.dm.parse;

import static gov.usgs.ngwmn.dm.parse.DataFlatteningFormatterTest.*;
import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.junit.*;



public class DataFlatteningFormatterWithParentsTest {
	
	protected LoggingPrintStream out;
	
	@Before
	public void setUp() throws Exception {
		out = new LoggingPrintStream();
		out.disable();
		// out.enable(); uncomment this line to print the output
	}

	@Test
	public void testSingleRowSimple() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = "<get><Site>2172257</Site></get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Site</b></td></tr><tr><td>2172257</td></tr></table>", output);
	}

	private DataFlatteningFormatter makeDFFormatter(String rowElement) {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		if (rowElement != null) formatter.setRowElementName(rowElement);
		formatter.setKeepElderInfo(true)
			.setCopyDown(false)
			.isSilent = true;
		return formatter;
	}

	@Test public void testDoubleRowSimple() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = SIMPLE_DOUBLE_ROW;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Site</b></td></tr><tr><td>2172257</td></tr><tr><td>2172258</td></tr></table>", output);
	}

	@Test public void testDoubleRowTwoElements() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = DOUBLE_ROW_TWO_ELEMENTS;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Id</b></td><td><b>Type</b></td></tr><tr><td>2172257</td><td>water</td></tr><tr><td>2172258</td><td>water</td></tr></table>", output);
	}

	@Test public void testDoubleRowTwoElementsWithNesting() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_NESTING;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>agency</b></td><td><b>Id</b></td><td><b>Type</b></td></tr><tr><td>USGS</td><td>2172257</td><td>water</td></tr><tr><td>USGS</td><td>2172258</td><td>water</td></tr></table>", output);
	}

	@Test public void testDoubleRowTwoElementsWithNestingUnequal() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_UNEQUAL_NESTING;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>agency</b></td><td><b>Id</b></td><td><b>Type</b></td></tr><tr><td></td><td>2172257</td><td>water</td></tr><tr><td>USGS</td><td></td><td>water</td></tr></table>", output);
	}

	@Test public void testDoubleRowTwoElementsWithAttributes() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_ATTRIBUTES;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>02</td><td>USGS</td><td></td><td></td><td>water</td></tr></table>", output);

	}
	
	@Test public void testDoubleRowTwoElementsWithAttributesAsData() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.TAB, false);
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(false);
		formatter.isSilent = true;
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
	
	@Test public void testDoubleRowTwoElementsWithNewElements() throws Exception {
		IFormatter formatter = makeDFFormatter(null);
		String testInput = DOUBLE_ROW_TWO_ELEMENTS_WITH_NEW_ELEMENTS;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals(
				"<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr></table>", output);
	}
	
	@Test public void testDoubleRowTwoElementsWithNewElementsAsData() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter(MimeType.TAB, false);
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(false);
		formatter.isSilent = true;
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
	
	@Test public void testTripleRowTwoElementsUsingElementNameUnevenDepth() throws Exception {
		IFormatter formatter = makeDFFormatter("Site");
		String testInput = TRIPLE_ROW_UNEVEN_DEPTH;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>04</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>05</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>06</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}


	@Test public void testTripleRowTwoElementsUsingElementName() throws Exception {
		IFormatter formatter = makeDFFormatter("Site");
		String testInput = "<get>"
			+ "<Org><OrgID>5</OrgID><Address>10 Downing St</Address>"
			+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
			+ "<Site><Identification code='02'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
			+ "</Org>"
			+ "<Org><OrgID>6</OrgID>"
			+ "<Site><Identification code='03'><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
			+ "</Org>"
			+ "</get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>OrgID</b></td><td><b>Address</b></td><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>5</td><td>10 Downing St</td><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td></td><td></td><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>6</td><td></td><td>03</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}
	
	@Test public void testAdditionalParentItemsAfterFirst() throws Exception {
		IFormatter formatter = makeDFFormatter("Site");
		String testInput = "<get>"
			+ "<Org><OrgID>5</OrgID><Address>10 Downing St</Address>"
			+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
			+ "<Site><Identification code='02'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
			+ "</Org>"
			+ "<Org><OrgID>6</OrgID><Phone>123-456-7890</Phone><status>gov</status><Address>1000 Penn Ave.</Address>"
			+ "<Site><Identification code='03'><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
			+ "</Org>"
			+ "</get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>OrgID</b></td><td><b>Address</b></td><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>5</td><td>10 Downing St</td><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td></td><td></td><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>6</td><td>1000 Penn Ave.</td><td>03</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}


	@Test public void testSkipFirstTargetIfAllEmpty() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site");
		// hm, why was elder info skipped in this test? TODO figure out later
		formatter.setCopyDown(false);
		formatter.isSilent = true;
		String testInput = SIMPLE_EMPTY_FIRST_ROW;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
		.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Identification/code</b></td><td><b>agency</b></td><td><b>agency/code</b></td><td><b>Type</b></td></tr><tr><td>04</td><td></td><td>03</td><td>water</td></tr></table>", output);
	}

	
}
