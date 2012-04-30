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
		spec = new Specifier("USGS","1234",WellDataType.ALL);
	}
	
	@Test
	public void test_createZipOutput_thenCompareOriginalToZipInput() throws Exception {
		Supplier<OutputStream> outs = new Supplier<OutputStream>() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				return os;
			}
		};
		
		Supplier<InputStream> ins = new Supplier<InputStream>() {
			
			@Override
			public InputStream makeSupply(Specifier spec) throws IOException {
				byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer){
					@Override
					public int read(byte[] bytes) throws IOException {
						int length = super.read(bytes);
						for (int b=0; b<length; b++) {
							System.out.print(bytes[b]);
						}
						System.out.println();
						return length;
					}
					
					@Override
					public synchronized int read() {
						int b = super.read();
						System.out.println(b);
						return b;
					}
				};
				
				return bais;
			}
		};
		
		SupplyChain<OutputStream> chain = new SupplyChain<OutputStream>() {
			SupplyZipOutput oz;
			
			@Override
			public void setSupply(Supplier<OutputStream> supply) {
				oz = new SupplyZipOutput(supply);
				super.setSupply(oz);
			}
			
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				return oz.begin(spec);
			}
		};
		
		Pipeline pipe = new Pipeline(spec);
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(outs);
		pipe.chainOutputSupplier(chain);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		chain.end(false); // this is not helping but it does close the entry and stream
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs.begin(null);
		checkBytes( out.toByteArray() );
	}
	
	private void checkBytes(byte bytes[]) throws IOException {
		assertNotNull(bytes);
		
		byte buffer[] = new byte[10];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ZipInputStream zis = new ZipInputStream(bais);
		ZipEntry entry = zis.getNextEntry();
		
		assertTrue(entry.getName().startsWith(spec.getAgencyID()));
		
		int size = zis.read(buffer);
		assertEquals(10, size);
		
//		assertEquals(10, bytes.length);
		byte n = 0;
		for (byte b : buffer) {
			assertEquals(n++,b);
		}
		
		size = zis.read(buffer);
		assertEquals(-1, size);
	}

}
