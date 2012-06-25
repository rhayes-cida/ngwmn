package gov.usgs.ngwmn.dm.harvest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.Invoker;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

public class WebRetrieverTests {

	private final String testUrl = "testUrl";
	
	private Map<String,Boolean> checkValues;
	
	private WebRetriever web;
	private Pipeline     pipe;
	private Specifier    spec;
	
	
	@Before
	public void setUp() throws Exception {
		checkValues = new HashMap<String, Boolean>();
		
		web = new WebRetriever();
		web.urlFactory   = new SpringUrlFactory() {
			@Override
			public String makeUrl(Specifier spec) {
				checkValues.put("makeUrlCalled",true);
				return testUrl;
			}
		};
		web.harvester    = new Harvester() {
			@Override
			public int wget(String url) throws IOException {
				assertEquals(testUrl,url);
				checkValues.put("wgetCalled",true);
				return HttpStatus.SC_OK;
			}
		};
		spec = new Specifier("A","F",WellDataType.LOG);
		pipe = new Pipeline(spec) {
			@Override
			public void setInputSupplier(Supplier<InputStream> in) {
				checkValues.put("setInputCalled", true);
			}
			@Override
			public void setInvoker(Invoker invoke) {
				checkValues.put("setInvokerCalled", true);
			}
		};
	}


	@Test
	public void test_callChain() throws Exception {

		boolean result = web.configureInput(spec, pipe);
		
		assertTrue(result);
		assertTrue(checkValues.get("makeUrlCalled"));
		assertTrue(checkValues.get("setInvokerCalled"));
		assertTrue(checkValues.get("setInputCalled"));
	}

/*	this test is more for the pipe invoke now that wget is deferred
	@Test
	public void test_callHarvester_withHttpStatusNotOK() throws Exception {

		web.harvester    = new Harvester() {
			@Override
			public int wget(String url) throws IOException {
				assertEquals(testUrl,url);
				checkValues.put("wgetCalled",true);
				return HttpStatus.SC_BAD_GATEWAY;
			}
		};
		
		boolean result = web.configureInput(spec, pipe);
		
		assertTrue(result);
		
		assertTrue(checkValues.get("makeUrlCalled"));
		assertTrue(checkValues.get("wgetCalled"));
		assertTrue(checkValues.get("checkSpecCalled"));
		assertTrue(checkValues.get("setInvokerCalled"));
		
		assertNull(checkValues.get("setInputCalled"));
	}
*/	
}
