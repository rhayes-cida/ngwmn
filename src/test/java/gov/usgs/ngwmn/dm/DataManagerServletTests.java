package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static gov.usgs.ngwmn.dm.DataManagerServlet.*;

public class DataManagerServletTests {

	DataManagerServlet dms;

	@Before
	public void setUp() {
		dms = new DataManagerServlet();
	}

	
	@Test(expected=RuntimeException.class)
	public void test_precheckWells_duplicateWellInList() {
		List<Specifier> specs = new ArrayList<Specifier>();
		specs.add( new Specifier("USGS", "007", WellDataType.LOG));
		specs.add( new Specifier("USGS", "007", WellDataType.LOG));
		
		dms.precheckWells(specs);
	}
	
	@Test
	public void test_precheckWells_checksWellsInList() {
		List<Specifier> specs = new ArrayList<Specifier>();
		specs.add( new Specifier("USGS", "007", WellDataType.LOG));
		specs.add( new Specifier("USGS", "006", WellDataType.LOG));
		
		final Map<String,Integer> called = new HashMap<String,Integer>();
		called.put("check",0);
		dms.db = new DataBroker() {
			@Override
			void check(Specifier spec) {
				called.put("check", called.get("check")+1);
			}
		};
		dms.precheckWells(specs);
		assertEquals("DataBroker.check must be called in precheck for each spec",
				Integer.valueOf(2),called.get("check"));
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_makeSpecification_successfullSingle_notBundled() {
		final List<Specifier> specsList = new ArrayList<Specifier>();
		specsList.add( new Specifier("AGRA", "007", WellDataType.LOG));
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");

		dms = new DataManagerServlet() {
			@Override
			protected List<Specifier> parseListOfWells(HttpServletRequest req) {
				return Collections.emptyList();
			}
			@Override
			protected List<Specifier> parseSpecifier(HttpServletRequest req) {
				return specsList;
			}
			@Override
			protected void precheckWells(List<Specifier> specs) {
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		Specification spect = dms.makeSpecification(req);
		assertFalse("expect that a single well req will not be bundled by default",spect.isBundled());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_makeSpecification_successfullSingle_bundled() {
		final List<Specifier> specsList = new ArrayList<Specifier>();
		specsList.add( new Specifier("AGRA", "007", WellDataType.LOG));
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");
		params.put(PARAM_BUNDLED,"Y");

		dms = new DataManagerServlet() {
			@Override
			protected List<Specifier> parseListOfWells(HttpServletRequest req) {
				return Collections.emptyList();
			}
			@Override
			protected List<Specifier> parseSpecifier(HttpServletRequest req) {
				return specsList;
			}
			@Override
			protected void precheckWells(List<Specifier> specs) {
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		Specification spect = dms.makeSpecification(req);
		assertTrue("expect that a single well req will be bundled on request",spect.isBundled());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_makeSpecification_successfullList() {
		final List<Specifier> specsList = new ArrayList<Specifier>();
		specsList.add( new Specifier("AGRA", "007", WellDataType.LOG));
		specsList.add( new Specifier("USGS", "006", WellDataType.LOG));
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");

		dms = new DataManagerServlet() {
			@Override
			protected List<Specifier> parseListOfWells(HttpServletRequest req) {
				return specsList;
			}
			@Override
			protected List<Specifier> parseSpecifier(HttpServletRequest req) {
				throw new RuntimeException("Should not be called for list of wells.");
			}
			@Override
			protected void precheckWells(List<Specifier> specs) {
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		Specification spect = dms.makeSpecification(req);
		assertTrue("expect that a list of wells req will be bundled",spect.isBundled());
	}

	
	// TODO possibly test a bad well format, non-existing well, etc
	
	@Test
	@SuppressWarnings("serial")
	public void test_parseListOfWells_successfully_multiAgency() {
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_TYPE,   "LOG");
		final String[] arrayOfWells = new String[] {"AGRA:007","FOOG:123","BARG:ABC"};
		
		dms = new DataManagerServlet() {
			@Override
			protected Specifier makeSpec(String agency, String featureID, String type) {
				assertNotNull(agency);
				assertNotNull(featureID);
				assertEquals("LOG",  type);
				assertTrue("expect agency IDs to be members of the array",
						Arrays.toString(arrayOfWells).contains(agency));
				assertTrue("expect feature IDs to be members of the array",
						Arrays.toString(arrayOfWells).contains(featureID));
				
				return new Specifier(agency, featureID, WellDataType.valueOf(type));
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
			@Override
			public String[] getParameterValues(String param) {
				if ("listOfWells".equals(param)) {
					return arrayOfWells;
				}
				return null;
			}
		};
		
		List<Specifier> list = dms.parseListOfWells(req);
		assertEquals(arrayOfWells.length, list.size());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void test_parseListOfWells_successfully_singleAgency() {
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY, "AGRA");
		params.put(PARAM_TYPE,   "LOG");
		final String[] arrayOfWells = new String[] {"007","123","ABC"};
		
//		final Specifier spec = new Specifier("AGRA", "007", WellDataType.LOG);
		dms = new DataManagerServlet() {
			@Override
			protected Specifier makeSpec(String agency, String featureID, String type) {
				assertEquals("AGRA", agency);
				assertNotNull(featureID);
				assertEquals("LOG",  type);
				assertTrue("expect feature IDs to be members of the array",
						Arrays.toString(arrayOfWells).contains(featureID));
				
				return new Specifier(agency, featureID, WellDataType.valueOf(type));
			}
		};
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
			@Override
			public String[] getParameterValues(String param) {
				if ("listOfWells".equals(param)) {
					return arrayOfWells;
				}
				return null;
			}
		};
		
		List<Specifier> list = dms.parseListOfWells(req);
		assertEquals(arrayOfWells.length, list.size());
	}
	
	
	
	
	@Test
	@SuppressWarnings("serial")
	public void test_parseSpecifier_successfully() {
		final Specifier spec = new Specifier("AGRA", "007", WellDataType.LOG);
		dms = new DataManagerServlet() {
			@Override
			protected Specifier makeSpec(String agency, String featureID, String type) {
				
				// we are not testing makeSpec here so the mock is testing req parsing.
				
				assertEquals("AGRA", agency);
				assertEquals("007",  featureID);
				assertEquals("LOG",  type);
				
				return spec;
			}
		};
		
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_AGENCY,  "AGRA");
		params.put(PARAM_FEATURE, "007");
		params.put(PARAM_TYPE,    "LOG");
		
		HttpServletRequest req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return params.get(param);
			}
		};
		
		List<Specifier> list = dms.parseSpecifier(req);
		assertEquals(1, list.size());
		assertEquals(spec, list.get(0));
	}
	
	
	
	
	@Test
	public void test_makeSpec_successfully() {
		Specifier spec = dms.makeSpec("AGRA", "007", "LOG");

		assertEquals("AGRA", spec.getAgencyID());
		assertEquals("007",  spec.getFeatureID());
		assertEquals(WellDataType.LOG, spec.getTypeID());
	}
	
	@Test
	public void test_makeSpec_defaults() {
		Specifier spec = dms.makeSpec(null, "007", null);

		assertEquals("USGS", spec.getAgencyID());
		assertEquals("007",  spec.getFeatureID());
		assertEquals(WellDataType.ALL, spec.getTypeID());
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_makeSpec_badSiteId() {
		dms.makeSpec("USGS", null, "LOG");
		assertTrue(false); // if we get here the method did not throw
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_makeSpec_badWellType() {
		dms.makeSpec("USGS", "007", "NONE");
		assertTrue(false); // if we get here the method did not throw
	}

}

abstract class MockRequest implements HttpServletRequest {	
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
