package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class FetchLogDAOTest {

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
		dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
	}

	@Test
	public void testInsert() {
		FetchLog entry = new FetchLog();
		
		entry.setAgencyCd("USGS");
		entry.setSiteNo("007");
		dao.insert(entry);
		assertNull("generated ID not set by this method", entry.getFetchlogId());
	}

	@Test
	public void testInsertId() {
		FetchLog entry = new FetchLog();
		
		entry.setAgencyCd("USGS");
		entry.setSiteNo("007");
		dao.insertId(entry);
		assertNotNull("id after insert", entry.getFetchlogId());
		
		System.out.printf("id after insert: %d\n", entry.getFetchlogId());
	}
	
	@Test
	public void testSelectByWell() {
		WellRegistryKey key = new WellRegistryKey("USGS", "007");
		
		List<FetchLog> ff = dao.byWell(key);
		assertFalse("empty", ff.isEmpty());
	}

}
