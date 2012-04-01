package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.*;

import java.util.List;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import com.google.common.eventbus.EventBus;

public class FetchRecorderTest {
	
	private PipelineFinishListener victim;
	private FetchLogDAO dao;
	private ApplicationContext ctx;

	public FetchRecorderTest() {
		ctx = new ClassPathXmlApplicationContext("applicationContextTest.xml");
	}

	@BeforeClass
	public static void setupNaming() throws Exception {
		final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.activate();
	}
	
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
		PipeStatistics stats = makeStats();
		stats.markEnd(Status.DONE);
		
		int countBefore = getFetchCount(stats.getSpecifier());
		victim.notifySuccess(stats);
		
		int countAfter = getFetchCount(stats.getSpecifier());
		
		assertEquals("fetch record count", countBefore+1, countAfter);
	}

	private PipeStatistics makeStats() {
		Specifier spec = new Specifier();
		spec.setAgencyID("USGS");
		spec.setFeatureID("007");
		spec.setTypeID(WellDataType.ALL);
		
		PipeStatistics stats = new PipeStatistics();
		stats.setCalledBy(getClass());
		stats.setSpecifier(spec);
		stats.markStart();
		return stats;
	}

	@Test
	public void testFAIL() {
		PipeStatistics stats = makeStats();
		
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
		
		PipeStatistics stats = makeStats();

		b.post(stats);

	}
	

}
