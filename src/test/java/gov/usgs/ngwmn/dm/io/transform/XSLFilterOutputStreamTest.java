package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import org.junit.Test;

public class XSLFilterOutputStreamTest {

	@Test
	public void test() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSLFilterOutputStream victim = new XSLFilterOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		victim.setTransform("/gov/usgs/ngwmn/dm/io/transform/TestStyleSheet.xsl");

		InputStream tis = getClass().getResourceAsStream("TestInput.xml");
		
		copy(tis,victim);
		
		victim.close();
		
		String result = bos.toString();
		
		assertEquals("Now is the time\nfor all good men\nto party!\n", result);
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
