package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import org.junit.Test;

public class DirectCSVOutputStreamTest {

	@Test
	public void test() throws Exception {
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
