package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.executor.ExecFactory;
import gov.usgs.ngwmn.dm.io.executor.Executee;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PipelineAggregateTests {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	List<Specifier> specs;

	@Rule
	public MethodRule watchman = new TestWatchman() {
	    public void starting(FrameworkMethod method) {
	    	logger.info("{} being run...", method.getName());
	    }
	};

	@Before
	public void setUp() {
		specs = new ArrayList<Specifier>();
		specs.add( new Specifier("USGS","1234",WellDataType.WATERLEVEL) );
		specs.add( new Specifier("USGS","1235",WellDataType.WATERLEVEL) );
		
//		ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger) logger;
//		log.setLevel(Level.DEBUG);
	}
	
	@Test
	public void test_createAggregate_thenCompareOriginalToZipInput() throws Exception {
		
		ByteArrayOutputStream os    = new ByteArrayOutputStream(10);
		Supplier<OutputStream> outs = new SimpleSupplier<OutputStream>(os);
		SupplyZipOutput oz          = new SupplyZipOutput(outs);

		ExecFactory fac = new ExecFactory() {
			int makeCount = 0;
			
			@Override
			public Executee makeExecutor(Specifier spec, Supplier<OutputStream> out)
					throws IOException {
				makeCount++;

				byte buffer[] = new byte[]{0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer){
					@Override
					public int read(byte[] bytes) throws IOException {
						System.out.println();
						int length = super.read(bytes);
						for (int b=0; b<length; b++) {
							bytes[b]*=makeCount;
							System.out.print(bytes[b]);
							System.out.print(" ");
						}
						System.out.println();
						return length;
					}
					
					@Override
					public synchronized int read() {
						int b = makeCount * super.read();
						System.out.println(b + " ");
						return b;
					}
				};
				Supplier<InputStream> ins  = new SimpleSupplier<InputStream>(bais);
				
				Supplier<OutputStream> oes = out.makeEntry(spec);
				
				Pipeline pipe = new Pipeline(spec);
				pipe.setOutputSupplier(oes);
				pipe.setInputSupplier(ins);
				
				return pipe;
			}
		};
		
		
		PipelineAggregate pipe = new PipelineAggregate(fac,specs,oz);

		pipe.invoke();
		
		ByteArrayOutputStream out = (ByteArrayOutputStream) outs.getSource();
		checkBytes( out.toByteArray() );
	}
	
	private void checkBytes(byte bytes[]) throws IOException {
		assertNotNull(bytes);
		
		byte buffer[] = new byte[10];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ZipInputStream zis = new ZipInputStream(bais);
		
		int count = 0;
		for (Specifier spec : specs) {
			count++;
			ZipEntry entry = zis.getNextEntry();
			
			assertTrue(entry.getName().startsWith(spec.getAgencyID()));
			
			int size = zis.read(buffer);
			assertEquals(10, size);
			
	//		assertEquals(10, bytes.length);
			byte n = 0;
			for (byte b : buffer) {
				assertEquals(count*n++,b);
			}
		
			size = zis.read(buffer);
			assertEquals(-1, size);
		}
	}

}
