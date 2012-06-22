package gov.usgs;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadTokenFilter implements Filter {

	public static final String TOKEN_NAME = "downloadToken";
	public static final String DOMAIN     = ".er.usgs.gov";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String token = request.getParameter(TOKEN_NAME);
		logger.trace("noticed token {}", token);
		
		if (token != null) {
			if (response instanceof HttpServletResponse) {
				processTokenParam(request,  (HttpServletResponse) response, token);
			} else {
				String className = response==null ? "null" : response.getClass().getName();
				logger.warn("got token, but not HttpServletResponse, actual type = {}", className);
			}
		}
		chain.doFilter(request, response);
	}

	protected void processTokenParam(ServletRequest request, HttpServletResponse response, String token) {
		
			Cookie cookie = new Cookie(TOKEN_NAME, token);
			String path = "/";
			if (request instanceof HttpServletRequest) {
				HttpServletRequest hreq = (HttpServletRequest) request;
				path = hreq.getContextPath();
				if ("".equals(path)) {
					path = "/";
				}
			}
			cookie.setPath(path);

			String server = request.getServerName();
			logger.trace("processTokenParam server name {} path {}", server, path);
			
			if (server.contains(DOMAIN)) { // TODO maybe endsWith is better?
				cookie.setDomain(DOMAIN);
			}
			cookie.setVersion(1);

			response.addCookie(cookie);
			logger.debug("replayed token {}={}", TOKEN_NAME, token);
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// nothing to do here
	}

	@Override
	public void destroy() {
		// nothing to do here		
	}

}
