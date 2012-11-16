package gov.usgs.ngwmn.dm.io.transform;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;

public class XSLFilterOutputStreamTest {

	private static final String TEST_STYLE_SHEET_XSL = "/gov/usgs/ngwmn/dm/io/transform/TestStyleSheet.xsl";

	// @Before
	public void preflight() {
		URL src = getClass().getResource(TEST_STYLE_SHEET_XSL);
		
		if (src == null) {
			throw new RuntimeException("Missing resource " + TEST_STYLE_SHEET_XSL);
		}
		
		try {
			InputStream srcIn = src.openStream();
			int ct = 0;
			while (true) {
				int c = srcIn.read();
				if (c < 0) {
					break;
				}
				ct++;
			}
			System.out.printf("succeeded in reading %d bytes from %s\n", ct, TEST_STYLE_SHEET_XSL);
		} catch (Exception e) {
			throw new RuntimeException("Problem reading transform", e);
		}
	}
	
	// @Test
	public void testWithXSLResource() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSLFilterOutputStream victim = new XSLFilterOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		InputStream xin = getClass().getResourceAsStream(TEST_STYLE_SHEET_XSL);
		try {
			victim.setTransform(xin, TEST_STYLE_SHEET_XSL);

			InputStream tis = getClass().getResourceAsStream("TestInput.xml");
			
			copy(tis,victim);
			
			victim.close();
			
		} finally {
			xin.close();
		}
		
		String result = bos.toString();
		
		assertEquals("Now is the time\nfor all good men\nto party!\n", result);
	}

	// @Test
	public void testWithXSLSource() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSLFilterOutputStream victim = new XSLFilterOutputStream(bos);
		
		victim.setExecutor(Executors.newSingleThreadExecutor());
		
		InputStream xin = getClass().getResourceAsStream(TEST_STYLE_SHEET_XSL);
		StreamSource xform = new StreamSource(xin, TEST_STYLE_SHEET_XSL);
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
