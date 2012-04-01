package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class FetchLogTest {
	
	private FetchRecorder victim;
	private FetchLogDAO dao;

	@BeforeClass
	public static void setupNaming() throws Exception {
		final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		try {
			builder.activate();
		} catch (IllegalStateException ise) {
			// already had a naming provider; ignore
		}
	}
	
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContextTest.xml");
		victim = ctx.getBean("FetchRecorder", FetchRecorder.class);
		dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
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
