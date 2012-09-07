package gov.usgs.ngwmn.dm.cache;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.Before;
import org.junit.Test;

public class CacheCleanerIntegrationTest extends ContextualTest {

	private Cleaner cleaner;
	
	@Before
	public void setup() {
		cleaner = ctx.getBean("CacheCleaner", Cleaner.class);
	}
	
	@Test
	public void testClean() {
		assertEquals("days", 90, cleaner.getDaysToKeep());
		assertEquals("count", 3, cleaner.getCountToKeep());
		
		cleaner.setDaysToKeep(4*365);
		cleaner.setCountToKeep(100);
		
		int ct = cleaner.clean();
		System.out.printf("Cleaned %d old cache entries\n", ct);
		
		assertTrue("survived", true);
	}

}
