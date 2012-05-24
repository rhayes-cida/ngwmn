package gov.usgs.ngwmn.dm.parse;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.junit.*;


public class DataFlatteningFormatterWithContentDefinedElementTest {
	protected LoggingPrintStream out;

	@Before
	public void setUp() throws Exception {
		out = new LoggingPrintStream();
		out.disable();
		// out.enable(); uncomment this line to print the output
	}

	@Test
	public void testContentDefinedElementTypeRepeated() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(false);
		formatter.addContentDefinedElement("Book", "type");
		formatter.setRowElementName("Collection");
		String testInput = "<Library><Collection><Book type='paper'>The Tipping Point</Book><Book type='ebook'>Seven Habits</Book></Collection></Library>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = DataFlatteningFormatterTest.replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Book-paper</b></td><td><b>Book-ebook</b></td></tr><tr><td>The Tipping Point</td><td>Seven Habits</td></tr></table>", output);
		// This was the old test, Revised 12/15/2009 --Sibley
//		assertEquals("<table><tr><td><b>Collection/Book-paper</b></td><td><b>Collection/Book-ebook</b></td></tr><tr><td>The Tipping Point</td><td>Seven Habits</td></tr></table>", output);
	}

	@Test
	public void testContentDefinedElementTypeSingle() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(false);
		formatter.addContentDefinedElement("Book", "type");
		formatter.setRowElementName("Collection");
		String testInput = "<Library><Collection><Book type='paper'>The Tipping Point</Book></Collection></Library>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = DataFlatteningFormatterTest.replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Book-paper</b></td></tr><tr><td>The Tipping Point</td></tr></table>", output);
	}

	@Test
	public void testContentDefinedElementTypeRepeatedWithChildren() throws Exception {
		DataFlatteningFormatter formatter = new DataFlatteningFormatter();
		formatter.setKeepElderInfo(true);
		formatter.setCopyDown(false);
		formatter.addContentDefinedElement("Book", "type");
		formatter.setRowElementName("Collection");
		String testInput = "<Library><Collection><Book type='paper'><title>The Tipping Point</title></Book><Book type='ebook'><title>Seven Habits</title></Book></Collection></Library>";
		XMLStreamReader inStream = USGS_StAXUtils.getXMLInputFactory()
				.createXMLStreamReader(new StringReader(testInput));
		formatter.dispatch(inStream, out);
		String output = DataFlatteningFormatterTest.replaceNewLine(out.getRecord());
		assertEquals("<table><tr><td><b>Book-paper/title</b></td><td><b>Book-ebook/title</b></td></tr><tr><td>The Tipping Point</td><td>Seven Habits</td></tr></table>", output);
	}
}
