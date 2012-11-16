package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import org.junit.Test;

public class DirectCSVOutputStreamTest {

	@Test
	public void testSample() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DirectCSVOutputStream victim = new DirectCSVOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		victim.setAgency("USGS");
		victim.setSite("20515416303801");
		victim.setElevation(99.999);
		InputStream tis = getClass().getResourceAsStream("/sample-data/USGS_20515416303801_WATERLEVEL_ABBREV.xml");
		
		copy(tis,victim);
		
		victim.close();
		
		String result = bos.toString();
		
		assertTrue("has header", result.contains("Mediated Value"));
		assertTrue("has timestamp", result.contains("1983-08-25"));
		assertTrue("has value", result.contains("up,ft,14.04"));
	}

	@Test
	public void testSkipHeaders() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DirectCSVOutputStream victim = new DirectCSVOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		victim.setAgency("USGS");
		victim.setSite("20515416303801");
		victim.setElevation(99.999);
		victim.setWrittenHeaders(true);
		InputStream tis = getClass().getResourceAsStream("/sample-data/USGS_20515416303801_WATERLEVEL_ABBREV.xml");
		
		copy(tis,victim);
		
		victim.close();
		
		String result = bos.toString();
		
		assertFalse("has header", result.contains("Mediated Value"));
		assertTrue("has timestamp", result.contains("1983-08-25"));
		assertTrue("has value", result.contains("up,ft,14.04"));
	}

	@Test
	public void testEmpty() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DirectCSVOutputStream victim = new DirectCSVOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		victim.setAgency("USGS");
		victim.setSite("20515416303801");
		victim.setElevation(99.999);
		victim.setWrittenHeaders(false);
		
		byte[] emptyBuf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<nothing/>\n".getBytes();
		InputStream tis = new ByteArrayInputStream(emptyBuf);
		
		copy(tis,victim);
		
		victim.close();
		
		String result = bos.toString();
		
		assertTrue("has header", result.contains("Mediated Value"));
		assertFalse("has timestamp", result.contains("1983-08-25"));
		assertFalse("has value", result.contains("up,ft,14.04"));
		
	}
	
	@Test
	public void testComma() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DirectCSVOutputStream victim = new DirectCSVOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		victim.setAgency("USGS");
		victim.setSite("SAMPLE");
		victim.setElevation(99.999);
		victim.setWrittenHeaders(false);
		
		InputStream tis = getClass().getResourceAsStream("/sample-data/USGS_SAMPLE_WATERLEVEL_COMMAS.xml");

		copy(tis,victim);
		
		victim.close();
		
		String result = bos.toString();
		
		// System.out.println(result);
		
		assertTrue("has header", result.contains("Mediated Value"));
		assertFalse("has raw input", result.contains("Comment, with embedded commas, and some \"quoted text\" as well"));
		assertTrue("has escaped commas", result.contains("\"Comment, with embedded commas, and some \"\"quoted text\"\" as well\""));
		
	}
	
	private void copy(InputStream is, OutputStream os) throws IOException {
		while (true) {
			int c = is.read();
			if (c < 0) {
				break;
			}
			os.write(c);
		}
	}
}
