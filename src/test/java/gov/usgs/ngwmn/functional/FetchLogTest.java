package gov.usgs.ngwmn.functional;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.io.FetchRecorder;
import gov.usgs.ngwmn.dm.io.StatsMaker;

import org.junit.Before;
import org.junit.Test;

public class FetchLogTest extends ContextualTest {
	
	private FetchRecorder victim;
	//private FetchLogDAO dao;

	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean("FetchRecorder", FetchRecorder.class);
		//dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
		
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		checkSiteIsVisible(stats.getSpecifier());
	}

	@Test
	public void testSUCCESS() {
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		
		victim.notifySuccess(stats);
		
		// TODO Check dao for fetch log with self as fetcher
	}

	@Test
	public void testFAIL() {
		PipeStatistics stats = StatsMaker.makeStats(getClass());
		
		Exception npe = new NullPointerException();
		victim.notifyException(stats, npe);
		
		// TODO check dao for item with recent time, self as fetcher, status fail
		
	}
	

}
