package gov.usgs.ngwmn.dm;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import gov.usgs.MockRequest;
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
	
	@Test
	public void test_parseListOfWells_with_extra_separators() {
		final Map<String,String> params = new HashMap<String, String>();
		params.put(PARAM_TYPE,   "LOG");
		final String[] arrayOfWells = new String[] {"IL_EPA:007","IL_EPA:1-well-with-:-and-chars"};
		
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
		assertEquals("expect all wells to parse", 
				arrayOfWells.length, spect.getWellIDs(WellDataType.LOG).size());
		
		List<Specifier> wells = spect.getWellIDs(WellDataType.LOG);
		for (Specifier spec : wells) {
			// Note that parser transliterates _ to space!
			assertEquals("agency id", "IL EPA", spec.getAgencyID());
		}
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
