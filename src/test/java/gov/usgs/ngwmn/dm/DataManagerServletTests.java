package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

import static gov.usgs.ngwmn.dm.DataManagerServlet.*;

public class DataManagerServletTests {

	DataManagerServlet dms;
	Specification spc;

	@Before
	public void setUp() {
		dms = new DataManagerServlet();
		spc = new Specification();
	}

	@Test
	public void test_parseBundling_trueIfParamSet() {
		final Map<String,String> params = new HashMap<String, String>();
		params.put(DataManagerServlet.PARAM_BUNDLED, "");
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		dms.parserBundling(req, spc);
		
		assertTrue( spc.isBundled() );
	}
	
	@Test
	public void test_parseBundling_falseIfParamNotSet() {
		final Map<String,String> params = new HashMap<String, String>();
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		dms.parserBundling(req, spc);
		
		assertFalse( spc.isBundled() );
	}
	
	@Test
	public void test_parseBundling_trueIfParamDataTypeCountMoreThanOne() {
		final Map<String,String> params = new HashMap<String, String>();
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		spc.addWell( new Specifier("a", "b", WellDataType.LOG) );
		spc.addWell( new Specifier("a", "b", WellDataType.QUALITY) );
		dms.parserBundling(req, spc);
		
		assertTrue( spc.isBundled() );
	}
	
	@Test
	public void test_parseBundling_trueIfParamWellCountMoreThanOne() {
		final Map<String,String> params = new HashMap<String, String>();
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		spc.addWell( new Specifier("a", "b", WellDataType.LOG) );
		spc.addWell( new Specifier("a", "c", WellDataType.LOG) );
		dms.parserBundling(req, spc);
		
		assertTrue( spc.isBundled() );
	}
	
	@Test(expected=RuntimeException.class)
	public void test_precheckWells_duplicateWellInList() {
		Specification spect = new Specification();
		spect.addWell( new Specifier("USGS", "007", WellDataType.LOG));
		spect.addWell( new Specifier("USGS", "007", WellDataType.LOG));
		
		dms.precheckWells(spect);
	}
	
