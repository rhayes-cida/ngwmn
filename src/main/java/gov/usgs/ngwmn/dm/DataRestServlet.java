package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.HttpResponseSupplier;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DataRestServlet extends HttpServlet {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	
	protected DataBroker db;
	protected ApplicationContext ctx;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		
		db = ctx.getBean("DataBroker", DataBroker.class);
	}

	@SuppressWarnings("unchecked") // this is for getParameterNames cast into generics
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		String path = request.getPathInfo();
		
		String agency = null;
		String site = null;
		String type = null;
		
		if (path != null) {
			// may be / or /AGENCY or /AGENCY/WELL or /AGENCY/WELL/DATA
			String[] pelems = path.split("/");
			
			for (String pe : pelems) {
				logger.info("path elem: {}", pe);
			}
			
			switch (pelems.length) {
			case 4:
				type = pelems[3];
			case 3:
				site = pelems[2];
			case 2:
				agency = pelems[1];
			case 1:
			case 0:
				break;
			default:
				throw new ServletException("too long a path");
			}
		}
		
		if (logger.isDebugEnabled()) {
			for (String nm : Collections.list((Enumeration<String>) request.getParameterNames()))  {
				for (String v : request.getParameterValues(nm)) {
					logger.debug("{} = {}", nm, v);	
				}
			}
		}
		
		Specifier spec = null;
		if (type !=  null) {
			spec = new Specifier(agency,site,WellDataType.valueOf(type));
		}
		
		if (spec == null) {
			throw new ServletException("not enuf parameters");
		}
		
		boolean exists = true;
		try {
			db.checkSiteExists(spec);
		} catch (SiteNotFoundException snfe) {
			exists = false;
		}
		
		if ( ! exists) {
			// See if changing _ to space in agency name will fix it
			// maybe it a problem with space in agency name?
			String agncy = agency.replaceAll("_", " ");
			if (! agncy.equals(agency)) {
				logger.warn("retrying with spaced-out agency name {}", agncy);
				spec = new Specifier(agncy,site,WellDataType.valueOf(type));
			}
			// don't check again, just let the downstream processing throw the error
		}
		
		Specification spect = makeSpecification(spec);

		try {
			db.fetchWellData(spec, new HttpResponseSupplier(spect, response));
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected Specification makeSpecification(Specifier well) {
		Specification spec = new Specification();
		spec.setBundled(false);
		spec.addWell(well);
		return spec;
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
