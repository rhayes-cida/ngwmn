package gov.usgs.ngwmn.dm.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gov.usgs.ngwmn.dm.cache.fs.FileCache;
import gov.usgs.ngwmn.dm.spec.Specifier;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Level;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContextTest.xml"})
// Cannot use this, because it overrides system property spring.profiles.active; use that instead.
// Or set environment SPRING_PROFILES_ACTIVE or SPRING_PROFILES_DEFAULT
// @ActiveProfiles("local")
public abstract class ContextualTest {
	
	private static String  basedir   = "/tmp/gwdp-cache";
	private static boolean preTestedOnce;
	private static boolean databaseAvailable;
	private static boolean ginAvailable;

	@Autowired
	protected ApplicationContext ctx;

	
	
	public static void setBasedir(String basedir) {
		ContextualTest.basedir = basedir;
	}
	public static String getBaseDir() {
		return basedir;
	}
	
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
	
	@BeforeClass
	public static void setUpLogging() {
		setUpLogging(Level.INFO);
	}
	public static void setUpLogging(Level level) {
		ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger)
				LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );
		log.setLevel(level);
	}

	@Before
	public final void logSeparator() {
		System.out.println();
		System.out.println("    ----");
		System.out.println();
	}

	public final void resourceAvailable(String resource, boolean available) throws Exception {
		if (available) {
			System.out.println("preTest: available " + resource);
		} else {
			String msg = ">>>> preTest: " + resource + " NOT available <<<<";
			System.out.println();
			System.out.println(msg);
			System.out.println();
			throw new IOException(msg);
		}
	}
	@Before
	public final void preTestGin() throws Exception {
		resourceAvailable("GIN", ginAvailable);
	}
	@Before
	public final void preTestDatabase() throws Exception {
		resourceAvailable("DATABASE OR TABLE", databaseAvailable);
	}
	
	public final void checkGin() throws Exception {
		// TODO check GIN system with an http call
		System.out.println("preTest - gin available");
	}
	public final void checkDatabase() throws Exception {
		System.out.println("preTest - database available");
		
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		Connection conn = ds.getConnection();

//		import static org.junit.Assume.*; // TODO note that the following line require this import
//		assumeNotNull(conn); // TODO not sure how to use this method
		
		if (conn == null) {
			String msg = ">>>> DATABASE OR TABLE IS NOT AVAILABLE <<<<";
			logSeparator();
			System.out.println(msg);
			throw new IOException(msg);
		}
	}
	@Before
	public final void beforeOnce() throws Exception {
		
		if ( ! preTestedOnce ) {
			try {
				logSeparator();
				
				checkDatabase();
				databaseAvailable = true;
				
				checkGin();
				ginAvailable = true;
				
				preTest();
				preTestedOnce = true;
				
			} catch (Exception e) {
				System.out.println();
				System.out.println(">>>> beforeOnce ERROR running pre-test method <<<<");
				System.out.println();
				e.printStackTrace();
			}
		}
	}
	public void preTest() throws Exception {
		// subclasses override to have a preTests run only once.
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
