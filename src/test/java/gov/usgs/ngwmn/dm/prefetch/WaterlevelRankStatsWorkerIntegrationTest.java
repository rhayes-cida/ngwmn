package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;

import java.util.Date;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.joda.time.DateTime;
import org.joda.time.JodaTimePermission;
import org.junit.Before;
import org.junit.Test;

public class WaterlevelRankStatsWorkerIntegrationTest extends ContextualTest {

	private WaterlevelRankStatsWorker victim;
	
	@Before
	public void setup() {
		victim = ctx.getBean("WaterlevelRankStatsWorker", WaterlevelRankStatsWorker.class);
	}
	
	@Test
	public void testFindSample() {
		Integer id = victim.findOneSample();
		assertNotNull("sample id", id);
		assertTrue("id > 0", id > 0);
	}
	
	@Test
	public void testObservation() {
		Integer id = victim.findOneSample();
		if (id == null) {
			System.out.printf("Nothing in database to test, sorry");
			return;
		}
		
		Observation obs = victim.latestObservation(id);
		assertNotNull(obs);
		assertEquals("id", id.intValue(), obs.getCacheId());
		assertTrue("month looks OK", obs.getMonth() >= 1 && obs.getMonth() <= 12);
	}
	
	@Test
	public void testMonthly() {
		Integer id = victim.findOneSample();
		if (id == null) {
			System.out.printf("Nothing in database to test, sorry");
			return;
		}
		
		Observation obs = victim.latestObservation(id);
		Statistics monthly = victim.monthly_stats(obs);

		assertNotNull(monthly);
		
		// Note that Date getMonth range is 0..11 not 1..12
		assertEquals("max date in month", obs.getMonth(), getMonth(monthly.getMax_date()));
		assertEquals("min date in month", obs.getMonth(), getMonth(monthly.getMin_date()));
		assertEquals("cache id", id.intValue(), monthly.getWaterlevel_cache_id());
	}
	
	private int getMonth(Date d) {
		DateTime dt = new DateTime(d);
		return dt.getMonthOfYear();
	}
	
	@Test
	public void testTotal() {
		Integer id = victim.findOneSample();
		if (id == null) {
			System.out.printf("Nothing in database to test, sorry");
			return;
		}
		
		Observation obs = victim.latestObservation(id);
		Statistics total = victim.total_stats(obs);

		assertNotNull(total);
		assertEquals("cache id", id.intValue(), total.getWaterlevel_cache_id());
		assertNotNull("min date", total.getMin_date());
		assertNotNull("max date", total.getMax_date());
	}
	
	@Test(timeout=300000)
	public void testUpdateOne() {
		int id = victim.updateOne();
		
		System.out.printf("Updated waterlevel_cache[%d]\n", id);
		assertTrue("id > 0", id > 0);
	}

}
