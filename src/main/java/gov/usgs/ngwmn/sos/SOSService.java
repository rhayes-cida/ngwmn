package gov.usgs.ngwmn.sos;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.TeeInputStream;
import gov.usgs.ngwmn.dm.io.transform.XSLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.io.CountingInputStream;
import com.google.common.io.CountingOutputStream;

@Controller
public class SOSService {

	public static final String FEATURE_PREFIX = "VW_GWDP_GEOSERVER";

	static private Logger logger = LoggerFactory.getLogger(SOSService.class);

	private String baseURL;
	
	@RequestMapping(params={"REQUEST=GetCapabilities"})
	public void getCapabilities(
			OutputStream out
	) {
		// TODO deliver static file
		throw new NotImplementedException();
	}
	
	@RequestMapping(params={"!REQUEST"})
	public void processXmlParams(
			HttpServletRequest request,
			HttpServletResponse response
	) {
		// TODO parse parameters out of XML input, call methods in this class
		throw new NotImplementedException();
	}
	
	
	@RequestMapping(params={"REQUEST=GetObservation"})
	public void getObservation(
			@RequestParam String featureOfInterest,
			// @RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date startDate,
			// @RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date endDate,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception
	{
		// Implement by fetching from self-URL for raw data, passing thru wml1.9 to wml2 transform
		
		String baseURL = "http" + "://" + request.getLocalName() + ":" + request.getLocalPort() + "/" + request.getContextPath();
		
		SiteID site = SiteID.fromFid(featureOfInterest);
		
		Waterlevel19DataSource source = new Waterlevel19DataSource(baseURL, site.agency, site.site);
		
		try {
			InputStream is = source.getStream();
			
			response.setContentType("text/xml");
			OutputStream os = response.getOutputStream();

			// copy from stream to response, filtering through xsl transform
			copyThroughTransform(is,os, "/gov/usgs/ngwmn/wl2waterml2.xslt");
			logger.debug("done");
		}
		catch (Exception e) {
			logger.warn("Problem", e);
			throw e;
		}
		finally {
			source.close();
		}
	}
	
	// TODO Add binding for XML document input 
	
	// TODO Make param names case-insensitive (might require use of filter)
	
	// GetFeatureOfInterest
	// implement on the back of geoserver
	// two forms of filter: featureId or bounding box
	// example URL $BASE?REQUEST=GetFeatureOfInterest&VERSION=2.0.0&SERVICE=SOS&featureOfInterest=ab.mon.45
	@RequestMapping(params={"REQUEST=GetFeatureOfInterest"})
	public void getFOI_byId(
			@RequestParam(required=false) String featureOfInterest,
			@RequestParam(required=false) String spatialFilter,
			@RequestParam(required=false, defaultValue="EPSG:4326") String srsName,
			HttpServletResponse response
			)
		throws Exception
	{
		GeoserverFeatureSource featureSource = new GeoserverFeatureSource(getBaseURL());
		
		logger.info("GetFeatureOfInterest");
		
		// optional filters
		int filterCt = 0;
		
		if (featureOfInterest != null) {
			SiteID site = SiteID.fromFid(featureOfInterest);
			logger.debug("Filter for site {}", site);
			
			featureSource.addParameter("featureID", featureOfInterest);
			logger.debug("Added filter featureID={}", featureOfInterest);
			filterCt++;
		}
		
		if (spatialFilter != null) {
			String[] part = spatialFilter.split(",");
			if ( ! "om:featureOfInterest/*/sams:shape".equals(part[0]) ) {
				throw new RuntimeException("bad filter");
			}
			for (int i = 1; i <= 4; i++) {
				// just for validation
				Double.parseDouble(part[i]);
			}
		
			// extra params for GeoServer WFS request, example:
			// use the original input strings for fidelity
			String cql_filter = MessageFormat.format("(BBOX(GEOM,{0},{1},{2},{3}))",
					part[1],part[2], part[3], part[4]);
			srsName = "EPSG:4326";
				
			featureSource.addParameter("srsName", srsName);
			featureSource.addParameter("CQL_FILTER", cql_filter);
			
			logger.debug("added spatial filter {} in srs {}", cql_filter, srsName);
			filterCt++;
		}
		
		if (filterCt == 0) {
			logger.warn("No filters for WFS request, may get lots of data");
		}
		
		// TODO It seems that geoserver may not accept the two filters in conjunction
		
		try {
			InputStream is = featureSource.getStream();
			
			response.setContentType("text/xml");
			OutputStream os = response.getOutputStream();

			// copy from stream to response, filtering through xsl transform
			copyThroughTransform(is,os, "/gov/usgs/ngwmn/geoserver-2-sos.xsl");
			logger.debug("done");
		}
		catch (Exception e) {
			logger.warn("Problem", e);
			throw e;
		}
		finally {
			featureSource.close();
		}
	}
	
	public static void copyThroughTransform(InputStream is, OutputStream os,
			String xformName) throws IOException, TransformerException {
		XSLHelper xslHelper = new XSLHelper();
		xslHelper.setTransform(xformName);
		
		CountingInputStream countingIs = null;
		TeeInputStream teeIs = null;
		CountingOutputStream countingOs = null;
		
		if (logger.isDebugEnabled()) {
			countingIs = new CountingInputStream(is);
			is = countingIs;
			
			countingOs = new CountingOutputStream(os);
			os = countingOs;
		}
		
		if (logger.isTraceEnabled()) {
			File tOut = File.createTempFile("geoserver",".xml");
			logger.info("Saving a copy of geo-output to {}", tOut);
			FileOutputStream fos = new FileOutputStream(tOut);
			teeIs = new TeeInputStream(is, fos, true);
			is = teeIs;
		}
		
		Transformer t = xslHelper.getTemplates().newTransformer();
		StreamResult result = new StreamResult(os);
		StreamSource source = new StreamSource(is);	

		t.transform(source, result);
		
		if (countingIs != null) {
			logger.debug("Processed {} bytes of input", countingIs.getCount());
		}
		if (countingOs != null) {
			logger.debug("Got {} bytes of output", countingOs.getCount());
		}
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
		logger.info("Will use base URL {}", this.baseURL);
	}
	
	public static class SiteID {
		public final String agency;
		public final String site;
		
		// fid is like VW_GWDP_GEOSERVER.NJGS.2288614
		// site id is like NJGS:2288614
		// (agency:site)

		public SiteID(String agency, String site) {
			super();
			this.agency = agency;
			this.site = site;
		}
		
		public String getFid() {
			return FEATURE_PREFIX + "." + agency + "." + site;
		}
		
		public String toString() {
			return agency + ":" + site;
		}
		
		public static SiteID fromFid(String fid) {
			String[] parts = fid.split("\\.");
			// Check first part
			if ( ! FEATURE_PREFIX.equals(parts[0])) {
				throw new IllegalArgumentException("Expected " + FEATURE_PREFIX + ", got " + parts[0]);
			}
			return new SiteID(parts[1], parts[2]);
		}
		
		public static SiteID fromID(String siteId) {
			String[] parts = siteId.split(":");
			return new SiteID(parts[0], parts[1]);
		}
	}
	

	// GetDataAvailability
	// later
	
	
}
