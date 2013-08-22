package gov.usgs.ngwmn.ogc;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.TeeInputStream;
import gov.usgs.ngwmn.dm.io.transform.XSLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
public class WFSService {

	public static final String FEATURE_PREFIX = "VW_GWDP_GEOSERVER";
	
	static private Logger logger = LoggerFactory.getLogger(WFSService.class);

	private String geoserverURL;
	
	@RequestMapping(params={"REQUEST=GetCapabilities"})
	public void getCapabilities(
			OutputStream out
	) {
		// TODO deliver static file
		throw new NotImplementedException();
	}
	
	// GetFeatureOfInterest
	// implement on geoserver, with output transformed to gwml
	@RequestMapping(params={"REQUEST=GetFeature"})
	public void getFeature(
			@RequestParam("FID") String featureOfInterest,
			HttpServletResponse response
			)
		throws Exception
	{
		GeoserverFeatureSource featureSource = new GeoserverFeatureSource(getGeoserverURL());
		
		logger.info("GetFeature");
		
		SiteID site = SiteID.fromFid(featureOfInterest);
		logger.debug("Filter for site {}", site);
		
		featureSource.addParameter("featureID", featureOfInterest);
		logger.debug("Added filter featureID={}", featureOfInterest);
		
		try {
			InputStream is = featureSource.getStream();
			
			response.setContentType("text/xml");
			OutputStream os = response.getOutputStream();

			// copy from stream to response, filtering through xsl transform
			copyThroughTransform(is,os, "/gov/usgs/ngwmn/geoserver-2-gwml.xsl");
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

	public String getGeoserverURL() {
		return geoserverURL;
	}

	public void setGeoserverURL(String gsURL) {
		this.geoserverURL = gsURL;
		logger.info("Will use geoserver URL {}", this.geoserverURL);
	}
	
}
