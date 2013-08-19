package gov.usgs.ngwmn.sos;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author Bill Blondeau <wblondeau@usgs.gov>
 */
public class SOSServiceXMLExtractorTest {
	
	public static final String BASIC_BOUNDING_BOX = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<sos:GetFeatureOfInterest  service=\"SOS\" version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" xmlns:fes=\"http://www.opengis.net/fes/2.0\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:sams=\"http://www.opengis.net/spatialSampling/2.0\">\n" +
"       <sos:spatialFilter>\n" +
"             <fes:Intersects>\n" +
"                    <fes:ValueReference>sams:shape</fes:ValueReference>\n" +
"                    <gml:Envelope srsName=\"urn:ogc:def:crs:EPSG::4326\">\n" +
"                           <gml:lowerCorner>50.5 -116</gml:lowerCorner>\n" +
"                           <gml:upperCorner>51.6 -114.3</gml:upperCorner>\n" +
"                    </gml:Envelope>\n" +
"             </fes:Intersects>\n" +
"       </sos:spatialFilter>\n" +
"</sos:GetFeatureOfInterest>\n";

	public static final String SINGLE_FEATURE_OF_INTEREST =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<sos:GetFeatureOfInterest  service=\"SOS\" version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\">\n" +
"       <sos:featureOfInterest>ab.mon.45</sos:featureOfInterest>\n" +
"</sos:GetFeatureOfInterest>\n";
	
public static final String GET_OBSERVATION =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<sos:GetObservation\n" +
"       service=\"SOS\" version=\"2.0.0\"\n" +
"       xmlns:sos=\"http://www.opengis.net/sos/2.0\"\n" +
"       xmlns:fes=\"http://www.opengis.net/fes/2.0\"\n" +
"       xmlns:gml=\"http://www.opengis.net/gml/3.2\"\n" +
"       xmlns:swe=\"http://www.opengis.net/swe/2.0\"\n" +
"       xmlns:swes=\"http://www.opengis.net/swes/2.0\"\n" +
"       xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd http://www.opengis.net/gml/3.2\n" +
"http://schemas.opengis.net/gml/3.2.1/gml.xsd\">\n" +
"  <sos:offering>GW_LEVEL</sos:offering>\n" +
"  <sos:observedProperty>urn:ogc:def:phenomenon:OGC:1.0.30:groundwaterlevel</sos:observedProperty>\n" +
"   <sos:featureOfInterest>ab.mon.45</sos:featureOfInterest>\n" +
"</sos:GetObservation>";
	
	private SOSService victim;
	
	public SOSServiceXMLExtractorTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		victim = new SOSService();
		
	}
	
	private Document parseDocumentString (String xmlDoc) throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder bld = fac.newDocumentBuilder();
		return bld.parse(new ByteArrayInputStream(xmlDoc.getBytes()));
	}
	
	@Test
	public void testConstructor() throws Exception {
		
		Document doc = parseDocumentString(BASIC_BOUNDING_BOX);
		
		victim.new xmlParameterExtractor(doc);
	}
	
	@After
	public void tearDown() {
	}
	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	// @Test
	// public void hello() {}
}