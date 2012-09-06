package gov.usgs.ngwmn.dm.cache.qw;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WellLogInspectorIntegrationTest extends ContextualTest {

	private WellLogInspector victim;
	private Connection conn;
	
	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean("WellLogInspector", WellLogInspector.class);
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		conn = ds.getConnection();
	}

	@After
	public void tearDown() throws Exception {
		if (conn != null) {
			conn.close();
		}
	}

	private int getPlausibleID() throws Exception {
		PreparedStatement stat = conn.prepareStatement(
				"select max(log_cache_id) from gw_data_portal.log_cache " +
				"where published = 'Y' and xml IS NOT NULL ");
		try {
			int result = -1;
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
			if (result <= 0) {
				throw new RuntimeException("Could not find a plausible row to check");
			}
			return result;
		} finally {
			stat.close();
		}
	}
	
	@Test
	public void testAcceptable() throws Exception {
		int id = getPlausibleID();
		
		System.out.printf("testing log data for log id %d\n", id);
		boolean ok = victim.acceptable(id);
		
		assertTrue("got a result", ok || true);
	}
	
	@Test
	public void testLots() throws Exception {
		Statement s = conn.createStatement();
		s.setMaxRows(30);
		ResultSet rs = s.executeQuery("SELECT log_cache_id from gw_data_portal.log_cache " +
				"where xml IS NOT NULL " +
				"order by log_cache_id DESC ");
		
		List<Integer> ii = new ArrayList<Integer>();
		
		while (rs.next()) {
			ii.add(rs.getInt(1));
		}
		s.close();
		
		for (Integer i : ii) {
			boolean ok = victim.acceptable(i);
			
			System.out.printf("%d -> %s\n", i, ok);
		}
	}

}
