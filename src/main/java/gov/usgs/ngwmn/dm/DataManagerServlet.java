package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.HttpResponseSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.SupplyZipOutput;
import gov.usgs.ngwmn.dm.io.aggregate.Flow;
import gov.usgs.ngwmn.dm.io.aggregate.SequentialJoiningAggregator;
import gov.usgs.ngwmn.dm.spec.Encoding;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DataManagerServlet extends HttpServlet {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private   static final long   serialVersionUID = 2L;
	
	public static final String PARAM_AGENCY     = "agencyID";
	public static final String PARAM_FEATURE    = "featureID";
	public static final String PARAM_TYPE       = "type";
	public static final String PARAM_BUNDLED    = "bundled";
	public static final String PARAM_ENCODING   = "encode";
	
	
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
			int bufsize = resp.getBufferSize();
			if (bufsize < 12*1024) {
				resp.setBufferSize(12*1024);
				logger.warn("bumped buffer size from {} to {}", bufsize, resp.getBufferSize());
			}
			
			Specification spect = makeSpecification(req);
			Supplier<OutputStream> outs = new HttpResponseSupplier(spect, resp);
					
			try {
				
				Flow exec = null;
				if ( ! spect.getDataTypes().contains(WellDataType.ALL)  // TODO ALL asdf
						&& spect.isBundled() ) {
					outs = new SupplyZipOutput(outs);
					exec = new SequentialJoiningAggregator(db, spect, outs);
				// TODO ALL asdf
				} else {
					// TODO initial impl of single unbundled request
					// this is required because there is only one pipe and the seq exec calls begin unnecessarily
					exec = db.makeFlow(spect.getWellIDs(WellDataType.ALL).get(0), outs);
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
				if (logger.isDebugEnabled()) {
					dumpParameters(req);
				}
				// TODO this message should not be rendered to the request
				// TODO it could reveal too much detail to the user
				throw new ServletException(e);
			}
		}
		catch (InvalidParameterException ivp) {
			dumpParameters(req);
			throw ivp;
		} 
		finally {
			// TODO identify the request
			logger.info("Done with request for specifier");
		}
	}

	private void dumpParameters(HttpServletRequest req) {
		
		@SuppressWarnings("unchecked")
		List<String> names = Collections.list(req.getParameterNames());
		
		for (String pn : names) {
			logger.debug("param {}", pn);
			String[] vv = req.getParameterValues(pn);
			for (String v : vv) {
				logger.debug("{}: {}", pn, v);
			}
		}
		
	}

	protected Specification makeSpecification(HttpServletRequest req) {
		Specification spect = parseListOfWells(req);
		
		// if there is no wells list there surely should be a single well request
		if ( spect.isEmpty() ) {
			// TODO how do we what to handle the bundling
//			String bundled = req.getParameter(PARAM_BUNDLED);
//			spect.setBundled(bundled != null);
			spect = parseSpecifier(req);
		}
		
		parserBundling(req, spect);
		
		String encoding = req.getParameter(PARAM_ENCODING);
		if (encoding != null) {
			spect.setEncode( Encoding.valueOf(encoding) );
		}

		String beginDate = req.getParameter("beginDate");
		if (beginDate != null) {
			DateMidnight ld = new DateMidnight(beginDate);
			spect.setBeginDate(ld.toDate());
		}
		
		String endDate = req.getParameter("endDate");
		if (endDate != null) {
			DateMidnight ld = new DateMidnight(endDate);
			spect.setEndDate(ld.toDate());
		}
		
		// TODO parse out BBox and other timeSeriesQuery params
		
		precheckWells(spect);
		
		return spect;
	}

	protected void parserBundling(HttpServletRequest req, Specification spect) {
		
		boolean bundled
			=  ( null != req.getParameter(PARAM_BUNDLED) ) // specifically requested
			|| ( 1 < spect.getWellTotalSize() )
			|| ( 1 < spect.getDataTypes().size() );        // must bundle if multiple data elements
		
		spect.setBundled(bundled);
	}

	protected WellDataType[] parseDataTypes(HttpServletRequest req) {
		
		// TODO handle invalid types
		
		String[] typeIDs = req.getParameterValues(PARAM_TYPE);
		
		if (typeIDs==null) {
			// TODO This seems bogus. Change to new WellDataType[0]?
			return WellDataType.values();
		}
		
		WellDataType[] wtd = new WellDataType[typeIDs.length];
		int w=0;
		for (String typeID : typeIDs) {
			wtd[w++] = WellDataType.valueOf(typeID);
		}
		
		return wtd;
	}

	
	protected void precheckWells(Specification spect) throws SiteNotFoundException  {

		for (WellDataType type : spect.getDataTypes()) {
			List<Specifier> specs = spect.getWellIDs(type);
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
	}

	protected String wellname(Specifier spec) {
		return spec.getAgencyID() + "_" + spec.getFeatureID();
	}

	protected Specification parseListOfWells(HttpServletRequest req) {
		Specification spect = new Specification();
		
		WellDataType[] typeIDs = parseDataTypes(req);
		
		// TODO this is how we can enforce one agency?
		String agencyDefault = req.getParameter(PARAM_AGENCY);

		String[] wells = req.getParameterValues(PARAM_FEATURE);
		if (wells == null) return spect; // TODO I would rather there be only featureId instead of listOfWells

		RuntimeException re = null; // TODO should we allow one well to be bad or none?
		
		// construct the list of well specifiers
		for (String well : wells) {
			if (well==null) continue;

			try {
				// attempt to separate the agency and site IDs
				String[] specParts = well.split(":",2);
				boolean hasSeparator = well.contains(":");
				
				// list of wells might be a list of sites only or a list of agency:site combos
				String agencyID  = (specParts.length==1 && !hasSeparator) ? agencyDefault : specParts[0];
				
				// TODO with all this commentary it would be better to rewrite
				// if the well contains only one entry and a separator then the agency was given w/o feature
				// however, if there is no separator with one entry then the feature is given w/o agency
				String featureID = specParts.length==1 ? (hasSeparator ?  "" : specParts[0])  
						: specParts[1]; // otherwise, we had both agency and well with separator
								
				for (WellDataType typeID : typeIDs) {
					Specifier spec   = makeSpec(agencyID, featureID, typeID);
					if ( ! spect.addWell(spec)) {
						logger.warn("Duplicate well: {}", spec);
					}
				}
			} catch (RuntimeException e) {
				// TODO catching NPE and IPE for one entry but two is too many?
				// TODO this is a first blush impl
				if (re!=null) throw e;
				re = e;
			}
		}
		// TODO this is first blush response to an invalid specs
		// TODO allowing one bad well unless there are no specs left to fetch
		if (re!=null && spect.isEmpty()) {
			throw re;
		}
		return spect;
		
	}	
	
	/** Parse a specifier from the request.
	 * This may get arbitrarily complex, but the specifier should not be evaluated here.
	 * The specifier is a timeSeriesQuery and can be constructed without touching the cache.
	 * 
	 * @param req
	 * @return
	 */
	protected Specification parseSpecifier(HttpServletRequest req) {
		Specification spect    = new Specification();
		
		String agencyID        = req.getParameter(PARAM_AGENCY);
		String featureID       = req.getParameter(PARAM_FEATURE);
		WellDataType[] typeIDs = parseDataTypes(req);

		for (WellDataType typeID : typeIDs) {
			Specifier spec   = makeSpec(agencyID, featureID, typeID);
			spect.addWell(spec);
		}

		return spect;
	}

	protected Specifier makeSpec(String agency, String featureID, WellDataType type) {
		// TODO should we really default type?
		if (type == null) {
			type = WellDataType.LOG;
		}
		
		// TODO should we really default agency?
		if (agency == null) {
			agency = "USGS";
		}
		// TODO Find a better place for this hack - this might be incompatible with the spaces in the urls
		agency = agency.replace("_", " ");
		
		Specifier spec = new Specifier(agency, featureID, type);
		return spec;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
