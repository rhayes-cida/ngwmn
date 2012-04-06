package gov.usgs.ngwmn.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.DataBroker;
import gov.usgs.ngwmn.dm.SiteNotFoundException;
import gov.usgs.ngwmn.dm.cache.Loader;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
import gov.usgs.ngwmn.dm.cache.Retriever;
import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.cache.fs.FileCache;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.harvest.WebRetriever;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

public class DataBrokerTest extends ContextualTest {

	private DataBroker victim;
	
	@Before
	public void setUp() throws Exception {
		FileCache c = ctx.getBean("FileCache", FileCache.class);
		victim = ctx.getBean("DataBroker", DataBroker.class);		
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
		
		Specifier spec = makeSpec("USGS","402734087033401");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			victim.fetchWellData(spec, out);
		} catch (SiteNotFoundException ok) {
			assertFalse("Expected exception", true);
		}
	}

	@Test
	public void testPrefetch() throws Exception {
		Specifier spec = makeSpec("USGS","402734087033401");

		PipeStatistics stats = victim.prefetchWellData(spec);
		
		assertNotNull("stats", stats);
		assertEquals("spec", spec, stats.getSpecifier());
		assertEquals("success", Status.DONE, stats.getStatus());
		assertTrue("got bytes", stats.getCount() > 100);
		assertEquals("caller", "WebRetriever", stats.getCalledBy().getSimpleName());
	}
}
