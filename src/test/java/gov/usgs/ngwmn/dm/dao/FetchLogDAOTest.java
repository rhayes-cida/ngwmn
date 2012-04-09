package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FetchLogDAOTest extends ContextualTest {

	private FetchLogDAO dao;
	
	@Before
	public void setUp() throws Exception {
		dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
	}

	@Test
	public void testInsertId() {
		FetchLog entry = new FetchLog();
		// NJGS:2288614
		entry.setAgencyCd("USGS");
		entry.setSiteNo("402734087033401");
		dao.insertId(entry);
		assertNotNull("id after insert", entry.getFetchlogId());
		
		System.out.printf("id after insert: %d\n", entry.getFetchlogId());
	}
	
	@Test
	public void testSelectByWell() {
		WellRegistryKey key = new WellRegistryKey("USGS", "402734087033401");
		
		List<FetchLog> ff = dao.byWell(key);
		assertFalse("empty", ff.isEmpty());
	}

}
