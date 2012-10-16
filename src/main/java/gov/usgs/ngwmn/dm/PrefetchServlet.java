package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Servlet implementation class WellListServlet
 * 
 * Sample invocation: http://localhost:8080/ngwmn/prefetch?featureID=402734087033401&agencyID=USGS
 */
public class PrefetchServlet extends DataManagerServlet {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		
		Specification spect = parseSpecifier(request);
		
		// There must be at least one valid well.
		if ( spect.isEmpty() ) {
			throw new ServletException("No wells found for that request");
		}
		// "There can be only one!"
		if ( spect.getDataTypes().size() > 1 || spect.size() > 1 ) {
			throw new ServletException("Only one well per request allowed");
		}
		// now we have one
		WellDataType type = spect.getDataTypes().iterator().next();
		Specifier spec    = spect.getWellIDs(type).get(0);
		
		String well_name = wellname(spec);
		
		try {
			response.setContentType("text/html");
			
			MDC.put("prefetch", "servlet");
			ServletOutputStream puttee = response.getOutputStream();
			try {
				logger.info("Getting well data for {} with well name {}", spec, well_name);
				long ct = db.prefetchWellData(spec);
				
				// TODO Get statistics from database or somewhere.
				
				puttee.println("<html><body>");
				puttee.println("<h1>Pre-fetch result</h1>");
				puttee.println("<pre>" + "got " + ct + " bytes" + "</pre>");
				puttee.println("</body></html>");
			} catch (SiteNotFoundException nse) {
				// this may fail, if detected after output buffer has been flushed
				response.resetBuffer();
				puttee = null;
				response.sendError(HttpServletResponse.SC_NOT_FOUND, nse.getLocalizedMessage());
			} catch (DataNotAvailableException nda) {
				response.resetBuffer();
				puttee = null;
				// TODO What's the right error code? 503? 504?
				response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, nda.getLocalizedMessage());
			} catch (Exception e) {
				logger.error("Problem getting well data", e);
				puttee = null;
				throw new ServletException(e);
			} finally {
				if (puttee != null) {
					puttee.close();
				}
			}
		} finally {
			logger.info("Done with request for specifier {}", spec);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
