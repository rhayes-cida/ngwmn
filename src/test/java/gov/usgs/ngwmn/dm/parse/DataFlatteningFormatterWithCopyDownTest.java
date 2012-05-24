package gov.usgs.ngwmn.dm.parse;

import static gov.usgs.ngwmn.dm.parse.DataFlatteningFormatterTest.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.junit.*;

public class DataFlatteningFormatterWithCopyDownTest {

	protected LoggingPrintStream out;
	
	@Before
	public void setUp() throws Exception {
		out = new LoggingPrintStream();
		out.disable();
		// out.enable(); uncomment this line to print the output
	}

	public void testDoubleRowSimple() throws IOException {
		// skip
	}

	@Test
	public void testDoubleRowTwoElements() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site");
		formatter.setCopyDown(true);
		String testInput = TRIPLE_ROW_UNEVEN_DEPTH;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>04</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>05</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>06</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}

	@Test
	public void testTripleRowTwoElementsUsingElementName() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site");
		formatter.setKeepElderInfo(false); // will be auto set to true with setCopyDown() call
		formatter.setCopyDown(true);
		String testInput = TRIPLE_ROW_MULTIPLE_ORG;
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>OrgID</b></td><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>5</td><td>01</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>5</td><td>02</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td>6</td><td>03</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);

	}
	
	@Test
	public void testTripleRowUnevenLargeParent() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site");
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(true);
		String testInput = "<get>"
			+ "<Org><contact phone='1234'><address><city>SF</city><state>CA</state></address></contact>"
			+ "<Site><Identification code='04'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
			+ "<Site><Identification code='05'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
			+ "</Org>"
			+ "<Org><OrgID>6</OrgID>"
			+ "<jinx>"
			+ "<Site><Identification code='06'><agency></agency><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
			+ "</jinx>"
			+ "</Org>"
			+ "</get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>phone</b></td><td><b>city</b></td><td><b>state</b></td><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>1234</td><td>SF</td><td>CA</td><td>04</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>1234</td><td>SF</td><td>CA</td><td>05</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td></td><td></td><td></td><td>06</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}
	
	@Test
	public void testTripleRowMultipleOrg() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setRowElementName("Site");
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(true);
		String testInput = "<get>"
			+ "<Org><contact phone='1234'><address><city>SF</city><state>CA</state></address></contact>"
			+ "<Site><Identification code='04'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
			+ "<Site><Identification code='05'><agency new='yes'>USGS</agency></Identification><Type>water</Type><Result>good</Result></Site>"
			+ "</Org>"
			+ "<Org><OrgID>6</OrgID>"
			+ "<Site><Identification code='06'><agency></agency><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
			+ "</Org>"
			+ "</get>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>phone</b></td><td><b>city</b></td><td><b>state</b></td><td><b>code</b></td><td><b>agency</b></td><td><b>Id</b></td><td><b>level</b></td><td><b>Type</b></td></tr><tr><td>1234</td><td>SF</td><td>CA</td><td>04</td><td></td><td>2172257</td><td>admin</td><td>water</td></tr><tr><td>1234</td><td>SF</td><td>CA</td><td>05</td><td>USGS</td><td></td><td></td><td>water</td><td>yes</td><td>good</td></tr><tr><td></td><td></td><td></td><td>06</td><td></td><td>2172258</td><td>user</td><td>stream</td><td></td><td></td></tr></table>", output);
	}


}
