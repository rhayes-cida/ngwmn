package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.*;

public class DataBrokerTest extends ContextualTest {

	// TODO not used
	//private static final String SITE_URL = "http://localhost:8080/ngwmn/data?agency_cd=USGS&featureID=402734087033401";
	private DataBroker victim;
	private Specifier spec;
	
	@Before
	public void setUp() {
		spec = new Specifier("agency","well",WellDataType.LOG);
	}
	
	@Before
	public void checkSite() throws Exception {
		checkSiteIsVisible("USGS", "402734087033401");
	}
	@Before
	public void getVictim() throws Exception {
		victim = ctx.getBean("DataBroker", DataBroker.class);
	}

	@Test
	public void testFetchWellData() throws Exception {
		Specifier spec = new Specifier("USGS","402734087033401",WellDataType.ALL);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		Supplier<OutputStream> out = new SimpleSupplier<OutputStream>(bos);
		victim.fetchWellData(spec, out);
		
		Cache cache = ctx.getBean("FileCache", Cache.class);
		
		assertTrue("expect well data is cached", cache.contains(spec));
	}
	
	@Test
	public void test_validation_noDataFetchers() {
		DataBroker broker = new DataBroker();
		try {
			broker.check(spec);
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
		

}
