package gov.usgs.ngwmn.functional;

import static org.junit.Assert.*;
import static gov.usgs.ngwmn.dm.DataManagerServlet.*;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpNotFoundException;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class BasicServletTest extends ContextualTest {

	private static final String WELL_LIST_AGENCY_DATA = "http://localhost:8080/ngwmn/data?"+PARAM_AGENCY+"=USGS&"+PARAM_WELLS_LIST+"=402734087033401&"+PARAM_WELLS_LIST+"=402247074250301&"+PARAM_TYPE+"="+WellDataType.WATERLEVEL;
	private static final String WELL_LIST_DATA = "http://localhost:8080/ngwmn/data?"+PARAM_WELLS_LIST+"=USGS:402734087033401&"+PARAM_WELLS_LIST+"=NJGS:2288614&"+PARAM_TYPE+"="+WellDataType.WATERLEVEL;
	private static final String WELL_WITH_DATA = "http://localhost:8080/ngwmn/data?"+PARAM_AGENCY+"=USGS&"+PARAM_FEATURE+"=402734087033401";
	private static final String WELL_NO_DATA   = "http://localhost:8080/ngwmn/data?"+PARAM_AGENCY+"=NJGS&"+PARAM_FEATURE+"=2288614";

	@BeforeClass
	public static void clearCache() {
		File c = new File( getBaseDir() );
		if (c.exists() && c.isDirectory()) {
			for (File f : c.listFiles()) {
				if ( f.canWrite() ) {
					boolean did = f.delete();
					if (did) {
						System.out.printf("Deleted cache file %s\n", f);
					} else {
						System.out.printf("Could not delete cache file %s\n", f);
						f.deleteOnExit(); // this way subsequent runs might work
					}
				} else {
					System.out.printf("Preserving cache file %s\n", f);
				}
			}
		}
	}
	
	@Before
	public void logSeparator() {
		System.out.println();
		System.out.println("    ----");
		System.out.println();
	}
	
	
	@Test
	public void test_listOfWells() throws Exception {
		checkSiteIsVisible("NJGS","2288614");
		checkSiteIsVisible("USGS", "402734087033401");
		ServletRunner sr = new ServletRunner( getClass().getResourceAsStream("/servlet-test-web.xml"), "/ngwmn");
		
		ServletUnitClient sc = sr.newClient();
		WebRequest req = new GetMethodWebRequest(WELL_LIST_DATA);
		WebResponse resp = sc.getResponse(req);
		assertNotNull("response", resp);
		
		for (String hn : resp.getHeaderFieldNames()) {
			System.out.printf("Header %s:%s\n", hn, resp.getHeaderField(hn));
		}
		String body = resp.getText();
		System.out.printf("contentLength=%d,size=%d\n", resp.getContentLength(), body.length());
		assertTrue("response size", body.length() > 10000);
		
		File file = new File("/tmp","data.zip");
		FileOutputStream fos = new FileOutputStream(file);
		ByteStreams.copy(resp.getInputStream(), fos);
		fos.flush();
		fos.close();
		int available = new FileInputStream(file).available();
		int bodyLenth = body.length();
		assertEquals("response size", available, bodyLenth);
	}
	
	@Test
	public void test_listOfWells_forSingleAgency() throws Exception {
		checkSiteIsVisible("USGS","400204074145401");
		checkSiteIsVisible("USGS","402734087033401");
		ServletRunner sr = new ServletRunner( getClass().getResourceAsStream("/servlet-test-web.xml"), "/ngwmn");
		
		ServletUnitClient sc = sr.newClient();
		WebRequest req = new GetMethodWebRequest(WELL_LIST_AGENCY_DATA);
		WebResponse resp = sc.getResponse(req);
		assertNotNull("response", resp);
		
		
		for (String hn : resp.getHeaderFieldNames()) {
			System.out.printf("Header %s:%s\n", hn, resp.getHeaderField(hn));
		}
		String body = resp.getText();
		System.out.printf("contentLength=%d,size=%d\n", resp.getContentLength(), body.length());
		assertTrue("response size", body.length() > 10000);
		
		File file = new File("/tmp","data2.zip");
		FileOutputStream fos = new FileOutputStream(file);
		ByteStreams.copy(resp.getInputStream(), fos);
		fos.flush();
		fos.close();
		int available = new FileInputStream(file).available();
		int bodyLenth = body.length();
		assertEquals("response size", available, bodyLenth);
	}
	
	
	@Test
	public void testWithData() throws Exception {
		checkSiteIsVisible("USGS", "402734087033401");
		ServletRunner sr = new ServletRunner( getClass().getResourceAsStream("/servlet-test-web.xml"), "/ngwmn");
		
		ServletUnitClient sc = sr.newClient();
		WebRequest req = new GetMethodWebRequest(WELL_WITH_DATA);
		WebResponse resp = sc.getResponse(req);
		assertNotNull("response", resp);
		
		for (String hn : resp.getHeaderFieldNames()) {
			System.out.printf("Header %s:%s\n", hn, resp.getHeaderField(hn));
		}
		String body = resp.getText();
		System.out.printf("contentLength=%d,size=%d\n", resp.getContentLength(), body.length());
		assertTrue("response size", body.length() > 10000);
	}

	@Test
	public void testWithNoData() throws Exception {
		checkSiteIsVisible("NJGS","2288614");
		// this site exists, but has no data (on 2012/03/23)
		ServletRunner sr = new ServletRunner( getClass().getResourceAsStream("/servlet-test-web.xml"), "/ngwmn");
		
		ServletUnitClient sc = sr.newClient();
		WebRequest req = new GetMethodWebRequest(WELL_NO_DATA);
		WebResponse resp = sc.getResponse(req);
		assertNotNull("response", resp);
		
		for (String hn : resp.getHeaderFieldNames()) {
			System.out.printf("Header %s:%s\n", hn, resp.getHeaderField(hn));
		}
		String body = resp.getText();
		System.out.printf("contentLength=%d,size=%d\n", resp.getContentLength(), body.length());
		
		// TODO We would prefer to get an HTTP error code here.
		assertTrue("response size", body.length() > 1000);
	}

	@Test(expected=HttpNotFoundException.class)
	public void testNonSite() throws Exception {
		// this site does not exist, so we expect an Exception when fetching
		ServletRunner sr = new ServletRunner( getClass().getResourceAsStream("/servlet-test-web.xml"), "/ngwmn");
		
		ServletUnitClient sc = sr.newClient();
		WebRequest req = new GetMethodWebRequest("http://localhost:8080/ngwmn/data?"+PARAM_FEATURE+"=NOSUCHSITE&"+PARAM_AGENCY+"=USGS");
		sc.getResponse(req);
		assertFalse("expected exception", true);
	}
	
/*	
    @Test(expected=HttpInternalErrorException.class)
	public void testIOError() throws Exception {
		// special test specifier, causes IO exception
		ServletRunner sr = new ServletRunner(this.getClass().getResourceAsStream("/servlet-test-web.xml"), "/ngwmn");
		
		ServletUnitClient sc = sr.newClient();
		WebRequest req = new GetMethodWebRequest("http://localhost:8080/ngwmn/data?"+PARAM_FEATURE+"=NOSUCHSITE&"+PARAM_AGENCY+"=TEST_INPUT_ERROR");
		sc.getResponse(req);
		assertFalse("expected exception", true);
	}
*/

	// Now repeat the tests; we expect to get cached results
	@Test(timeout=1000)
	public void testWithData_2() throws Exception {
		testWithData();
	}
	
	@Test(timeout=1000)
	public void testWithNoData_2() throws Exception {
		testWithNoData();
	}

	@Test(expected=HttpNotFoundException.class,timeout=1000)
	public void testNonSite_2() throws Exception {
		testNonSite();
	}
	
	
}
