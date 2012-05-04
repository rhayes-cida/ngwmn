package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.harvest.WebRetriever;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.*;

public class DataBrokerTest extends ContextualTest {

	// TODO not used
	//private static final String SITE_URL = "http://localhost:8080/ngwmn/data?agencyID=USGS&featureID=402734087033401";
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

	@Test(expected=NullPointerException.class)
	public void test_validation_noDataFetchers() {
		DataBroker broker = new DataBroker();
		broker.check(spec);
	}
		
	@Test(expected=NullPointerException.class)
	public void test_validation_noSpec() {
		DataBroker broker = new DataBroker();
		
		// these are not called - just removing nulls.
		broker.setRetriever(new WebRetriever());
		broker.setHarvester(new WebRetriever());
		
		broker.check(null);
	}
		

}
