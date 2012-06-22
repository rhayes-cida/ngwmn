package gov.usgs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

public class DownloadTokenFilterTests {

	FilterChain  chain;
	MockRequest  req;
	MockResponse res;

	String tokenStored;
	
	@Before
	public void setup() throws Exception {
		chain = new MockFilterChain() {
			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1)
					throws IOException, ServletException {
				// do nothing by default
			}
		};
		
		res = new MockResponse(){};
		req = new MockRequest() {
			@Override
			public String getParameter(String param) {
				return DownloadTokenFilter.TOKEN_NAME.equals(param) ? tokenStored : null;
			}
		};
	}
	
	@Test
	public void test_processTokenParam_cookieSetDomainViaEmbededServerName() throws Exception {
		final Map<String, Object> calls = new HashMap<String, Object>();
		
		tokenStored = "nothing";

		req = new MockRequest() {
			@Override
			public String getServerName() {
				return "foo"+DownloadTokenFilter.DOMAIN+"bar";
			}
			@Override
			public String getContextPath() {
				return "";
			}
		};
		
		res = new MockResponse() {
			@Override
			public void addCookie(javax.servlet.http.Cookie cookie) {
				calls.put("addCookie", cookie);
			}
		};
		
		new DownloadTokenFilter().processTokenParam(req, res, tokenStored);
		
		Cookie cookie = (Cookie) calls.get("addCookie");
		assertEquals("expecting domain", DownloadTokenFilter.DOMAIN, cookie.getDomain());
	}

	@Test
	public void test_processTokenParam_cookieSetDomain() throws Exception {
		final Map<String, Object> calls = new HashMap<String, Object>();
		
		tokenStored = "nothing";

		req = new MockRequest() {
			@Override
			public String getServerName() {
				return DownloadTokenFilter.DOMAIN;
			}
			@Override
			public String getContextPath() {
				return "";
			}
		};
		
		res = new MockResponse() {
			@Override
			public void addCookie(javax.servlet.http.Cookie cookie) {
				calls.put("addCookie", cookie);
			}
		};
		
		new DownloadTokenFilter().processTokenParam(req, res, tokenStored);
		
		Cookie cookie = (Cookie) calls.get("addCookie");
		assertEquals("expecting domain", DownloadTokenFilter.DOMAIN, cookie.getDomain());
	}

	
	@Test
	public void test_processTokenParam_getServerName_addCookie() throws Exception {
		final Map<String, Object> calls = new HashMap<String, Object>();
		
		tokenStored = "nothing";

		req = new MockRequest() {
			@Override
			public String getServerName() {
				return "serverName";
			}
			@Override
			public String getContextPath() {
				return "";
			}
		};
		
		res = new MockResponse() {
			@Override
			public void addCookie(javax.servlet.http.Cookie cookie) {
				calls.put("addCookie", cookie);
			}
		};
		
		new DownloadTokenFilter().processTokenParam(req, res, tokenStored);
		
		assertNotNull("addCookie should be called", calls.get("addCookie"));
		assertTrue("expecting instance of cookie", calls.get("addCookie") instanceof Cookie);

		Cookie cookie = (Cookie) calls.get("addCookie");
		assertEquals("expecting no domain", null, cookie.getDomain());
		assertEquals("expecting no tokenStored", DownloadTokenFilter.TOKEN_NAME, cookie.getName());
		assertEquals("expecting no tokenStored", tokenStored, cookie.getValue());
	}
	
	
	@Test
	public void test_doFilter_tokenNotNullCallProcess() throws Exception {
		final Map<String, Object> calls = new HashMap<String, Object>();
		
		tokenStored = "nothing";

		new DownloadTokenFilter() {
			protected void processTokenParam(ServletRequest request, javax.servlet.http.HttpServletResponse response, String token) {
				calls.put("processTokenParam", true );
			};
		}.doFilter(req, res, chain);
		
		assertNotNull("processTokenParam should be called", calls.get("processTokenParam"));
	}
	
	@Test
	public void test_doFilter_tokenNotNullNoCallProcessWithNullResponse() throws Exception {
		tokenStored = "nothing";

		Exception ex = null;
		
		try {
			new DownloadTokenFilter() {
				protected void processTokenParam(ServletRequest request, javax.servlet.http.HttpServletResponse response, String token) {
					throw new RuntimeException("This should not be called");
				};
			}.doFilter(req, null, chain);
			
		} catch (NullPointerException e) {
			ex = e;
		} catch (RuntimeException e) {
			ex = e;
		}
		
		assertTrue("If not exception from bad call to processTokenParam this should be true", ex==null);
	}
	
	@Test
	public void test_doFilter_tokenNotNullNoCallProcessWithWrongResponse() throws Exception {
		tokenStored = "nothing";
		
		Exception ex = null;
		
		try {
			new DownloadTokenFilter() {
				protected void processTokenParam(ServletRequest request, javax.servlet.http.HttpServletResponse response, String token) {
					throw new RuntimeException("This should not be called");
				};
			}.doFilter(req, null, chain);
		
		} catch (NullPointerException e) {
			ex = e;
		} catch (RuntimeException e) {
			ex = e;
		}
		
		assertTrue("If not exception from bad call to processTokenParam this should be true", ex==null);
	}
	
	@Test
	public void test_doFilter_tokenNullCallChain() throws Exception {
		final Map<String, Object> calls = new HashMap<String, Object>();
		
		FilterChain chain = new MockFilterChain() {
			@Override
			public void doFilter(ServletRequest req, ServletResponse res)
					throws IOException, ServletException {
				calls.put("doFilter", new Object[] {req,res} );
			}
		};
		
		tokenStored = null;
		
		new DownloadTokenFilter().doFilter(req, null, chain);
		
		Object[] doFilterVal = (Object[]) calls.get("doFilter");
		assertNotNull("FilterChains are responsible for chain propagation", doFilterVal);
		assertEquals(req,  doFilterVal[0]);
		assertEquals(null, doFilterVal[1]);
	}

}
