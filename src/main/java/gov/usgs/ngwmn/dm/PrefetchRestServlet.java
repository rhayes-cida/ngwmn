package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefetchRestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(PrefetchRestServlet.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
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
		if (site != null) {
			// default type to ALL
			if (type == null) {
				type = "ALL";
			}
			spec = new Specifier(agency,site,WellDataType.valueOf(type));
		}
		
		// TODO something useful
		response.setContentType("text/plain");
		ServletOutputStream sos = response.getOutputStream();
		try {
			if (spec != null) {
				sos.println("prefetch " + spec);
			} else {
				sos.println("list " + ((agency == null)?"":agency));
			}
		} finally {
			sos.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
