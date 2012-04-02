package gov.usgs.ngwmn.dm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.Loader;
import gov.usgs.ngwmn.dm.cache.Retriever;
import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.cache.fs.FileCache;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.harvest.Harvester;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

public class DataBrokerTest extends ContextualTest {

	private DataBroker victim;
	
	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean("DataBroker", DataBroker.class);
		
		// TODO this should really be done in Spring...
		FileCache c = new FileCache();
		victim.setRetriever( new Retriever(c) );
		victim.setLoader(    new Loader(c)    );
		victim.setHarvester( new Harvester()  );
	}

	private Specifier makeSpec(String agency, String site) {
		Specifier spec = new Specifier();
		spec.setAgencyID(agency);
		spec.setFeatureID(site);
		spec.setTypeID(WellDataType.ALL);
		
		return spec;
	}

	@Test
	public void testSiteNotFound() throws Exception {
		
		Specifier spec = makeSpec("USGS","no-such-site");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			victim.fetchWellData(spec, out);
		} catch (SiteNotFoundException ok) {
			assertTrue("Expected exception", true);
		}
	}

	@Test
	public void testSiteFound() throws Exception {
		
		Specifier spec = makeSpec("USGS","007");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			victim.fetchWellData(spec, out);
		} catch (SiteNotFoundException ok) {
			assertFalse("Expected exception", true);
		}
	}

}
