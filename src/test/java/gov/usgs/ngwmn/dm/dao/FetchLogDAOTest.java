package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
		entry.setAgencyCd("NJGS");
		entry.setSiteNo("2288614");
		dao.insertId(entry);
		assertNotNull("id after insert", entry.getFetchlogId());
		
		System.out.printf("id after insert: %d\n", entry.getFetchlogId());
	}
	
	@Test
	public void testSelectByWell() {
		WellRegistryKey key = new WellRegistryKey("NJGS", "2288614");
		
		List<FetchLog> ff = dao.byWell(key);
		assertFalse("empty", ff.isEmpty());
	}

}
