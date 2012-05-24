package gov.usgs.ngwmn.functional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.FetchRecorder;
import gov.usgs.ngwmn.dm.io.StatsMaker;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FetchLogTest extends ContextualTest {
	
	private FetchRecorder victim;
	private FetchLogDAO dao;

	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean("FetchRecorder", FetchRecorder.class);
		dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
	}
	
	@Override
	public void preTest() throws Exception {
		System.out.println("beforeOnce - checking sites used in these tests.");
		
		Specifier spec = StatsMaker.makeStats(getClass()).getSpecifier();
		checkSiteIsVisible(spec);
	}

	@Test
	public void testSUCCESS() throws Exception {
		// ensure we have the latest start time
		Thread.sleep(100);

		PipeStatistics stats = StatsMaker.makeStats(getClass());
		WellRegistryKey key = stats.getSpecifier().getWellRegistryKey();
		
		victim.notifySuccess(stats);
		
		FetchLog latest = dao.mostRecent(key);
		assertNotNull("Got a result", latest);
		assertEquals("fetcher name", this.getClass().getSimpleName(), latest.getFetcher());
		assertNull("no problem", latest.getProblem());
	}

	@Test
	public void testFAIL() throws Exception {
		// ensure we have the latest start time
		Thread.sleep(100);
		
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		WellRegistryKey key = stats.getSpecifier().getWellRegistryKey();
		
		Exception npe = new NullPointerException();
		stats.setStatus(Status.FAIL);
		victim.notifyException(stats, npe);
		
		FetchLog latest = dao.mostRecent(key);
		assertNotNull("Got a result", latest);
		assertEquals("fetcher name", this.getClass().getSimpleName(), latest.getFetcher());
		
		assertEquals("Problem report", npe.getMessage(), latest.getProblem());
	}
	

	@Test
	public void testStats() throws Exception {
		GregorianCalendar cal = new GregorianCalendar(2012, 5-1, 18);
		Date center = cal.getTime();
		
		System.out.printf("stats for date %s\n", center);
		
		List<Map<String, Object>> v = dao.statisticsByDay(center);
		
		assertNotNull(v);
		for (Map<String,Object> row : v) {
			assertNotNull("agency", row.get("AGENCY_CD"));
			assertNotNull("data type", row.get("DATA_STREAM"));
			assertNotNull("status", row.get("STATUS"));
			
			Object ct = row.get("CT");
			assertNotNull("count", ct);
			assertTrue("count is numeric", ct instanceof Number);
			assertTrue("count is non-negative", ((Number) ct).doubleValue() >= 0);

			assertNotNull("average fetch time", row.get("AVG"));
			BigDecimal bd = (BigDecimal) row.get("AVG");
			assertTrue("Non-negative average fetch time", bd.signum() >= 0);
		}
		
	}

}
