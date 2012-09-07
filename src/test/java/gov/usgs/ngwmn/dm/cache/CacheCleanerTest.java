package gov.usgs.ngwmn.dm.cache;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;

import gov.usgs.ngwmn.dm.cache.fs.FileCache;

import org.junit.Test;

public class CacheCleanerTest {

	@Test
	public void testClean() throws Exception {
		Cleaner c = new Cleaner();
		
		FileCache fc = new FileCache();
		fc.setBasedir(new File("/tmp"));
		
		c.setToClean(Collections.singletonList((Cache)fc));
		c.setCountToKeep(1000);
		c.setDaysToKeep(4*365);
		
		int ct = c.clean();
		
		assertTrue("survived",true);
		assertEquals("count", 0, ct);
	}

}
