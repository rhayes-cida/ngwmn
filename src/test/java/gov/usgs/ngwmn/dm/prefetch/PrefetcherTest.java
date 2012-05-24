package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

import gov.usgs.ngwmn.dm.PrefetchI;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.prefetch.Prefetcher.WellStatus;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

public class PrefetcherTest extends ContextualTest {

	private Prefetcher victim;
	private int ct = 0;
	private int delay = 0;
	
	@Before
	public void setup() {
		victim = ctx.getBean("Prefetcher", Prefetcher.class);
		victim.setBroker(new PrefetchI() {
			
			@Override
			public long prefetchWellData(Specifier spec) throws Exception {
				ct++;
				if (delay > 0) {
					Thread.sleep(delay);
				}
				return spec.hashCode();
			}
		});
	}
	
	@Test
	public void test_count() {
		ct = 0;
		delay = 0;
		victim.setTimeLimit(null);
		victim.setFetchLimit(100);
		PrefetchOutcome outcome = 
				victim.call();
		assertTrue("tried lots", ct > 99);
		assertEquals(PrefetchOutcome.LIMIT_COUNT, outcome);
	}
	
	@Test
	public void test_time() {
		ct = 0;
		delay = 10;
		victim.setTimeLimit(100L);
		victim.setFetchLimit(1000000);
		PrefetchOutcome outcome =
				victim.call();
		assertTrue("tried a few", ct < 20);
		assertEquals(PrefetchOutcome.LIMIT_TIME, outcome);
	}

	@Test
	public void test_all() {
		ct = 0;
		delay = 0;
		victim.setTimeLimit(null);
		victim.setFetchLimit(0);
		PrefetchOutcome outcome =
				victim.call();
		assertTrue("tried lots and lots", ct > 500);
		assertEquals(PrefetchOutcome.FINISHED, outcome);
	}

	@Test
	public void testComparator() {
		Comparator<Prefetcher.WellStatus> comp = victim.getWellComparator();
		
		long now = System.currentTimeMillis();
		
		Prefetcher.WellStatus ws1 = new Prefetcher.WellStatus();
		ws1.cacheInfo = new CacheMetaData();
		ws1.cacheInfo.setMostRecentFetchDt(new Date(now - 2134));
		
		Prefetcher.WellStatus ws2 = new Prefetcher.WellStatus();
		ws2.cacheInfo = new CacheMetaData();
		ws2.cacheInfo.setMostRecentFetchDt(new Date(now));
		
		PriorityQueue<WellStatus> pq = new PriorityQueue<Prefetcher.WellStatus>(4,comp);
		pq.add(ws2);
		pq.add(ws1);
		
		Prefetcher.WellStatus ows1 = pq.poll();
		Prefetcher.WellStatus ows2 = pq.poll();
		Prefetcher.WellStatus ows3 = pq.poll();
		
		assertNotNull(ows1);
		assertNotNull(ows2);
		assertNull(ows3);
		
		assertTrue(ows1.cacheInfo.getMostRecentFetchDt().before(ows2.cacheInfo.getMostRecentFetchDt()));
	}
}
