package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.spec.Specifier;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

public class FileInputInvokerTest {

	protected Invoker victim;
	protected Invoker getVictim() {
		return new FileInputInvoker();
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
		
		Pipeline pl = new Pipeline((Specifier)null);
		pl.setInputSupplier(new SimpleSupplier<InputStream>(is));
		pl.setOutputSupplier(new SimpleSupplier<OutputStream>(os));
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
