package gov.usgs.ngwmn.sos;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.transform.XSLHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

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

@Controller
public class SOSService {

	static private Logger logger = LoggerFactory.getLogger(SOSService.class);

	@RequestMapping(params={"REQUEST=GetCapabilities"})
	public void getCapabilities(
			OutputStream out
	) {
		throw new NotImplementedException();
	}
	
	@RequestMapping(params={"REQUEST=GetObservation"})
	public void getObservation(
			@RequestParam String featureId,
			// @RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date startDate,
			// @RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date endDate,
			HttpServletResponse response
			)
	{
		
	}
	
	// GetFeatureOfInterest
	// implement on the back of geoserver
	// two forms of input: featureId or bounding box
	// example URL $BASE?REQUEST=GetFeatureOfInterest&VERSION=2.0.0&SERVICE=SOS&featureOfInterest=ab.mon.45
	@RequestMapping(params={"REQUEST=GetFeatureOfInterest","featureId"})
	public void getFOI_byId(
			@RequestParam String featureId,
			HttpServletResponse response
			)
		throws Exception
	{
		GeoserverFeatureSource featureSource = new GeoserverFeatureSource();
		
		logger.info("GetFeatureOfInterest,featureId={}", featureId);
		
		// variable parameters
		// TODO fix param name and perhaps value
		featureSource.addParameter("featureOfInterest", featureId);

		try {
			InputStream is = featureSource.getStream();
			
			response.setContentType("text/xml");
			OutputStream os = response.getOutputStream();

			// copy from stream to response, filtering through xsl transform
			copyThroughTransform(is,os, "/gov/usgs/ngwmn/hydrograph.xsl");
		}
		finally {
			featureSource.close();
		}
	}
	
	// example URL $BASE?REQUEST=GetFeatureOfInterest&VERSION=2.0.0&SERVICE=SOS&spatialFilter=om:featureOfInterest/*/sams:shape,-116,50.5,-114.3,51.6&namespaces=xmlns(sams,http://www.opengis.net/samplingSpatial/2.0),xmlns(om,http://www.opengis.net/om/2.0)
	@RequestMapping(params={"REQUEST=GetFeatureOfInterest","spatialFilter"})
	public void getFOI_bySpatialFilter(
			@RequestParam String spatialFilter,
			HttpServletResponse response
			)  throws Exception
	{
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
		
		GeoserverFeatureSource featureSource = new GeoserverFeatureSource();
		
		// variable parameters
		featureSource.addParameter("srsName", srsName);
		featureSource.addParameter("CQL_FILTER", cql_filter);

		try {
			InputStream is = featureSource.getStream();
			
			response.setContentType("text/xml");
			OutputStream os = response.getOutputStream();

			// copy from stream to response, filtering through xsl transform
			copyThroughTransform(is,os, "/gov/usgs/ngwmn/hydrograph.xsl");
		}
		finally {
			featureSource.close();
		}
	}

	public static void copyThroughTransform(InputStream is, OutputStream os,
			String xformName) throws IOException, TransformerException {
		XSLHelper xslHelper = new XSLHelper();
		xslHelper.setTransform(xformName);
		
		Transformer t = xslHelper.getTemplates().newTransformer();
		StreamResult result = new StreamResult(os);
		StreamSource source = new StreamSource(is);	

		t.transform(source, result);

	}
	
	
	// GetDataAvailability
	// later
}
