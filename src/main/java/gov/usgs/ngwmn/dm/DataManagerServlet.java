package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.HttpResponseSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.SupplyZipOutput;
import gov.usgs.ngwmn.dm.io.executor.Flow;
import gov.usgs.ngwmn.dm.io.executor.SequentialFlowAggregator;
import gov.usgs.ngwmn.dm.spec.SpecResolver;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;
import gov.usgs.ngwmn.dm.spec.WellListResolver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DataManagerServlet extends HttpServlet {

	private   static final long   serialVersionUID = 2L;
	
	public static final String PARAM_AGENCY     = "agencyID";
	public static final String PARAM_FEATURE    = "featureID";
	public static final String PARAM_TYPE       = "type";
	public static final String PARAM_WELLS_LIST = "listOfWells";
	public static final String PARAM_BUNDLED    = "bundled";
	
	private  final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected DataBroker db;
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
	 * 		http://localhost:8080/ngwmn/data?agencyID=IL%20EPA&featureID=P405805 gets all the data
	 * 		http://localhost:8080/ngwmn/data?agencyID=IL%20EPA&featureID=P405805&type=LOG  gets just the log data
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		try {
			Specification spect = makeSpecification(req);
			Supplier<OutputStream> outs = new HttpResponseSupplier(spect, resp);
					
			try {
				Flow exec = null;
				if ( spect.isBundled() ) {
					SpecResolver resolver = new WellListResolver();
					outs = new SupplyZipOutput(outs);
					exec = new SequentialFlowAggregator(db, resolver.specIterator(spect), outs);
				} else {
					// TODO initial impl of single unbundled request
					// this is required because there is only one pipe and the seq exec calls begin unnecessarily
					exec = db.makeFlow(spect.getWellIDs().get(0), outs);
				}
				exec.call();
			} catch (SiteNotFoundException nse) {
				// this may fail, if detected after output buffer has been flushed
				resp.resetBuffer();
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, nse.getLocalizedMessage());
			} catch (DataNotAvailableException nda) {
				resp.resetBuffer();
				// TODO What's the right error code? 503? 504?
				resp.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, nda.getLocalizedMessage());
			} catch (Exception e) {
				logger.error("Problem getting well data", e);
				// TODO this message should not be rendered to the request
				// TODO it could reveal too much detail to the user
				throw new ServletException(e);
			}
		} finally {
			// TODO identify the request
			logger.info("Done with request for specifier");
		}
	}

	protected Specification makeSpecification(HttpServletRequest req) {
		Specification spec = new Specification();

		List<Specifier> wells = parseListOfWells(req);
		
		// if there is no wells list there surely should be a single well request
		if ( wells.isEmpty() ) {
			String bundled = req.getParameter(PARAM_BUNDLED);
			spec.setBundled(bundled != null);
			wells = parseSpecifier(req);
		} else {
			// a list of wells will be bundled as one file for now
			spec.setBundled(true);
		}

		// allow the empty wells list to remain null
		if ( ! wells.isEmpty() ) {
			spec.setWellIDs(wells);
		}
		
		// TODO parse out BBox and other query params
		
		precheckWells( spec.getWellIDs() );
		
		return spec;
	}

	protected String parseDataType(HttpServletRequest req) {
		String typeID = req.getParameter(PARAM_TYPE);
		if (typeID==null) {
			typeID = WellDataType.ALL.toString();
		}
		return typeID;
	}

	
	protected void precheckWells(List<Specifier> specs) throws SiteNotFoundException  {

		// check duplicates
		Set<Specifier> specSet = new HashSet<Specifier>(specs);
		if (specs.size() != specSet.size()) {
			throw new RuntimeException("Duplicate requested wells.");
		}

		// check exists and display
		for (Specifier spec : specs) {
			db.check(spec);
		}
	}

	protected String wellname(Specifier spec) {
		return spec.getAgencyID() + "_" + spec.getFeatureID();
	}

	protected List<Specifier> parseListOfWells(HttpServletRequest req) {
		
		// TODO this is how we can enforce one agency?
		String agencyDefault = req.getParameter(PARAM_AGENCY);

		String typeID = parseDataType(req);
		
		String wells[] = req.getParameterValues(PARAM_WELLS_LIST);
		if (wells==null || wells.length==0) {
			return Collections.emptyList();
		}
		List<Specifier> specs = new ArrayList<Specifier>(wells.length);
		
		RuntimeException re = null; // TODO should we allow one well to be bad or none?
		
		// construct the list of well specifiers
		for (String well : wells) {
			if (well==null) continue;

			try {
				// attempt to separate the agency and site IDs
				String specParts[] = well.split("[:_]");
				boolean hasSeparator = well.contains(":") || well.contains("_");
				
				// list of wells might be a list of sites only or a list of agency:site combos
				String agencyID  = specParts.length==1 && !hasSeparator ? agencyDefault : specParts[0];
				
				// TODO with all this commentary it would be better to rewrite
				// if the well contains only one entry and a separator then the agency was given w/o feature
				// however, if there is no separator with one entry then the feature is given w/o agency
				String featureID = specParts.length==1 ? (hasSeparator ?  "" : specParts[0])  
						: specParts[1]; // otherwise, we had both agency and well with separator
								
				
				Specifier spec   = makeSpec(agencyID, featureID, typeID);
				specs.add(spec);
			} catch (RuntimeException e) {
				// TODO catching NPE and IPE for one entry but two is too many?
				// TODO this is a first blush impl
				if (re!=null) throw e;
				re = e;
			}
		}
		// TODO this is first blush response to an invalid specs
		// TODO allowing one bad well unless there are no specs left to fetch
		if (re!=null && specs.isEmpty()) {
			throw re;
		}
		return specs;
		
	}	
	
	/** Parse a specifier from the request.
	 * This may get arbitrarily complex, but the specifier should not be evaluated here.
	 * The specifier is a query and can be constructed without touching the cache.
	 * 
	 * @param req
	 * @return
	 */
	protected List<Specifier> parseSpecifier(HttpServletRequest req) {
		
		String agencyID  = req.getParameter(PARAM_AGENCY);
		String featureID = req.getParameter(PARAM_FEATURE);
		String typeID    = parseDataType(req);

		Specifier spec   = makeSpec(agencyID, featureID, typeID);
		
		List<Specifier> specs = new ArrayList<Specifier>();
		specs.add(spec);
		return specs;
	}

	protected Specifier makeSpec(String agency, String featureID, String type) {
		if (type == null) {
			type = "ALL";
		}
		WellDataType wdt = WellDataType.valueOf(type);
		
		// TODO should we really default the agency?
		if (agency == null) {
			agency = "USGS";
		}
		// TODO Find a better place for this hack
		agency = agency.replace("_", " ");
		
		Specifier spec = new Specifier(agency,featureID,wdt);
		return spec;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
