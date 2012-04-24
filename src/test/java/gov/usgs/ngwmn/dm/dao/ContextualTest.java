package gov.usgs.ngwmn.dm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gov.usgs.ngwmn.dm.cache.fs.FileCache;
import gov.usgs.ngwmn.dm.spec.Specifier;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContextTest.xml"})
// Cannot use this, because it overrides system property spring.profiles.active; use that instead.
// Or set environment SPRING_PROFILES_ACTIVE or SPRING_PROFILES_DEFAULT
// @ActiveProfiles("local")
public abstract class ContextualTest {
	
	@Autowired
	protected ApplicationContext ctx;

	private static String basedir = "/tmp/gwdp-cache";

	@BeforeClass
	public static void setupNaming() throws Exception {
		final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.bind(FileCache.BASEDIR_JNDI_NAME, basedir);

		try {
			builder.activate();
		} catch (IllegalStateException ise) {
			// Set the required value into the existing context instead
			InitialContext ctx = new InitialContext();
			ctx.bind(FileCache.BASEDIR_JNDI_NAME, basedir);
		}
	}

	public static void setBasedir(String basedir) {
		ContextualTest.basedir = basedir;
	}

	protected void checkSiteExists(Specifier spec) throws Exception {
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		
		Connection conn = ds.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT count(*) from WELL_REGISTRY " +
					"WHERE agency_cd = ? and site_no = ?");
			ps.setString(1, spec.getAgencyID());
			ps.setString(2, spec.getFeatureID());
			ResultSet rs = ps.executeQuery();
			int ct = 0;
			while (rs.next()) {
				ct = rs.getInt(1);
			}
			
			if (ct != 1) {
				throw new RuntimeException("Expected site not found: " + spec);
			}
		} finally {
			conn.close();
		}		
	}

	// low-level, should not depend on much besides the data
	protected void checkSiteIsVisible(Specifier spec) throws Exception {
		
		String agencyID = spec.getAgencyID();
		String wellID = spec.getFeatureID();

		checkSiteIsVisible(agencyID, wellID);		
	}

	protected void checkSiteIsVisible(String agencyID, String wellID) 
			throws SQLException 
	{
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		
		Connection conn = ds.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT count(*) from GW_DATA_PORTAL.WELL_REGISTRY " +
					"WHERE AGENCY_CD = ? and SITE_NO = ? " +
					"and DISPLAY_FLAG = '1'");
			ps.setString(1, agencyID);
			ps.setString(2, wellID);
			ResultSet rs = ps.executeQuery();
			int ct = 0;
			while (rs.next()) {
				ct = rs.getInt(1);
			}
			
			if (ct != 1) {
				throw new RuntimeException("Expected site not found or not visible: " + agencyID + ":" + wellID);
			}
		} finally {
			conn.close();
		}
	}
	

}
