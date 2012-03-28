package gov.usgs.ngwmn.dm.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
	public void testFindByKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectAll() {
		List<WellRegistry> all = dao.selectAll();
		assertNotNull("all", all);
		assertFalse("empty", all.isEmpty());
	}

}
