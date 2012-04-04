package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

public class PipelineTest extends Pipeline {

	@Test
	public void testClose() {
		String sample = "Hello";
		final InputStream is = new ByteArrayInputStream(sample.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		final OpCountOutputStream cos = new OpCountOutputStream(os);
		Invoker victim = new GenericInvoker();
		
		Pipeline pl = new Pipeline();
		pl.setInputSupplier(new SupplyInput() {
			@Override
			public InputStream get() throws IOException {
				return is;
			}
		});
		pl.setOutputSupplier(new SupplyOutput() {
			@Override
			public OutputStream get() throws IOException {
				return cos;
			}
		});
		pl.setInvoker(victim);
		try {
			pl.invoke();
			assertEquals("noted success", PipeStatistics.Status.DONE, pl.getStatistics().getStatus());
			assertEquals("stream closed", 1, cos.getCloseCt());
			assertEquals("contents", sample, os.toString());
			assertEquals("count", sample.length(), pl.getStatistics().getCount());
			assertEquals("write byte count", sample.length(), cos.getWriteByteCt());
		} catch (IOException ioe) {
			assertEquals("noted failure", PipeStatistics.Status.FAIL, pl.getStatistics().getStatus());
		}
	}

	@Test
	public void test_simpleChain()  throws Exception {
		
		SupplyOutput outs1 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		
		SupplyInput ins = new SupplyInput() {
			
			@Override
			public InputStream get() throws IOException {
				byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};
		
		Pipeline pipe = new Pipeline();
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(outs1);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.get();
		checkBytes( out.toByteArray() );
	}
	
	@Test
	public void test_uniTeeChain()  throws Exception {
		
		SupplyOutput outs1 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		SupplyOutput outs2 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		
		SupplyInput ins = new SupplyInput() {
			
			@Override
			public InputStream get() throws IOException {
				byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};
		
		Pipeline pipe = new Pipeline();
		pipe.setInputSupplier(ins);		
		pipe.setOutputSupplier(outs1);
		pipe.addOutputSupplier(outs2);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.get();
		checkBytes( out.toByteArray() );
		
		out = (ByteArrayOutputStream) outs2.get();
		checkBytes( out.toByteArray() );		
	}
	
	@Test
	public void test_doubleTeeChain()  throws Exception {
		
		SupplyOutput outs1 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		SupplyOutput outs2 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		
		SupplyOutput outs3 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		
		SupplyInput ins = new SupplyInput() {
			
			@Override
			public InputStream get() throws IOException {
				byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};
		
		Pipeline pipe = new Pipeline();
		pipe.setInputSupplier(ins);		
		pipe.setOutputSupplier(outs1);
		pipe.addOutputSupplier(outs2);
		pipe.addOutputSupplier(outs3);
		pipe.setInvoker(new CopyInvoker());
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.get();
		checkBytes( out.toByteArray() );
		
		out = (ByteArrayOutputStream) outs2.get();
		checkBytes( out.toByteArray() );		
		
		out = (ByteArrayOutputStream) outs3.get();
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
		
		SupplyOutput outs1 = new SupplyOutput() {
			ByteArrayOutputStream os = new ByteArrayOutputStream(10);
			
			@Override
			public OutputStream get() throws IOException {
				return os;
			}
		};
		
		SupplyInput ins = new SupplyInput() {
			
			@Override
			public InputStream get() throws IOException {
				byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
				ByteArrayInputStream is = new ByteArrayInputStream(buffer);
				return is;
			}
		};

		SupplyChain<OutputStream> chain = new SupplyChain<OutputStream>() {
			@Override
			public OutputStream get() throws IOException {
				OutputStream os = super.get();
				os = new FilterOutputStream(os) {
					@Override
					public void write(int b) throws IOException {
						super.write(b*2);
					}
				};
				return os;
			}
		};
		
		Pipeline pipe = new Pipeline();
		pipe.setInputSupplier(ins);
		pipe.setOutputSupplier(outs1);
		pipe.chainOutputSupplier(chain);
		pipe.setInvoker(new CopyInvoker());
		
		pipe.invoke();
		
		ByteArrayOutputStream out;
		
		out = (ByteArrayOutputStream) outs1.get();
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
