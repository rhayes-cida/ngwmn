package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.spec.Specifier;

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
		final InputStream is = new ByteArrayInputStream(sample.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		final OpCountOutputStream cos = new OpCountOutputStream(os);
		Invoker invoker = new FileInputInvoker();
		
		Pipeline pl = new Pipeline(null);
		pl.setInputSupplier(new Supplier<InputStream>() {
			@Override
			public InputStream makeSupply(Specifier spec) throws IOException {
				return is;
			}
		});
		pl.setOutputSupplier(new Supplier<OutputStream>() {
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				return cos;
			}
		});
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
		
		Supplier<OutputStream> outs1 = new Supplier<OutputStream>() {
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
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(outs1);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.begin(null);
		checkBytes( out.toByteArray() );
	}
	
	@Test
	public void test_uniTeeChain()  throws Exception {
		
		Supplier<OutputStream> outs1 = new Supplier<OutputStream>() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				return os;
			}
		};
		Supplier<OutputStream> outs2 = new Supplier<OutputStream>() {
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
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);		
		pipe.setOutputSupplier(outs1);
		pipe.addOutputSupplier(outs2);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.begin(null);
		checkBytes( out.toByteArray() );
		
		out = (ByteArrayOutputStream) outs2.begin(null);
		checkBytes( out.toByteArray() );		
	}
	
	@Test
	public void test_doubleTeeChain()  throws Exception {
		
		Supplier<OutputStream> outs1 = new Supplier<OutputStream>() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				return os;
			}
		};
		Supplier<OutputStream> outs2 = new Supplier<OutputStream>() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				return os;
			}
		};
		
		Supplier<OutputStream> outs3 = new Supplier<OutputStream>() {
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
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};
		
		Pipeline pipe = new Pipeline(null);
		pipe.setInputSupplier(ins);		
		pipe.setOutputSupplier(outs1);
		pipe.addOutputSupplier(outs2);
		pipe.addOutputSupplier(outs3);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.begin(null);
		checkBytes( out.toByteArray() );
		
		out = (ByteArrayOutputStream) outs2.begin(null);
		checkBytes( out.toByteArray() );		
		
		out = (ByteArrayOutputStream) outs3.begin(null);
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
		
		Supplier<OutputStream> outs1 = new Supplier<OutputStream>() {
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
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};

		SupplyChain<OutputStream> chain = new SupplyChain<OutputStream>() {
			@Override
			public OutputStream makeSupply(Specifier spec) throws IOException {
				OutputStream os = super.makeSupply(spec);
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
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.begin(null);
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
