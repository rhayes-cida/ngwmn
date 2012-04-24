package gov.usgs.ngwmn.dm.io;

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
	
	@Before
	public void checkSite() throws Exception {
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		
		checkSiteIsVisible(stats.getSpecifier());
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

		int type_ct = 0;
		for (FetchLog fl : wfr) {
			if (type.toString().equals(fl.getDataStream())) {
				type_ct ++;
			}
		}
		
		FetchLog last = wfr.get(wfr.size()-1);
		assertEquals("type", stats.getSpecifier().getTypeID().toString(), last.getDataStream());
		
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
	

}
