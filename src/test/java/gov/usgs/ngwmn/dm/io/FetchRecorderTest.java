package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
