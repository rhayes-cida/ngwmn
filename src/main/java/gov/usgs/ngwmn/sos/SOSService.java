package gov.usgs.ngwmn.sos;

import gov.usgs.ngwmn.NotImplementedException;

import java.io.OutputStream;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("sos")
public class SOSService {

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
	@RequestMapping(params={"REQUEST=GetFeatureOfInterest","featureOfInterest"})
	public void getFOI_byId(
			@RequestParam String featureId,
			HttpServletResponse response
			) {

	}
	
	// example URL $BASE?REQUEST=GetFeatureOfInterest&VERSION=2.0.0&SERVICE=SOS&spatialFilter=om:featureOfInterest/*/sams:shape,-116,50.5,-114.3,51.6&namespaces=xmlns(sams,http://www.opengis.net/samplingSpatial/2.0),xmlns(om,http://www.opengis.net/om/2.0)
	@RequestMapping(params={"REQUEST=GetFeatureOfInterest","spatialFilter"})
	public void getFOI_bySpatialFilter(
			@RequestParam String spatialFilter,
			HttpServletResponse response
			) {
		String[] part = spatialFilter.split(",");
		if ( ! "om:featureOfInterest/*/sams:shape".equals(part[0]) ) {
			throw new RuntimeException("bad filter");
		}
		for (int i = 1; i <= 4; i++) {
			Double.parseDouble(part[i]);
		}
		
		// generate params for GeoServer WFS request, example:
/*		SERVICE:WFS
		VERSION:1.0.0
		srsName:EPSG:4326
		outputFormat:GML2
		typeName:ngwmn:VW_GWDP_GEOSERVER
		CQL_FILTER:((QW_SN_FLAG = '1') OR (WL_SN_FLAG = '1')) AND (BBOX(GEOM,-101.333008,34.269568,-99.838867,35.459421))
		*/
		// use the original input strings for fidelity
		String cql_filter = MessageFormat.format("(BBOX(GEOM,{1},{2},{3,{4}))",
				part[1],part[2], part[3], part[4]);
		String srsName = "EPSG:4326";
		
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod("http://cida-wiwsc-ngwmndev.er.usgs.gov:8080/ngwmn/geoserver/wfs?request=GetFeature");
		method.addParameter("SERVICE", "WFS");
		method.addParameter("VERSION", "1.0.0");
		method.addParameter("outputFormat","GML2");
		method.addParameter("typeName","ngwmn:VW_GWDP_GEOSERVER");
		
		// variable parameters
		method.addParameter("srsName", srsName);
		method.addParameter("CQL_FILTER", cql_filter);

		try {
			int statusCode = client.executeMethod(method);
			
			if (statusCode != HttpStatus.SC_OK) {
				throw new RuntimeException("status " + statusCode);
			}
			
			InputStream stream = method.getResponseBodyAsStream();
			
			// copy from stream to response, filtering through xsl transform
		}
		finally {
			method.releaseConnection();
		}
	}
	
	
	// GetDataAvailability
	// later
}
