package gov.usgs.ngwmn.functional;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.cache.qw.DatabaseXMLCache;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.CountingOutputStream;
import com.google.common.io.InputSupplier;
import com.google.common.io.NullOutputStream;

public class QualityDBCacheTest extends ContextualTest {

	private static final int SIZE = 17779;
	private static final WellDataType TYPE = WellDataType.QUALITY;
	private static final String SITE = "402734087033401";
	private static final String AGENCY = "USGS";
	private static final String filename = AGENCY+ "_" + SITE + "_" + TYPE;

	private DatabaseXMLCache victim;
	
	@BeforeClass
	public static void checkFile() throws Exception {
		InputStream ris = QualityDBCacheTest.class.getResourceAsStream("/sample-data/" + filename);
		
		CountingOutputStream cos = new CountingOutputStream(new NullOutputStream());
		
		long ct = ByteStreams.copy(ris, cos);
		
		cos.close();
		
		assertEquals("bytes", SIZE, ct);
	}

	public void printDriverVersion(Connection conn) throws SQLException {
	    DatabaseMetaData meta = conn.getMetaData();

	    // gets driver info:
	    System.out.println("DriverName: " + meta.getDriverName() );  
	    System.out.println("DriverVersion: " + meta.getDriverVersion() );  
	    System.out.println("DriverMajorVersion: " + meta.getDriverMajorVersion() );  
	    System.out.println("DriverMinorVersion: " + meta.getDriverMinorVersion() );  
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean("QualityCache",DatabaseXMLCache.class);
	}

	// @Before
	public void showDriver() throws SQLException {
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		
		Connection conn = ds.getConnection();
		try {
			printDriverVersion(conn);
		} finally {
			conn.close();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUpdateMD5() throws Exception {
		int ct = victim.fixMD5();
		
		assertEquals("none to fix", 0, ct);
	}
	
	@Test
	public void testClean() throws Exception {
		int ct = victim.cleanCache();
		
		System.out.printf("Cleaned cache, count %d\n", ct);
		assertTrue("should not have a lot of cruft", ct <= 1);
	}
		
	@Test
	public void testSaveAndFetch() throws Exception {
		testSave();
		testFetchWellData();
	}

	public void testSave() throws Exception {
				
		Specifier spec = makeSpecifier();
		
		OutputStream os = victim.destination(spec);
		
		InputStream inp = getClass().getResourceAsStream("/sample-data/" + filename);
		
		long ct = ByteStreams.copy(inp, os);
		os.close();
		
		assertTrue("expect got some bytes", ct > 0);
		assertEquals("expect byte count has not changed", SIZE, ct);
	}

	private Specifier makeSpecifier() {
		Specifier spec = new Specifier(AGENCY,SITE,TYPE);
		return spec;
	}

	public void testFetchWellData() throws Exception {
		Specifier spec = makeSpecifier();
		
		Pipeline pip = new Pipeline(spec);
		
		victim.fetchWellData(spec, pip);
		InputSupplier<InputStream> iss = pip.getInputSupplier();
		
		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		ByteStreams.copy(iss, dest);
		
		// Contents may have expanded in shipping, due to pretty printing
		assertTrue("expect byte count has not shrunk", dest.size() >= SIZE);
	}

	// @Test
	public void testDumpData() throws Exception {
		Specifier spec = makeSpecifier();
		
		Pipeline pip = new Pipeline(spec);
		
		victim.fetchWellData(spec, pip);
		InputSupplier<InputStream> iss = pip.getInputSupplier();
		
		ByteStreams.copy(iss, System.out);
		
		assertTrue("expect test method reaches end", true);
	}

	@Test
	public void testExists() throws Exception {
		Specifier spec = makeSpecifier();
		
		boolean e = victim.contains(spec);
		
		assertTrue("expect cache exists", e);
	}
	
	@Test
	public void testInfo() throws Exception {
		Specifier spec = makeSpecifier();
		
		CacheInfo info = victim.getInfo(spec);
		
		// Contents may have expanded in shipping, due to pretty printing
		// assertEquals(SIZE, info.getLength());
		assertTrue("expect size no smaller than inpout file", info.getLength() >= SIZE);
		assertTrue("expect cache exists",info.isExists());
		
		assertTrue("expect created before now", info.getCreated().before(new Date()));
		assertFalse("expect create time after modify time", info.getCreated().after(info.getModified()));
	}
}
