package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.PrefetchI;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataKey;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.prefetch.Prefetcher.WellStatus;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

public class PrefetcherIntegrationTest extends ContextualTest {

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
	
	// @Test Depends on pre-Spring set up, have to jimmy this into context set-up
	public void test_config() {
		assertEquals("overridden fetch count limit", 401, victim.getFetchLimit());
	}
	
	@Test
	public void test_count() {
		ct = 0;
		delay = 0;
		victim.setTimeLimit(null);
		victim.setFetchLimit(10);
		PrefetchOutcome outcome = 
				victim.call();
		assertTrue("tried lots", ct > 9);
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

	/* Too burdensome 
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
	*/

	@Test
	public void testComparator() {
		Comparator<Prefetcher.WellStatus> comp = victim.getWellComparator();
		
		long now = System.currentTimeMillis();
		
		Prefetcher.WellStatus ws1 = new Prefetcher.WellStatus();
		ws1.cacheInfo = new CacheMetaData();
		ws1.type = WellDataType.CONSTRUCTION;
		ws1.cacheInfo.setMostRecentAttemptDt(new Date(now - 2134));
		
		Prefetcher.WellStatus ws2 = new Prefetcher.WellStatus();
		ws2.cacheInfo = new CacheMetaData();
		ws2.type = WellDataType.LITHOLOGY;
		ws2.cacheInfo.setMostRecentAttemptDt(new Date(now));
		
		Prefetcher.WellStatus ws3 = new Prefetcher.WellStatus();
		ws3.cacheInfo = new CacheMetaData();
		ws3.cacheInfo.setMostRecentAttemptDt(new Date(now));
		ws3.type = WellDataType.LOG;
		ws3.cacheInfo.setFetchPriority(87);

		PriorityQueue<WellStatus> pq = new PriorityQueue<Prefetcher.WellStatus>(4,comp);
		pq.add(ws1);
		pq.add(ws2);
		pq.add(ws3);
		
		Prefetcher.WellStatus ows1 = pq.poll();
		Prefetcher.WellStatus ows2 = pq.poll();
		Prefetcher.WellStatus ows3 = pq.poll();
		Prefetcher.WellStatus ows4 = pq.poll();
		
		assertNotNull(ows1);
		assertEquals(ws3, ows1);
		assertNotNull(ows2);
		assertNotNull(ows3);
		assertNull(ows4);
		
		assertTrue(ows2.cacheInfo.getMostRecentAttemptDt().before(ows3.cacheInfo.getMostRecentAttemptDt()));
	}

	@Test
	public void testSimpleComparator() {
		Comparator<Prefetcher.WellStatus> comp = victim.getSimpleWellComparator();
		
		final long now = System.currentTimeMillis();
		final long pastTime = now - 2134;
		WellRegistry wrMock = new WellRegistry() {

			@Override
			public String getAgencyCd() {
				return "NOTANAGENCY";
			}

			@Override
			public String getSiteNo() {
				return "NOTASITE";
			}
			
		};
		
		Prefetcher.WellStatus ws1 = new Prefetcher.WellStatus();
		ws1.cacheInfo = new CacheMetaData();
		ws1.type = WellDataType.CONSTRUCTION;
		ws1.cacheInfo.setMostRecentAttemptDt(new Date(pastTime));
		ws1.well = wrMock;
		
		Prefetcher.WellStatus ws2 = new Prefetcher.WellStatus();
		ws2.cacheInfo = new CacheMetaData();
		ws2.type = WellDataType.LITHOLOGY;
		ws2.cacheInfo.setMostRecentAttemptDt(new Date(now));
		ws2.well = wrMock;
		
		Prefetcher.WellStatus ws3 = new Prefetcher.WellStatus();
		ws3.cacheInfo = new CacheMetaData();
		ws3.type = WellDataType.LOG;
		ws3.cacheInfo.setMostRecentAttemptDt(new Date(now));
		ws3.well = wrMock;

		PriorityQueue<WellStatus> pq = new PriorityQueue<Prefetcher.WellStatus>(4,comp);
		pq.add(ws1);
		pq.add(ws2);
		pq.add(ws3);
		
		Prefetcher.WellStatus ows1 = pq.poll();
		Prefetcher.WellStatus ows2 = pq.poll();
		Prefetcher.WellStatus ows3 = pq.poll();
		assertTrue("isEmpty", pq.isEmpty());
		Prefetcher.WellStatus ows4 = pq.poll();
		
		assertNotNull(ows1);
		assertEquals(ws1, ows1);
		assertNotNull(ows2);
		assertNotNull(ows3);
		assertNull(ows4);
		assertNotSame(ows1, ows2);
		assertNotSame(ows2, ows3);
		
		assertEquals("first time is oldest", pastTime, ows1.cacheInfo.getMostRecentAttemptDt().getTime());
		assertEquals("second time is now", now, ows2.cacheInfo.getMostRecentAttemptDt().getTime());
		assertEquals("thirst timne is now", now, ows3.cacheInfo.getMostRecentAttemptDt().getTime());
	}
	
	@Test
	public void testQueue_IL_EPA() {
		String agency_cd = "IL EPA";
		tryQueue(agency_cd);
	}
	
	private Date getRecentest(CacheMetaData cmd) {
		Date attempt = null;
		if (cmd != null) {
			attempt = cmd.getMostRecentAttemptDt();
		}
		return Prefetcher.ensureDate(attempt);
	}
	
	@Test
	public void testQueue_TWDB() {
		String agency_cd = "TWDB";
		tryQueue(agency_cd);
	}

	public void tryQueue(String agency_cd) {
		PriorityQueue<WellStatus> q = victim.populateWellQeueForAgency(agency_cd);
		
		// check that fetch items are in oldest-first order
		WellStatus prev = null;
		int pos = 0;
		while ( ! q.isEmpty()) {
			WellStatus cur = q.remove();
			if (prev != null) {
				Date dprev = getRecentest(prev.cacheInfo);
				Date dcur = getRecentest(cur.cacheInfo);
				if (dprev.after(dcur)) {
					System.err.printf("trouble, prev %s after cur %s for wells %s, %s at position %d\n", dprev, dcur, prev, cur, pos);
				}
				assertFalse("wells not ordered by longest ago attempt order", 
						dprev.after(dcur));
			}
			prev = cur;
			pos++;
		}
	}
	
	@Test
	public void testCMDMethods() {
		CacheMetaData cmd1 = new CacheMetaData();
		cmd1.setAgencyCd("Agency");
		cmd1.setSiteNo("sight");
		cmd1.setDataType("DT");
		
		CacheMetaData cmd2 = new CacheMetaData();
		cmd2.setAgencyCd("Agency");
		cmd2.setSiteNo("sight");
		cmd2.setDataType("DT");
		
		assertEquals("hash code", cmd1.hashCode(), cmd2.hashCode());
		assertTrue("equals", cmd1.equals(cmd2));
		assertNotSame(cmd1,cmd2);
		
		CacheMetaDataKey cmd1k = cmd1;
		CacheMetaDataKey cmd2k = cmd2;
		
		assertEquals("hash code", cmd1k.hashCode(), cmd2k.hashCode());
		assertTrue("equals", cmd1k.equals(cmd2k));
		assertNotSame(cmd1,cmd2);
		
		cmd2.setSiteNo("blind");
		assertFalse("hash code", cmd1.hashCode() == cmd2.hashCode());
		assertFalse("equals", cmd1.equals(cmd2));
		assertNotSame(cmd1,cmd2);
		
	}
	
	
	@Test
	public void testRecordSkip() {
		WellStatus well = new WellStatus();
		well.type = WellDataType.LOG;
		
		// get a well
		WellRegistryDAO wrDAO = ctx.getBean(WellRegistryDAO.class);
		WellRegistry wr = wrDAO.findByKey("USGS", "007");
		well.well = wr;
		
		well.cacheInfo = new CacheMetaData();
		
		FetchLog record = victim.recordSkip(well, "Test");
		assertNotNull("fetch log item", record);
		assertTrue("fetch log id", record.getFetchlogId() > 0);
	}
	
	@Test
	public void testMultipleInstantiation() {
		Prefetcher p1 = ctx.getBean("PrefetchInstance", Prefetcher.class);
		Prefetcher p2 = ctx.getBean("PrefetchInstance", Prefetcher.class);
		
		assertNotNull("p1", p1);
		assertNotNull("p2", p2);
		
		assertFalse("two instances are the same", p1==p2);
		assertFalse("p1 same as global", p1 == victim);
		assertFalse("p2 same as global", p2 == victim);
	}
}
