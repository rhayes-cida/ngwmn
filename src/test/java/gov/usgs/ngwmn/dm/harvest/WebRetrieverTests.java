package gov.usgs.ngwmn.dm.harvest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.Invoker;
import gov.usgs.ngwmn.dm.io.Pipeline;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

public class WebRetrieverTests {

	private Map<String,Boolean> checkValues;
	private WebRetriever web;
	private Pipeline pipe;
	private Specifier spec;
	
	private final String testUrl = "testUrl";
	
	@Before
	public void setUp() throws Exception {
		checkValues = new HashMap<String, Boolean>();
		
		web = new WebRetriever();
		web.urlFactory   = new UrlFactory() {
			@Override
			public String makeUrl(Specifier spec) {
				checkValues.put("makeUrlCalled",true);
				return testUrl;
			}
		};
		web.harvester    = new Harvester() {
			@Override
			public int wget(String url) throws Exception {
				assertEquals(testUrl,url);
				checkValues.put("wgetCalled",true);
				return HttpStatus.SC_OK;
			}
		};
		spec = new Specifier() {
			public void check() {
				checkValues.put("checkSpecCalled",true);
			}
		};
		pipe = new Pipeline() {
			public void setInputStream(java.io.InputStream in) {
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
		assertTrue(checkValues.get("wgetCalled"));
		assertTrue(checkValues.get("checkSpecCalled"));
		assertTrue(checkValues.get("setInvokerCalled"));
		assertTrue(checkValues.get("setInputCalled"));
	}

	
	@Test
	public void test_callHarvester_withHttpStatusNotOK() throws Exception {

		web.harvester    = new Harvester() {
			@Override
			public int wget(String url) throws Exception {
				assertEquals(testUrl,url);
				checkValues.put("wgetCalled",true);
				return HttpStatus.SC_BAD_GATEWAY;
			}
		};
		
		boolean result = web.configureInput(spec, pipe);
		
		assertFalse(result);
		
		assertTrue(checkValues.get("makeUrlCalled"));
		assertTrue(checkValues.get("wgetCalled"));
		assertTrue(checkValues.get("checkSpecCalled"));
		assertTrue(checkValues.get("setInvokerCalled"));
		
		assertNull(checkValues.get("setInputCalled"));
	}
}
