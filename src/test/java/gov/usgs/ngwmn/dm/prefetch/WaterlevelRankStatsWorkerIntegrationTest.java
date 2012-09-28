package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.Before;
import org.junit.Test;

public class WaterlevelRankStatsWorkerIntegrationTest extends ContextualTest {

	private WaterlevelRankStatsWorker victim;
	
	@Before
	public void setup() {
		victim = ctx.getBean("WaterlevelRankStatsWorker", WaterlevelRankStatsWorker.class);
	}
	
	@Test
	public void testUpdateOne() {
		int id = victim.updateOne();
		
		System.out.printf("Updated waterlevel_cache[%d]\n", id);
		assertTrue("id > 0", id > 0);
	}

}
