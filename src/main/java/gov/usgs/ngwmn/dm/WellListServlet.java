package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * Servlet implementation class WellListServlet
 */
public class WellListServlet extends HttpServlet {
	
	private ApplicationContext ctx;
	private static final long serialVersionUID = 1L;
	private WellRegistryDAO dao;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		dao = ctx.getBean("WellRegistryDAO", WellRegistryDAO.class);
		if (dao == null) {
			throw new RuntimeException("failed to get dao");
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		String servletPath = Objects.firstNonNull(
				request.getParameter("servlet"), 
				"data");
		
		if ( ! servletPath.matches("[A-Za-z]+")) {
			throw new ServletException("disallowed servlet path");
		}
		
		ServletOutputStream sos = response.getOutputStream();
		try {
			sos.println("<html><body>");
			List<WellRegistry> ww;
			
			String[] state_fips = request.getParameterValues("state");
			if (state_fips != null) {
				// user requested by states
				ww = new ArrayList<WellRegistry>();
				for (String state_cd : state_fips) {
					List<WellRegistry> wws = dao.selectByState(Integer.parseInt(state_cd));
					
					ww.addAll(wws);
				}
			} else {
				ww = dao.selectAll();
			}
			
			for (WellRegistry w : ww) {
				sos.print(String.format("<a href=\"%s?agency_cd=%s&featureID=%s\">", servletPath, w.getAgencyCd(), w.getSiteNo()));
				sos.print(String.format("%s site %s", Strings.nullToEmpty(w.getAgencyNm()), Objects.firstNonNull(w.getSiteName(), w.getSiteNo())));
				sos.println("</href><br />\n");
			}
			sos.println("</body></html>");
		} finally {
			sos.close();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
