package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.dm.io.parse.DataRowParser;
import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.PostParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.LineReader;


public class CsvOutputStreamTests {
	
	public static final String TRIPLE_ROW_MULTIPLE_ORG 
    = "<get>"
	+ "<Org><OrgID>5</OrgID>"
	+ "<Site><Identification code='01'><agency></agency><Id level='admin'>2172257</Id></Identification><Type>water</Type></Site>"
	+ "<Site><Identification code='02'><agency new='yes'>USGS2</agency></Identification><Type>clay</Type><Result>good</Result></Site>"
	+ "</Org>"
	+ "<Org><OrgID>6</OrgID>"
	+ "<Site><Identification code='03'><agency></agency><Id level='user'>2172258</Id></Identification><Type>stream</Type></Site>"
	+ "</Org>"
	+ "</get>";

	CsvOutputStream csv;
	ByteArrayInputStream  inn;
	ByteArrayOutputStream out;
	DataRowParser psr;
	
	void makeCsv(String xml) throws Exception {
		PostParser pp = new PostParser() {
			
			@Override
			public List<Element> refineHeaderColumns(Collection<Element> headers) {
				return new LinkedList<Element>(headers);
			}
			
			@Override
			public void refineDataColumns(Map<String, String> data) {
				// do nothing
			}
			
			@Override
			public void addConstColumn(String col, String string) {
				// do nothing
			}
			
			@Override
			public Set<String> getRemoveColumns() {
				throw new RuntimeException("should not be called during this test");
			}
		};
		
		out = new ByteArrayOutputStream(1000);
		csv = new CsvOutputStream(out);
		psr = new DataRowParser(pp);
		csv.setParser(psr);
		inn = new ByteArrayInputStream( xml.getBytes() );
	}
	
	// I made this because there was a strange JUnit issue I had to t-shoot
	public static void main(String[] args) throws Exception {
		new CsvOutputStreamTests().test_transformTwoRowsOfSimplerXML();
	}

	@Test
	public void test_transformTwoRowsOfSimplerXML() throws Exception {
		String xml = "<get><Site>2172257</Site><Site>2172258</Site></get>";
		makeCsv(xml);

		ByteStreams.copy(inn,csv);
		csv.close();
		
		String result = new String(out.toByteArray());
		System.err.println(result);

		StringReader reader = new StringReader(result);
		LineReader   lines  = new LineReader(reader);
		String       line;
		
		line = lines.readLine();
		assertEquals("\"Site\"", line);
		
		line = lines.readLine();
		assertEquals("\"2172257\"", line);
		
		line = lines.readLine();
		assertEquals("\"2172258\"", line);
	}
	
	@Test
	public void test_transformThreeRowsOfComplexXML() throws Exception {
		String xml = TRIPLE_ROW_MULTIPLE_ORG;
		
		makeCsv(xml);
		psr.setKeepElderInfo(false);
		psr.setRowElementName("Site");
		
		ByteStreams.copy(inn,csv);
		csv.close();
		
		String result = new String(out.toByteArray());
		System.err.println(result);
		
		StringReader reader = new StringReader(result);
		LineReader   lines  = new LineReader(reader);
		String       line;
		
		line = lines.readLine();
		assertEquals("\"code\",\"agency\",\"Id\",\"level\",\"Type\"", line);
		
		line = lines.readLine();
		assertEquals("\"01\",\"\",\"2172257\",\"admin\",\"water\"", line);
		
		line = lines.readLine();
		assertEquals("\"02\",\"USGS2\",\"\",\"\",\"clay\"", line);
		
		line = lines.readLine();
		assertEquals("\"03\",\"\",\"2172258\",\"user\",\"stream\"", line);
	}
	
}
