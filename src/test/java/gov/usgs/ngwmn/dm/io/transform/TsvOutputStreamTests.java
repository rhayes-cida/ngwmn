package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.dm.io.parse.DataRowParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.LineReader;


public class TsvOutputStreamTests {
	
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

	TsvOutputStream tsv;
	ByteArrayInputStream  inn;
	ByteArrayOutputStream out;
	DataRowParser psr;
	
	void makeTsv(String xml) throws Exception {
		out = new ByteArrayOutputStream(1000);
		tsv = new TsvOutputStream(out);
		psr = new DataRowParser();
		tsv.setParser(psr);
		inn = new ByteArrayInputStream( xml.getBytes() );
	}
	
	// I made this because there was a strange JUnit issue I had to t-shoot
	public static void main(String[] args) throws Exception {
		new TsvOutputStreamTests().test_transformTwoRowsOfSimplerXML();
	}

	@Test
	public void test_transformTwoRowsOfSimplerXML() throws Exception {
		String xml = "<get><Site>2172257</Site><Site>2172258</Site></get>";
		makeTsv(xml);

		ByteStreams.copy(inn,tsv);
		tsv.close();
		
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
		
		makeTsv(xml);
		psr.setKeepElderInfo(false);
		psr.setRowElementName("Site");
		
		ByteStreams.copy(inn,tsv);
		tsv.close();
		
		String result = new String(out.toByteArray());
		System.err.println(result);
		
		StringReader reader = new StringReader(result);
		LineReader   lines  = new LineReader(reader);
		String       line;
		
		line = lines.readLine();
		assertEquals("\"code\"\t\"agency\"\t\"Id\"\t\"level\"\t\"Type\"", line);
		
		line = lines.readLine();
		assertEquals("\"01\"\t\"\"\t\"2172257\"\t\"admin\"\t\"water\"", line);
		
		line = lines.readLine();
		assertEquals("\"02\"\t\"USGS2\"\t\"\"\t\"\"\t\"clay\"", line);
		
		line = lines.readLine();
		assertEquals("\"03\"\t\"\"\t\"2172258\"\t\"user\"\t\"stream\"", line);
	}
	
}
