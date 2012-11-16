package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

public class XSLFilterOutputStreamTest {

	@Test
	public void testWithXSLResource() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSLFilterOutputStream victim = new XSLFilterOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		InputStream xin = getClass().getResourceAsStream("/gov/usgs/ngwmn/dm/io/transform/TestStyleSheet.xsl");
		try {
			victim.setTransform(xin, "/gov/usgs/ngwmn/dm/io/transform/TestStyleSheet.xsl");

			InputStream tis = getClass().getResourceAsStream("TestInput.xml");
			
			copy(tis,victim);
			
			victim.close();
			
		} finally {
			xin.close();
		}
		
		String result = bos.toString();
		
		assertEquals("Now is the time\nfor all good men\nto party!\n", result);
	}

	@Test
	public void testWithXSLSource() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSLFilterOutputStream victim = new XSLFilterOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		
		InputStream xin = getClass().getResourceAsStream("/gov/usgs/ngwmn/dm/io/transform/TestStyleSheet.xsl");
		StreamSource xform = new StreamSource(xin, "/gov/usgs/ngwmn/dm/io/transform/TestStyleSheet.xsl");
		victim.setTransform(xform);

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
