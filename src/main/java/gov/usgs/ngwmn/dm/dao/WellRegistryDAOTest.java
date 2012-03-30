package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WellRegistryDAOTest {
	private WellRegistryDAO dao;
	
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		dao = ctx.getBean("WellRegistryDAO", WellRegistryDAO.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSelectAll() {
		List<WellRegistry> all = dao.selectAll();
		assertNotNull("all", all);
		assertFalse("empty", all.isEmpty());
		
		for (WellRegistry w : all) {
			assertEquals("display flag", "1", w.getDisplayFlag());
		}
	}
	
	// IL EPA, P408223
	@Test
	public void testFindOne() {
		WellRegistry w = dao.findByKey("IL EPA", "P408223");
		assertNotNull("well",w);
		assertEquals("agency", "IL EPA", w.getAgencyCd());
		assertEquals("site", "P408223", w.getSiteNo());
	}

	@Test
	public void testFindNone() {
		WellRegistry w;
		w = dao.findByKey("no-such-agency", "P408223");
		assertNull("well",w);
		w = dao.findByKey("IL EPA", "no-such-well");
		assertNull("well",w);
	}
	
	@Test
	public void testSelectByAgency() {
		List<WellRegistry> ww = dao.selectByAgency("IL EPA");
		assertNotNull("all", ww);
		assertFalse("empty", ww.isEmpty());
		assertFalse("too many", ww.size() > 100);
		
		System.out.printf("IL well count: %d\n", ww.size());
		
		for (WellRegistry w : ww) {
			assertEquals("agency cd", "IL EPA", w.getAgencyCd());
		}
	}
	
	@Test
	public void testSelectNoneAgency() {
		List<WellRegistry> ww = dao.selectByAgency("no-such-agency");
		assertNotNull("all", ww);
		assertTrue("empty", ww.isEmpty());
	}

	
}
