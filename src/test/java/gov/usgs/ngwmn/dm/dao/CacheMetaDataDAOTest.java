package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.*;

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
}
