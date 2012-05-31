package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Encoding;

import java.io.FilterOutputStream;
import java.io.OutputStream;

import org.junit.Test;

public class TransformSupplierTests {

	@Test
	public void test_returnNonTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, Encoding.NONE);
		
		OutputStream result = s.begin();
		assertEquals(os, result);
	}

	@Test
	public void test_returnCsvTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, Encoding.CSV);
		
		OutputStream result = s.begin();
		assertTrue( result instanceof CsvOutputStream );
	}

	@Test
	public void test_returnTsvTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, Encoding.TSV);
		
		OutputStream result = s.begin();
		assertTrue( result instanceof TsvOutputStream );
	}

	// TODO this shows that it is not implemented yet
	@Test(expected=NotImplementedException.class)
	public void test_returnXlsxTransformer() throws Exception {
		OutputStream os = new FilterOutputStream(null);
		Supplier<OutputStream> upstream = new SimpleSupplier<OutputStream>(os);
		Supplier<OutputStream> s = new TransformSupplier(upstream, Encoding.XLSX);
		
		s.begin();
	}
}
