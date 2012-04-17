package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DataManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 2L;
	protected DataBroker db;
	private Logger logger = LoggerFactory.getLogger(DataManagerServlet.class);
	protected ApplicationContext ctx;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		
		db = ctx.getBean("DataBroker", DataBroker.class);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * Sample invocations:
	 * 		http://localhost:8080/ngwmn/data?agency_cd=IL%20EPA&featureID=P405805 gets all the data
	 * 		http://localhost:8080/ngwmn/data?agency_cd=IL%20EPA&featureID=P405805&type=LOG  gets just the log data
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Specifier spec = parseSpecifier(req);
		checkSpec(spec);
		
		String well_name = wellname(spec);
		
		try {
			WellDataType type = spec.getTypeID();
			resp.setContentType(type.contentType);
			resp.setHeader("Content-Disposition", "attachment; filename=" + type.makeFilename(well_name));
			
			// ensure that buffer size is greater than magic lower limit for
			// non-extant sites
			if (resp.getBufferSize() < 2000) {
				resp.setBufferSize(8*1024); // a reasonable guess at efficiency
			}
			ServletOutputStream puttee = resp.getOutputStream();
			Supplier<OutputStream> outSupply = new SimpleSupplier<OutputStream>(puttee);
			try {
				logger.info("Getting well data for {}", spec);
				db.fetchWellData(spec, outSupply);
			} catch (SiteNotFoundException nse) {
				// this may fail, if detected after output buffer has been flushed
				resp.resetBuffer();
				puttee = null;
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, nse.getLocalizedMessage());
			} catch (DataNotAvailableException nda) {
				resp.resetBuffer();
				puttee = null;
				// TODO What's the right error code? 503? 504?
				resp.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, nda.getLocalizedMessage());
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

	protected String wellname(Specifier spec) {
		return spec.getAgencyID() + "_" + spec.getFeatureID();
	}

	/** Parse a specifier from the request.
	 * This may get arbitrarily complex, but the specifier should not be evaluated here.
	 * The specifer is a query and can be constructed without touching the cache.
	 * 
	 * @param req
	 * @return
	 */
	protected Specifier parseSpecifier(HttpServletRequest req) {
		String featureID = req.getParameter("featureID");
		
		Specifier spec = new Specifier();
		spec.setFeatureID(featureID);
		
		String type = req.getParameter("type");
		if (type == null) {
			type = "ALL";
		}
		WellDataType wdt = WellDataType.valueOf(type);
		spec.setTypeID(wdt);
		
		String agency = req.getParameter("agency_cd");
		if (agency == null) {
			agency = "USGS";
		}
		// TODO Find a better place for this hack
		agency = agency.replace("_", " ");
		spec.setAgencyID(agency);
		
		return spec;
	}

	protected void checkSpec(Specifier spec) throws ServletException {
		if (null == spec.getFeatureID() || spec.getFeatureID().isEmpty()) {
			throw new ServletException("No feature identified by input");			
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
