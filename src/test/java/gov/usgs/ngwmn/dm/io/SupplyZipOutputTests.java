package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Test;

public class SupplyZipOutputTests {

	Specifier spec;

	@Before
	public void setUp() {
		spec = new Specifier("USGS","1234",WellDataType.LOG);
	}
	
	@Test
	public void test_createZipOutput_thenCompareOriginalToZipInput() throws Exception {
		
		ByteArrayOutputStream os    = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs = new SimpleSupplier<OutputStream>(os);
		SupplyZipOutput oz          = new SupplyZipOutput(outs);
		Supplier<OutputStream> ze   = oz.makeEntry( new SpecifierEntry(spec) );

		byte[] buffer = PipelineTest.makeBytes(10, 1);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer){
//			@Override
//			public int read(byte[] bytes) throws IOException {
//				int length = super.read(bytes);
//				for (int b=0; b<length; b++) {
//					System.out.print(bytes[b]);
//				}
//				System.out.println();
//				return length;
//			}
//			
//			@Override
//			public synchronized int read() {
//				int b = super.read();
//				System.out.println(b);
//				return b;
//			}
		};
		Supplier<InputStream> ins = new SimpleSupplier<InputStream>(bais);
		
		
		Pipeline pipe = new Pipeline(spec);
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(ze);
		pipe.setInvoker(new CopyInvoker());
		
		boolean threw = false;
		try {
			oz.begin();
			pipe.invoke();
			threw = false;
		} catch (Exception e) {
			oz.end(threw); // because it is a test i could have just passed in false but this shows the use contract
		}	
		
		ByteArrayOutputStream out = (ByteArrayOutputStream) outs.getSource();
		checkBytes( out.toByteArray() );
	}
	
	private void checkBytes(byte[] bytes) throws IOException {
		assertNotNull(bytes);
		
		byte[] buffer = new byte[10];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ZipInputStream zis = new ZipInputStream(bais);
		ZipEntry entry = zis.getNextEntry();
		
		assertTrue(entry.getName().startsWith(spec.getAgencyID()));
		
		int size = zis.read(buffer);
		
//		assertEquals(10, bytes.length);
		PipelineTest.checkBytes(buffer, 10, 1);
		
		size = zis.read(buffer);
		assertEquals(-1, size);
	}

}
