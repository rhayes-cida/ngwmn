package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class WellListServlet
 */
public class WellListServlet extends HttpServlet {
	
	ApplicationContext ctx;
	private static final long serialVersionUID = 1L;
       
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WellRegistryDAO dao = ctx.getBean("WellRegistryDAO", WellRegistryDAO.class);
		
		response.setContentType("text/plain");
		ServletOutputStream sos = response.getOutputStream();
		try {
			List<WellRegistry> ww = dao.selectAll();
			
			for (WellRegistry w : ww) {
				sos.println(w.getAgencyCd() +":"+ w.getSiteNo());
			}
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
