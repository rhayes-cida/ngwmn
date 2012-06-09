package gov.usgs.ngwmn;

import static org.junit.Assert.*;

import org.junit.Test;

public class WellDataTypeTests {

	@Test
	public void test_makeFilename_log() {
		String name = WellDataType.LOG.makeFilename("wellName");
		assertTrue("file name should end with xml", name.endsWith("xml"));
		assertTrue("file name should contain type", name.contains(WellDataType.LOG.toString()));
	}

}
