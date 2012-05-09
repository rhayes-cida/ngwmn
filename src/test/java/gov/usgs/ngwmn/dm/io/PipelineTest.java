package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

public class PipelineTest extends Pipeline {

	public PipelineTest() {
		super(null);
	}
	
	@Test
	public void testClose() {
		String sample = "Hello";
		InputStream is           = new ByteArrayInputStream(sample.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OpCountOutputStream cos  = new OpCountOutputStream(os);
		Invoker invoker = new FileInputInvoker();
		
		Pipeline pl = new Pipeline(null);
		pl.setInputSupplier(  new SimpleSupplier<InputStream>(is) );
		pl.setOutputSupplier( new SimpleSupplier<OutputStream>(cos) );
		pl.setInvoker(invoker);
		try {
			pl.invoke();
			assertEquals("pipeline stats should indicate DONE", PipeStatistics.Status.DONE, pl.getStatistics().getStatus());
			assertEquals("output stream should be closed", 1, cos.getCloseCt());
			assertEquals("pipeline stats data count should match the original data", sample.length(), pl.getStatistics().getCount());
			assertEquals("write byte count should match the original data", sample.length(), cos.getWriteByteCt());
			assertEquals("output data should match input data", sample, os.toString());
		} catch (IOException ioe) {
			assertEquals("noted failure", PipeStatistics.Status.FAIL, pl.getStatistics().getStatus());
		}
	}

	@Test
	public void test_simpleChain()  throws Exception {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs1 = new SimpleSupplier<OutputStream>(os);
		
		byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		Supplier<InputStream> ins = new SimpleSupplier<InputStream>(is);
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(outs1);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.getSource();
		checkBytes( out.toByteArray() );
	}
	
	@Test
	public void test_uniTeeChain()  throws Exception {
		
		ByteArrayOutputStream os1 = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs1 = new SimpleSupplier<OutputStream>(os1);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs2 = new SimpleSupplier<OutputStream>(os2);

		byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		Supplier<InputStream> ins = new SimpleSupplier<InputStream>(is);
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);		
		pipe.setOutputSupplier(outs1);
		pipe.addOutputSupplier(outs2);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.getSource();
		checkBytes( out.toByteArray() );
		
		out = (ByteArrayOutputStream) outs2.getSource();
		checkBytes( out.toByteArray() );		
	}
	
	@Test
	public void test_doubleTeeChain()  throws Exception {
		
		ByteArrayOutputStream os1 = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs1 = new SimpleSupplier<OutputStream>(os1);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs2 = new SimpleSupplier<OutputStream>(os2);
		ByteArrayOutputStream os3 = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs3 = new SimpleSupplier<OutputStream>(os3);
		
		byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		Supplier<InputStream> ins = new SimpleSupplier<InputStream>(is);
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);		
		pipe.setOutputSupplier(outs1);
		pipe.addOutputSupplier(outs2);
		pipe.addOutputSupplier(outs3);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.getSource();
		checkBytes( out.toByteArray() );
		
		out = (ByteArrayOutputStream) outs2.getSource();
		checkBytes( out.toByteArray() );		
		
		out = (ByteArrayOutputStream) outs3.getSource();
		checkBytes( out.toByteArray() );		
	}
	
	private void checkBytes(byte bytes[]) {
		assertNotNull(bytes);
		assertEquals(10, bytes.length);
		byte n = 0;
		for (byte b : bytes) {
			assertEquals(n++,b);
		}
	}
	
	@Test
	public void test_SupplyChain()  throws Exception {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs1 = new SimpleSupplier<OutputStream>(os);

		byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		Supplier<InputStream> ins = new SimpleSupplier<InputStream>(is);
		
		SupplyChain<OutputStream> chain = new SupplyChain<OutputStream>() {
			@Override
			public OutputStream initialize() throws IOException {
				OutputStream os = super.initialize();
				os = new FilterOutputStream(os) {
					@Override
					public void write(int b) throws IOException {
						super.write(b*2);
					}
				};
				return os;
			}
		};
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(outs1);
		pipe.chainOutputSupplier(chain);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out = (ByteArrayOutputStream) outs1.getSource();
		checkDoubleBytes( out.toByteArray() );
	}

	private void checkDoubleBytes(byte bytes[]) {
		assertNotNull(bytes);
		assertEquals(10, bytes.length);
		byte n = 0;
		for (byte b : bytes) {
			assertEquals(2*n++,b);
		}
	}

}
