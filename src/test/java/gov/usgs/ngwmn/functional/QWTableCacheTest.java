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

import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.cache.qw.QWTableCache;
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

public class QWTableCacheTest extends ContextualTest {

	private static final int SIZE = 17779;
	private static final String TYPE = "QUALITY";
	private static final String SITE = "394212075275101";
	private static final String AGENCY = "USGS";
	private static final String filename = AGENCY+ "_" + SITE + "_" + TYPE;

	private QWTableCache victim;
	
	@BeforeClass
	public static void checkFile() throws Exception {
		InputStream ris = QWTableCacheTest.class.getResourceAsStream("/sample-data/" + filename);
		
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
		victim = ctx.getBean(QWTableCache.class);
	}

	@Before
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
	public void testSave() throws Exception {
				
		Specifier spec = makeSpecifier();
		
		OutputStream os = victim.destination(spec);
		
		InputStream inp = getClass().getResourceAsStream("/sample-data/" + filename);
		
		long ct = ByteStreams.copy(inp, os);
		os.close();
		
		assertTrue("got bytes", ct > 0);
		assertEquals("byte count", SIZE, ct);
	}

	private Specifier makeSpecifier() {
		Specifier spec = new Specifier();
		spec.setAgencyID(AGENCY);
		spec.setFeatureID(SITE);
		spec.setTypeID(TYPE);
		return spec;
	}

	@Test
	public void testFetchWellData() throws Exception {
		Specifier spec = makeSpecifier();
		
		Pipeline pip = new Pipeline(spec);
		
		victim.fetchWellData(spec, pip);
		InputSupplier<InputStream> iss = pip.getInputSupplier();
		
		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		ByteStreams.copy(iss, dest);
		
		// Contents may have expanded in shipping, due to pretty printing
		assertTrue("byte count", dest.size() >= SIZE);
	}

	// @Test
	public void testDumpData() throws Exception {
		Specifier spec = makeSpecifier();
		
		Pipeline pip = new Pipeline(spec);
		
		victim.fetchWellData(spec, pip);
		InputSupplier<InputStream> iss = pip.getInputSupplier();
		
		ByteStreams.copy(iss, System.out);
		
		assertTrue("still alive", true);
	}

	@Test
	public void testExists() throws Exception {
		Specifier spec = makeSpecifier();
		
		boolean e = victim.contains(spec);
		
		assertTrue("exists", e);
	}
	
	@Test
	public void testInfo() throws Exception {
		Specifier spec = makeSpecifier();
		
		CacheInfo info = victim.getInfo(spec);
		
		// Contents may have expanded in shipping, due to pretty printing
		// assertEquals(SIZE, info.getLength());
		assertTrue("size", info.getLength() >= SIZE);
		assertTrue(info.isExists());
		
		assertTrue("created before now", info.getCreated().before(new Date()));
		assertFalse("created after modified", info.getCreated().after(info.getModified()));
	}
}
