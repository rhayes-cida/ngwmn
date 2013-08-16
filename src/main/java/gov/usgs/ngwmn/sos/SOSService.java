package gov.usgs.ngwmn.sos;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.TeeInputStream;
import gov.usgs.ngwmn.dm.io.transform.XSLFilterOutputStream;
import gov.usgs.ngwmn.dm.io.transform.XSLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
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

	static private Logger logger = LoggerFactory.getLogger(SOSService.class);

	private String baseURL;
	
	@RequestMapping(params={"REQUEST=GetCapabilities"})
	public void getCapabilities(
			OutputStream out
	) {
		// TODO deliver static file
		throw new NotImplementedException();
	}
	
	@RequestMapping(params={"REQUEST=GetObservation"})
	public void getObservation(
			@RequestParam String featureId,
			// @RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date startDate,
			// @RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date endDate,
			ServletContext ctx,
			HttpServletRequest request,
			HttpServletResponse response
			)
	{
		// TODO Implement by fetching from self-URL for raw data, passing thru wml1.9 to wml2 transform
		
		// raw data URL will be like /ngwmn_cache/data/$AGENCY/$SITE/WATERLEVEL
		// example http://cida-wiwsc-ngwmndev.er.usgs.gov:8080/ngwmn_cache/data/TWDB/6550504/WATERLEVEL
		RequestDispatcher dispatcher = ctx.getRequestDispatcher("/data/TWDB/6550504/WATERLEVEL");
		
		HttpServletRequest req = new HttpServletRequestWrapper(request) {

			@Override
			public String getHeader(String name) {
				// This only works 
				if ("Accept".equalsIgnoreCase(name)) {
					return "text/xml;subtype=WaterML/2.0";
				}
				return super.getHeader(name);
			}

			@Override
			public Enumeration getHeaderNames() {
				// TODO Do we need to override?
				return super.getHeaderNames();
			}

			@Override
			public Enumeration getHeaders(String name) {
				// TODO Do we need to override?
				return super.getHeaders(name);
			}
			
		};
		
		dispatcher.forward(request, response);
		
		// throw new NotImplementedException();
	}
	
	// TODO Add binding for XML document input 
	@RequestMapping(params={"!REQUEST"})
	public void operationByPost() {
		// TODO Parse XML request body, which will be something like <sos:GetObservation>
		throw new NotImplementedException();
	}

	
	// GetFeatureOfInterest
	// implement on the back of geoserver
	// two forms of filter: featureId or bounding box
	// example URL $BASE?REQUEST=GetFeatureOfInterest&VERSION=2.0.0&SERVICE=SOS&featureOfInterest=ab.mon.45
	@RequestMapping(params={"REQUEST=GetFeatureOfInterest"})
	public void getFOI_byId(
			@RequestParam(required=false) String featureId,
			@RequestParam(required=false) String spatialFilter,
			HttpServletResponse response
			)
		throws Exception
	{
		GeoserverFeatureSource featureSource = new GeoserverFeatureSource(getBaseURL());
		
		logger.info("GetFeatureOfInterest");
		
		// optional filters
		int filterCt = 0;
		
		if (featureId != null) {
			// TODO fix param name and perhaps value
			featureSource.addParameter("featureID", featureId);
			logger.debug("Added filter fid={}", featureId);
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
			String cql_filter = MessageFormat.format("(BBOX(GEOM,{1},{2},{3,{4}))",
					part[1],part[2], part[3], part[4]);
			String srsName = "EPSG:4326";
				
			featureSource.addParameter("srsName", srsName);
			featureSource.addParameter("CQL_FILTER", cql_filter);
			
			logger.debug("added spatial filter {} in srs {}", cql_filter, srsName);
			filterCt++;
		}
		
		if (filterCt == 0) {
			logger.warn("No filters for WFS request, may get lots of data");
		}
		
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
	
	
	// GetDataAvailability
	// later
	
	
}