	@Test
	public void test_precheckWells_checksWellsInList() {
		Specification spect = new Specification();
		spect.addWell( new Specifier("USGS", "007", WellDataType.LOG));
		spect.addWell( new Specifier("USGS", "006", WellDataType.LOG));
		
		final Map<String,Integer> called = new HashMap<String,Integer>();
		called.put("check",0);
		dms.db = new DataBroker() {
			@Override
			void check(Specifier spec) {
				called.put("check", called.get("check")+1);
			}
		};
		dms.precheckWells(spect);
		assertEquals("DataBroker.check must be called in precheck for each spec",
				Integer.valueOf(2),called.get("check"));
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_makeSpecification_successfullSingle_notBundled() {
		final Specification spect = new Specification();
		spect.addWell( new Specifier("AGRA", "007", WellDataType.LOG));
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");

		dms = new DataManagerServlet() {
			@Override
			protected Specification parseListOfWells(HttpServletRequest req) {
				return new Specification();
			}
			@Override
			protected Specification parseSpecifier(HttpServletRequest req) {
				return spect;
			}
			@Override
			protected void precheckWells(Specification spect) {
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		Specification spect2 = dms.makeSpecification(req);
		assertFalse("expect that a single well req will NOT be bundled by default",
				spect2.isBundled());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_makeSpecification_successfullSingle_bundled() {
		final Specification spect = new Specification();
		spect.addWell( new Specifier("AGRA", "007", WellDataType.LOG));
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");
		params.put(PARAM_BUNDLED,"Y");

		dms = new DataManagerServlet() {
			@Override
			protected Specification parseListOfWells(HttpServletRequest req) {
				return new Specification();
			}
			@Override
			protected Specification parseSpecifier(HttpServletRequest req) {
				return spect;
			}
			@Override
			protected void precheckWells(Specification spect) {
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		Specification spect2 = dms.makeSpecification(req);
		assertTrue("expect that a single well req will be bundled on request",
				spect2.isBundled());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_makeSpecification_successfullList() {
		final Specification spect = new Specification();
		spect.addWell( new Specifier("AGRA", "007", WellDataType.LOG));
		spect.addWell( new Specifier("USGS", "006", WellDataType.LOG));
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");

		dms = new DataManagerServlet() {
			@Override
			protected Specification parseListOfWells(HttpServletRequest req) {
				return spect;
			}
			@Override
			protected Specification parseSpecifier(HttpServletRequest req) {
				throw new RuntimeException("Should not be called for list of wells.");
			}
			@Override
			protected void precheckWells(Specification spect) {
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		Specification spect2 = dms.makeSpecification(req);
		assertTrue("expect that a list of wells req will be bundled", 
				spect2.isBundled());
	}

	
	@Test
	@SuppressWarnings("serial")
	public void test_parseListOfWells_successfully_multiAgency() {
		final Map<String,String[]> params = new HashMap<String, String[]>();
		params.put(PARAM_TYPE,   new String[] {"LOG"} );
		final String[] arrayOfWells = new String[] {"AGRA:007","FOOG:123","BARG:ABC"};
		
		dms = new DataManagerServlet() {
			@Override
			protected Specifier makeSpec(String agency, String featureID, WellDataType type) {
				assertNotNull(agency);
				assertNotNull(featureID);
				assertEquals(WellDataType.LOG,  type);
				assertTrue("expect agency IDs to be members of the array",
						Arrays.toString(arrayOfWells).contains(agency));
				assertTrue("expect feature IDs to be members of the array",
						Arrays.toString(arrayOfWells).contains(featureID));
				
				return new Specifier(agency, featureID, type);
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return null;
			}
			@Override
			public String[] getParameterValues(String param) {
				if (DataManagerServlet.PARAM_FEATURE.equals(param)) {
					return arrayOfWells;
				}
				if (PARAM_TYPE.equals(param)) {
					return (String[]) params.get(PARAM_TYPE);
				}
				return null;
			}
		};
		
		Specification spect = dms.parseListOfWells(req);
		assertEquals(arrayOfWells.length, spect.getWellIDs(WellDataType.LOG).size());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_parseListOfWells_successfully_singleAgency() {
		final Map<String,Object> params = new HashMap<String, Object>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   new String[]{"LOG"});
		final String[] arrayOfWells = new String[] {"007","123","ABC"};
		
		dms = new DataManagerServlet() {
			@Override
			protected Specifier makeSpec(String agency, String featureID, WellDataType type) {
				assertEquals("AGRA", agency);
				assertNotNull(featureID);
				assertEquals(WellDataType.LOG,  type);
				assertTrue("expect feature IDs to be members of the array",
						Arrays.toString(arrayOfWells).contains(featureID));
				
				return new Specifier(agency, featureID, type);
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return (String) params.get(param);
			}
			@Override
			public String[] getParameterValues(String param) {
				if (DataManagerServlet.PARAM_FEATURE.equals(param)) {
					return arrayOfWells;
				}
				if (PARAM_TYPE.equals(param)) {
					return (String[]) params.get(PARAM_TYPE);
				}
				return null;
			}
		};
		
		Specification spect = dms.parseListOfWells(req);
		assertEquals(arrayOfWells.length, spect.getWellIDs(WellDataType.LOG).size());
	}
	
	@Test
	public void test_parseListOfWells_successfully_oneBad() {
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_TYPE,   "LOG");
		final String[] arrayOfWells = new String[] {"AGRA:007","FOO:","BARG:ABC"}; // FOO has no well
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
			@Override
			public String[] getParameterValues(String param) {
				if (DataManagerServlet.PARAM_FEATURE.equals(param)) {
					return arrayOfWells;
				}
				return null;
			}
		};
		
		Specification spect = dms.parseListOfWells(req);
		assertEquals("expect one less well spec than the supplied list", 
				arrayOfWells.length-1, spect.getWellIDs(WellDataType.LOG).size());
	}
	
	@Test(expected=InvalidParameterException.class)
	public void test_parseListOfWells_successfully_twoBadIsTooMany() {
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_TYPE,   "LOG");
		final String[] arrayOfWells = new String[] {"AGRA:007","FOO:","BAR:"}; // FOO and BAR have no wells
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
			@Override
			public String[] getParameterValues(String param) {
				if (DataManagerServlet.PARAM_FEATURE.equals(param)) {
					return arrayOfWells;
				}
				return null;
			}
		};
		
		dms.parseListOfWells(req);
	}
	
	
	@Test
	public void test_parseDataType_successful_notNull() {
		final Map<String,String[]> params = new HashMap<String, String[]>();
		params.put(PARAM_TYPE, new String[]{"LOG"});
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String[] getParameterValues(String param) {
				return params.get(param);
			}
		};
		
		WellDataType[] typeIDs = dms.parseDataTypes(req);
		assertEquals(1, typeIDs.length);
		assertEquals(WellDataType.LOG, typeIDs[0]);
	}
	@Test
	public void test_parseDataType_successful_multiple() {
		final Map<String,String> params = new HashMap<String, String>();
		
		final String[] arrayOfTypes = new String[] {"LOG","QUALITY"};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
			@Override
			public String[] getParameterValues(String param) {
				if (DataManagerServlet.PARAM_TYPE.equals(param)) {
					return arrayOfTypes;
				}
				return null;
			}
		};
		
		WellDataType[] typeIDs = dms.parseDataTypes(req);
		assertEquals(2, typeIDs.length);

		Set<WellDataType> unique = new HashSet<WellDataType>(
				Arrays.asList(typeIDs)
			);
		assertEquals(2, unique.size());
		
		assertTrue(unique.contains(WellDataType.QUALITY));
		assertTrue(unique.contains(WellDataType.LOG));
	}
	
	@Test
	public void test_parseDataType_defaultAll() {
		HttpServletRequest req = new MockRequest() {
			@Override
			public String[] getParameterValues(String param) {
				return null;
			}
		};
		WellDataType[] typeIDs = dms.parseDataTypes(req);
		assertEquals(WellDataType.values().length, typeIDs.length);
		
		Set<WellDataType> unique = new HashSet<WellDataType>(
					Arrays.asList(typeIDs)
				);
		assertEquals(WellDataType.values().length, unique.size());
		
	}
	
	
	@Test
	@SuppressWarnings("serial")
	public void test_parseSpecifier_successfully() {
		final Specifier spec = new Specifier("AGRA", "007", WellDataType.LOG);
		dms = new DataManagerServlet() {
			@Override
			protected Specifier makeSpec(String agency, String featureID, WellDataType type) {
				
				// we are not testing makeSpec here so the mock is testing req parsing.
				
				assertEquals("AGRA", agency);
				assertEquals("007",  featureID);
				assertEquals(WellDataType.LOG,  type);
				
				return spec;
			}
		};
		
		final Map<String,Object> params = new HashMap<String, Object>();
		params.put(PARAM_AGENCY,  "AGRA");
		params.put(PARAM_FEATURE, "007");
		params.put(PARAM_TYPE,    new String[]{"LOG"});
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String[] getParameterValues(String param) {
				if (PARAM_TYPE.equals(param)) {
					return (String[]) params.get(PARAM_TYPE);
				}
				return null;
			}
			@Override
			public String getParameter(String param) {
				return (String) params.get(param);
			}
		};
		
		Specification spect  = dms.parseSpecifier(req);
		List<Specifier> list = spect.getWellIDs(WellDataType.LOG);
		assertEquals(1, list.size());
		assertEquals(spec, list.get(0));
	}
	
	
	
	
	@Test
	public void test_makeSpec_successfully() {
		Specifier spec = dms.makeSpec("AGRA", "007", WellDataType.LOG);

		assertEquals("AGRA", spec.getAgencyID());
		assertEquals("007",  spec.getFeatureID());
		assertEquals(WellDataType.LOG, spec.getTypeID());
	}
	
	@Test
	public void test_makeSpec_defaults() {
		Specifier spec = dms.makeSpec(null, "007", null);

		assertEquals("USGS", spec.getAgencyID());
		assertEquals("007",  spec.getFeatureID());
		assertEquals(WellDataType.LOG, spec.getTypeID());
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_makeSpec_badSiteId() {
		dms.makeSpec("USGS", null, WellDataType.LOG);
		assertTrue(false); // if we get here the method did not throw
	}
}

abstract class MockRequest implements HttpServletRequest {	
	
	public String getLocalAddr() {
		throw new NotImplementedException();
	}
	public String getLocalName() {
		throw new NotImplementedException();
	}
	public int getLocalPort() {
		throw new NotImplementedException();
	}
	public int getRemotePort() {
		throw new NotImplementedException();
	}
	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		throw new NotImplementedException();
	}
	@Override
	public void setAttribute(String arg0, Object arg1) {
		throw new NotImplementedException();
	}
	@Override
	public void removeAttribute(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public boolean isSecure() {
		throw new NotImplementedException();
	}
	@Override
	public int getServerPort() {
		throw new NotImplementedException();
	}
	@Override
	public String getServerName() {
		throw new NotImplementedException();
	}
	@Override
	public String getScheme() {
		throw new NotImplementedException();
	}
	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public String getRemoteHost() {
		throw new NotImplementedException();
	}
	@Override
	public String getRemoteAddr() {
		throw new NotImplementedException();
	}
	@Override
	public String getRealPath(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public BufferedReader getReader() throws IOException {
		throw new NotImplementedException();
	}
	@Override
	public String getProtocol() {
		throw new NotImplementedException();
	}
	@Override
	public String[] getParameterValues(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public Enumeration<?> getParameterNames() {
		throw new NotImplementedException();
	}
	@Override
	public Map<?,?> getParameterMap() {
		throw new NotImplementedException();
	}
	@Override
	public String getParameter(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public Enumeration<?> getLocales() {
		throw new NotImplementedException();
	}
	@Override
	public Locale getLocale() {
		throw new NotImplementedException();
	}
	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new NotImplementedException();
	}
	@Override
	public String getContentType() {
		throw new NotImplementedException();
	}
	@Override
	public int getContentLength() {
		throw new NotImplementedException();
	}
	@Override
	public String getCharacterEncoding() {
		throw new NotImplementedException();
	}
	@Override
	public Enumeration<?> getAttributeNames() {
		throw new NotImplementedException();
	}
	@Override
	public Object getAttribute(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public boolean isUserInRole(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public boolean isRequestedSessionIdValid() {
		throw new NotImplementedException();
	}
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new NotImplementedException();
	}
	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new NotImplementedException();
	}
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new NotImplementedException();
	}
	@Override
	public Principal getUserPrincipal() {
		throw new NotImplementedException();
	}
	@Override
	public HttpSession getSession(boolean arg0) {
		throw new NotImplementedException();
	}
	@Override
	public HttpSession getSession() {
		throw new NotImplementedException();
	}
	@Override
	public String getServletPath() {
		throw new NotImplementedException();
	}
	@Override
	public String getRequestedSessionId() {
		throw new NotImplementedException();
	}
	@Override
	public StringBuffer getRequestURL() {
		throw new NotImplementedException();
	}
	@Override
	public String getRequestURI() {
		throw new NotImplementedException();
	}
	@Override
	public String getRemoteUser() {
		throw new NotImplementedException();
	}
	@Override
	public String getQueryString() {
		throw new NotImplementedException();
	}
	@Override
	public String getPathTranslated() {
		throw new NotImplementedException();
	}
	@Override
	public String getPathInfo() {
		throw new NotImplementedException();
	}
	@Override
	public String getMethod() {
		throw new NotImplementedException();
	}
	@Override
	public int getIntHeader(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public Enumeration<?> getHeaders(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public Enumeration<?> getHeaderNames() {
		throw new NotImplementedException();
	}
	@Override
	public String getHeader(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public long getDateHeader(String arg0) {
		throw new NotImplementedException();
	}
	@Override
	public Cookie[] getCookies() {
		throw new NotImplementedException();
	}
	@Override
	public String getContextPath() {
		throw new NotImplementedException();
	}
	@Override
	public String getAuthType() {
		throw new NotImplementedException();
	}
}
