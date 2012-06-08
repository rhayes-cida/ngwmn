package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.SpecifierEntry;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Encoding;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.FilterOutputStream;
import java.io.OutputStream;

import org.junit.Test;

public class TransformSupplierTests {


	@Test(expected=RuntimeException.class)
	public void test_makeEntry_mustBeginBeforeMakeEntry() throws Exception {
		// because join will not return a new one each call

		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.CSV);
		
		Specifier spec = new Specifier("a","f",WellDataType.QUALITY);
		EntryDescription desc = new SpecifierEntry(spec);
		s.makeEntry(desc);

		assertTrue("Should have thrown an exception.", false);
	}
	
	
	@Test 
	public void test_makeEntry_returnsNewCsvEachCall() throws Exception {
		// because join will not return a new one each call

		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.CSV);
		s.begin();
		
		Specifier spec = new Specifier("a","f",WellDataType.QUALITY);
		EntryDescription desc = new SpecifierEntry(spec);
		Supplier<OutputStream> sup1 = s.makeEntry(desc);
		Supplier<OutputStream> sup2 = s.makeEntry(desc);

		assertTrue("Each call to makeEntry should return a new supplier", sup1 != sup2);
	}
	

	@Test
	public void test_makeEntry_returnsCsv() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		TransformSupplier s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.CSV);
		assertTrue("transformer encoding type should remain CSV", s.encoding   == Encoding.CSV);
		s.begin();
		
		Specifier spec = new Specifier("a","f",WellDataType.QUALITY);
		EntryDescription desc = new SpecifierEntry(spec);
		Supplier<OutputStream> sup = s.makeEntry(desc);

		assertTrue("Expecting " + TransformEntrySupplier.class.getName(), sup instanceof TransformEntrySupplier);
		
		TransformEntrySupplier ts = (TransformEntrySupplier) sup;
		assertTrue("transformer entry description should be set", ts.entryDesc == desc);
	}
	

	@Test
	public void test_makeEntry_extensionAsCsv() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.CSV);
		s.begin();
		
		Specifier spec = new Specifier("a","f",WellDataType.QUALITY);
		EntryDescription desc = new SpecifierEntry(spec);
		s.makeEntry(desc);
		assertTrue("makeEntry should override the file name with the encoding extension", 
				desc.baseName().endsWith(".csv"));
	}
	
	
	@Test
	public void test_returnNonTransformedOS() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.NONE);
		
		OutputStream result = s.begin();
		assertEquals("no encoding should just pass through the output stream", os, result);
	}
	@Test
	public void test_returnNonTransformed_nullEncode() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, null);
		
		OutputStream result = s.begin();
		assertEquals("null encoding should be protected as encoding " + Encoding.NONE, os, result);
	}

	@Test
	public void test_returnCsvTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.CSV);
		
		OutputStream result = s.begin();
		assertTrue("Expecting " + CsvOutputStream.class.getName(), result instanceof CsvOutputStream );
	}

	@Test
	public void test_returnTsvTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.TSV);
		
		OutputStream result = s.begin();
		assertTrue("Expecting " + TsvOutputStream.class.getName(), result instanceof TsvOutputStream );
	}

	// TODO this shows that it is not implemented yet
	@Test(expected=NotImplementedException.class)
	public void test_returnXlsxTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, WellDataType.LOG, Encoding.XLSX);
		
		s.begin();
		//assertTrue("Expecting " + XlsxOutputStream.class.getName(), result instanceof XlsxOutputStream );
	}
}
