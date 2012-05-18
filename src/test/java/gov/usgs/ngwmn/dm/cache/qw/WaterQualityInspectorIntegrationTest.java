package gov.usgs.ngwmn.dm.cache.qw;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WaterQualityInspectorIntegrationTest extends ContextualTest {

	private WaterQualityInspector victim;
	private Connection conn;
	
	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean("WaterQualityInspector", WaterQualityInspector.class);
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
				"select max(QUALITY_CACHE_ID) from gw_data_portal.quality_cache " +
				"where published = 'Y' and xml IS NOT NULL ");
		try {
			int result = -1;
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
			if (result < 0) {
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
		
		boolean ok = victim.acceptable(id);
		
		assertTrue("is ok", ok);
	}

}
