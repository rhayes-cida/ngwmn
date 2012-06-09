package gov.usgs.ngwmn.dm.harvest;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

public class UrlFactoryTests {

	UrlFactory urls;
	
	@Before
	public void setUp() {
		urls = new UrlFactory();
	}
	

	@Test
	public void test_makeUrl_forWaterQualityData() {
		Specifier  spec = new Specifier("USGS","WELL0",WellDataType.QUALITY);
		String url = urls.makeUrl(spec);
		System.out.println(url);
		assertTrue(url.contains("qw?"));
	}
	
	@Test
	public void test_makeUrl_forWaterLevelData() {
		Specifier  spec = new Specifier("USGS","WELL0",WellDataType.WATERLEVEL);
		String url = urls.makeUrl(spec);
		System.out.println(url);
		assertTrue(url.contains("sos?"));
	}
	
	@Test
	public void test_makeUrl_forLogData() {
		Specifier  spec = new Specifier("USGS","WELL0",WellDataType.LOG);
		String url = urls.makeUrl(spec);
		System.out.println(url);
		assertTrue(url.contains("wfs?"));
	}
		
	@Test
	public void test_makeUrl_containsAgencyAndFeature() {
		Specifier  spec = new Specifier("USGS","WELL0",WellDataType.LOG);
		String url = urls.makeUrl(spec);
		assertTrue(url.contains(spec.getAgencyID()));
		assertTrue(url.contains(spec.getFeatureID()));
	}
	
	@Test
	public void test_makeUrl_startsWithBaseUrl() {
		Specifier  spec = new Specifier("USGS","WELL0",WellDataType.LOG);
		String url = urls.makeUrl(spec);
		assertTrue(url.startsWith("http://cida.usgs.gov/cocoon/gin/gwdp/agency/"));
	}
	
	
	@Test
	public void test_loadPropertiesFile() {
		boolean success = true;
		try {
			urls = new UrlFactory();
		} catch (RuntimeException e) {
			success = false;
		}
		assertTrue(success);
	}
	
	@Test
	public void test_injectParams() {
		Specifier  spec = new Specifier("USGS","WELL0",WellDataType.LOG);
		String url = "<agencyId><featureId>";
		String actual = urls.injectParams(url, spec.getAgencyID(), spec.getFeatureID());
		assertEquals("USGSWELL0", actual);
	}	
	

}
