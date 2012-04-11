package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WellRegistryDAOTest extends ContextualTest {
	private WellRegistryDAO dao;
		
	@Before
	public void setUp() throws Exception {
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
		WellRegistry w = dao.findByKey("USGS", "402734087033401");
		assertNotNull("well",w);
		assertEquals("agency", "USGS", w.getAgencyCd());
		assertEquals("site", "402734087033401", w.getSiteNo());
	}

	@Test
	public void testFindNone() {
		WellRegistry w;
		w = dao.findByKey("no-such-agency", "402734087033401");
		assertNull("well",w);
		w = dao.findByKey("IL EPA", "no-such-well");
		assertNull("well",w);
	}
	
	@Test
	public void testSelectByAgency() {
		List<WellRegistry> ww = dao.selectByAgency("USGS");
		assertNotNull("all", ww);
		assertFalse("empty", ww.isEmpty());
		assertFalse("too many", ww.size() > 5000);
		
		for (WellRegistry w : ww) {
			assertEquals("agency cd", "USGS", w.getAgencyCd());
		}
	}
	
	@Test
	public void testSelectByState_stateCd18() {
		final int stateCd = 18;
		
		List<WellRegistry> ww = dao.selectByState(stateCd);
		assertNotNull("by state", ww);
		assertFalse("empty", ww.isEmpty());
		assertFalse("too many", ww.size() > 5000);
				
		for (WellRegistry w : ww) {
			assertEquals("State cd", stateCd, w.getStateCd().intValue());
		}
	}
	
	
	@Test
	public void testSelectNoneAgency() {
		List<WellRegistry> ww = dao.selectByAgency("no-such-agency");
		assertNotNull("all", ww);
		assertTrue("empty", ww.isEmpty());
	}

	
}
