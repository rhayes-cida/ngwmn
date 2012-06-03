package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.WellDataType;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CacheMetaDataDAOTest extends ContextualTest {

	private CacheMetaDataDAO dao;
	
	@Before
	public void setUp() throws Exception {
		dao = ctx.getBean("CacheMetaDataDAO", CacheMetaDataDAO.class);
	}

	@Test
	public void testSelectAll() {
		List<CacheMetaData> dd = dao.listAll();
		assertFalse("empty", dd.isEmpty());
	}

	@Test
	public void testSelectByAgency() {
		List<CacheMetaData> dd = dao.listByAgencyCd("USGS");
		assertFalse("empty", dd.isEmpty());
		for (CacheMetaData md : dd) {
			assertEquals("agency", "USGS", md.getAgencyCd());
		}
	}
	
	@Test
	public void testUpdate() throws Exception {
		dao.updateStatistics();
		assertTrue("made it", true);
	}
	
	@Test
	public void testUpdateCacheMetaData() throws Exception {
		dao.updateCacheMetaData();
		assertTrue("survived", true);
	}

	@Test
	public void testGet() {
		// checkSiteIsVisible("USGS", "402734087033401"); // 400204074145401
		WellRegistryKey well = new WellRegistryKey("USGS", "007");
		WellDataType type = WellDataType.WATERLEVEL;
		CacheMetaData cmd = dao.get(well, type);
		
		assertNotNull("expected some meta data",cmd);
		System.out.printf("Got successCt=%d, failCt=%d, last attempt=%s\n", cmd.getSuccessCt(), cmd.getFailCt(), cmd.getMostRecentAttemptDt());
		assertEquals("Agency code", "USGS", cmd.getAgencyCd());
		assertEquals("Site no", "007", cmd.getSiteNo());
		assertTrue("non-negative fail count", cmd.getFailCt() >= 0);
		assertTrue("non-negative success count", cmd.getSuccessCt() >= 0);
		
	}
}
