package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

public class GenericInvokerTest {

	protected Invoker victim;
	protected Invoker getVictim() {
		return new GenericInvoker();
	}
	
	@Before
	public void setup() {
		victim = getVictim();
	}
	
	@Test
	public void testInvoke() {
		String sample = "Hello";
		final InputStream is = new ByteArrayInputStream(sample.getBytes());
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		
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
				return os;
			}
		});
		pl.setInvoker(victim);
		try {
			pl.invoke();
			assertEquals("noted success", PipeStatistics.Status.DONE, pl.getStatistics().getStatus());
			assertTrue("stream closed", true);
			assertEquals("contents", sample, os.toString());
			assertEquals("count", sample.length(), pl.getStatistics().getCount());
			assertNotNull("elapsed time", pl.getStatistics().getElapsedMSec());
		} catch (IOException ioe) {
			assertEquals("noted failure", PipeStatistics.Status.FAIL, pl.getStatistics().getStatus());
		}
	}

}
