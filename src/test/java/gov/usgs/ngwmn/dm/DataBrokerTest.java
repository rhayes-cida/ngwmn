package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.harvest.WebRetriever;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.*;

public class DataBrokerTest extends ContextualTest {

	private Specifier spec;
	
	@Before
	public void setUp() {
		spec = new Specifier("USGS","007",WellDataType.LOG);
	}
	
	@Override
	public void preTest() throws Exception {
		System.out.println("beforeOnce - checking sites used in these tests.");
		
		checkSiteIsVisible("USGS", "007");
		checkSiteIsVisible("USGS", "402734087033401");
	}
	

	@Test(expected=NullPointerException.class)
	public void test_validation_noDataFetchers() {
		DataBroker broker = new DataBroker();
		broker.check(spec);
		
		assertTrue("should not get here - expecting an exception.", false);
	}
		
	@Test(expected=NullPointerException.class)
	public void test_validation_noSpec() {
		DataBroker broker = new DataBroker();
		
		// these are not called - just removing nulls.
		broker.setRetriever(new WebRetriever());
		broker.setHarvester(new WebRetriever());
		
		broker.check(null);
		
		assertTrue("should not get here - expecting an exception.", false);
	}
		
	@Test
	public void testPrefetchCallRetriever() throws Exception {
		DataBroker broker = new DataBroker();
		
		final AtomicInteger retrieverCallCt = new AtomicInteger(0);
		final AtomicInteger loaderCallCt = new AtomicInteger(0);
		final AtomicInteger harvestorCallCt = new AtomicInteger(0);
		
		
		broker.setHarvester(new DataFetcher() {

			@Override
			public boolean configureInput(Specifier spec, Pipeline pipe)
					throws IOException {
				harvestorCallCt.incrementAndGet();
				return false;
			}
			
		});
		broker.setRetriever(new DataFetcher() {

			@Override
			public boolean configureInput(Specifier spec, Pipeline pipe)
					throws IOException {
				retrieverCallCt.incrementAndGet();
				return false;
			}
			
		});
		
		broker.setWellRegistry(new WellRegistryDAO() {

			@Override
			public WellRegistry findByKey(WellRegistryKey key) {
				WellRegistry value = new WellRegistry() {

					@Override
					public String getDisplayFlag() {
						return "1";
					}
					
				};
				return value;
			}
			
		});
		
		broker.setLoader(new DataLoader() {

			@Override
			public boolean configureOutput(Specifier spec, Pipeline pipe)
					throws IOException {
				byte[] buf = new byte[10];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				pipe.setInputSupplier(new SimpleSupplier<InputStream>(new ByteArrayInputStream(buf)));
				pipe.setOutputSupplier(new SimpleSupplier<OutputStream>(baos));
				
				loaderCallCt.incrementAndGet();
				return true;
			}
			
		});
		
		broker.setCacheDAO(new CacheMetaDataDAO(null) {

			@Override
			public CacheMetaData get(WellRegistryKey well, WellDataType type) {
				return null;
			}
			
		});
		
		broker.prefetchWellData(spec);
		
		assertEquals(1, loaderCallCt.get());
		assertEquals(1, harvestorCallCt.get());
		assertEquals(0, retrieverCallCt.get());
	}
}
