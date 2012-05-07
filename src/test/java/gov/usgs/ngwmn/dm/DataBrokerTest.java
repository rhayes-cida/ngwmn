package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.harvest.WebRetriever;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.*;

public class DataBrokerTest extends ContextualTest {

	private Specifier spec;
	
	@Before
	public void setUp() {
		spec = new Specifier("agency","well",WellDataType.LOG);
	}
	
	@Before
	public void checkSite() throws Exception {
		checkSiteIsVisible("USGS", "402734087033401");
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
