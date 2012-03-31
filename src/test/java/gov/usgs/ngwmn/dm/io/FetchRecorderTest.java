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

public class FetchRecorderTest {
	
	private FetchRecorder victim;
	private FetchLogDAO dao;

	@BeforeClass
	public static void setupNaming() throws Exception {
		final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.activate();
	}
	
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContextTest.xml");
		victim = ctx.getBean("FetchRecorder", FetchRecorder.class);
		dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
	}

	@Test
	public void testSUCCESS() {
		PipeStatistics stats = new PipeStatistics();
		
		victim.notifySuccess(stats);
		
		fail("not implemented");
		// check FetchLogDao for new item
	}

	@Test
	public void testFAIL() {
		PipeStatistics stats = new PipeStatistics();
		
		Exception npe = new NullPointerException();
		victim.notifyException(stats, npe);
		
		fail("not implemented");
		// check FetchLogDao for new item
		
	}

}
