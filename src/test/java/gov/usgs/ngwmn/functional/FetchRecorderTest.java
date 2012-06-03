package gov.usgs.ngwmn.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.FetchRecorder;
import gov.usgs.ngwmn.dm.io.StatsMaker;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

public class FetchRecorderTest extends ContextualTest {
	
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
	
	private int getFetchCount(Specifier spec) {
		WellRegistryKey well = spec.getWellRegistryKey();
		List<FetchLog> wfr = dao.byWell(well);
		
		return wfr.size();
	}
	
	@Test
	public void testSUCCESS() {
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		stats.markEnd(Status.DONE);
		
		int countBefore = getFetchCount(stats.getSpecifier());
		victim.notifySuccess(stats);
		
		int countAfter = getFetchCount(stats.getSpecifier());
		
		assertEquals("fetch record count", countBefore+1, countAfter);
	}

	@Test
	public void testDataType() {
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		stats.markEnd(Status.DONE);

		// make sure there's at least one to look at
		victim.notifySuccess(stats);
		
		Specifier specifier = stats.getSpecifier();
		WellRegistryKey well = specifier.getWellRegistryKey();
		WellDataType type = specifier.getTypeID();
		
		List<FetchLog> wfr = dao.byWell(well);

		// Need to watch out for null start times in the underlying select (causing test failure).
		int type_ct = 0;
		for (FetchLog fl : wfr) {
			if (type.toString().equals(fl.getDataStream())) {
				type_ct ++;
			}
		}
		
		// Most recent is first -- list is in descending start time.
		// but need to skip any entries with null start time, they are synthetic test data
		FetchLog mostRecent = null;
		for (FetchLog entry : wfr) {
			if (entry.getStartedAt() != null) {
				mostRecent = entry;
				break;
			}
		}
		
		assertEquals("type", stats.getSpecifier().getTypeID().toString(), mostRecent.getDataStream());
		
		assertTrue("found at least one", type_ct > 0);
	}

	@Test
	public void testFAIL() {
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		
		int countBefore = getFetchCount(stats.getSpecifier());
		
		Exception npe = new NullPointerException();
		PipeStatisticsWithProblem event = new PipeStatisticsWithProblem(stats, npe);
		victim.notifyException(event);
				
		int countAfter = getFetchCount(stats.getSpecifier());
		
		assertEquals("fetch record count", countBefore+1, countAfter);
	}
	
	@Test
	public void testEventBus() {
		EventBus b = (EventBus) ctx.getBean("FetchEventBus");
		
		assertNotNull("FetchEventBus", b);
		
		PipeStatistics stats = StatsMaker.makeStats(getClass());

		b.post(stats);

	}
	
	@Test
	public void testUpdate() {
		EventBus eb = ctx.getBean("FetchEventBus", EventBus.class);

		Specifier spec = StatsMaker.makeStats(getClass()).getSpecifier();
		WellRegistryKey well = spec.getWellRegistryKey();

		FetchLog fl = dao.mostRecent(well);
		String st = fl.getStatus();
		
		fl.setStatus("TEST");
		try {

			eb.post(fl);

			FetchLog fl2 = dao.mostRecent(well);
			System.out.printf("status was %s, changed to %s\n", st, fl2.getStatus());
			assertEquals("TEST", fl2.getStatus());
		} finally {
			fl.setStatus(st);
			
			dao.update(fl);
		}
	}
	

}
