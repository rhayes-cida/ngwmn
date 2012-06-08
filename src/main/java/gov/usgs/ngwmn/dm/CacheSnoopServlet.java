package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.cache.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.io.ByteStreams;

public class CacheSnoopServlet extends HttpServlet {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	
	protected Loader ldr;
	protected ApplicationContext ctx;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
				
		ldr = ctx.getBean("Loader", Loader.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		String path = request.getPathInfo();
		
		String datatype = null;
		String id = null;
		
		if (path != null) {
			// expecting /DATATYPE/id
			String[] pelems = path.split("/");
			
			switch (pelems.length) {
			case 3:
				id = pelems[2];
			case 2:
				datatype = pelems[1];
			case 1:
			case 0:
				break;
			default:
				throw new ServletException("too long a path");
			}
		}

		if (id == null) {
			throw new ServletException("not enuf parameters");
		}
		
		WellDataType wdt = WellDataType.valueOf(datatype);

		try {
			Cache c = ldr.getCache(wdt);

			if (c == null) {
				throw new ServletException("No cache for " + datatype);
			}
			
			InputStream is = c.retrieve(id);
			if (is == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			response.setContentType(wdt.contentType);
			OutputStream os = response.getOutputStream();
			
			ByteStreams.copy(is, os);
			
		}
		catch (ServletException se) {
			throw se;
		}
		catch (Exception e) {
			logger.warn("Problem", e);
			throw new ServletException(e);
		}
	
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
