package gov.usgs.ngwmn.sos;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.sos.SOSService.XMLParameterExtractor;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

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
	
	public static final String MULTIPLE_FEATURE_OF_INTEREST =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<sos:GetFeatureOfInterest  service=\"SOS\" version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\">\n" +
"       <sos:featureOfInterest>ab.mon.45</sos:featureOfInterest>\n" +
"       <sos:featureOfInterest>ab.mon.46</sos:featureOfInterest>\n" +
"       <sos:featureOfInterest>ab.mon.47</sos:featureOfInterest>\n" +
"       <sos:featureOfInterest>ab.mon.48</sos:featureOfInterest>\n" +
"</sos:GetFeatureOfInterest>\n";
	
public static final String GET_OBSERVATION_1 =
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

public static final String GET_OBSERVATION_3 =
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
"   <sos:featureOfInterest>ab.mon.42</sos:featureOfInterest>\n" +
"   <sos:featureOfInterest>ab.mon.43</sos:featureOfInterest>\n" +
"   <sos:featureOfInterest>ab.mon.44</sos:featureOfInterest>\n" +
"</sos:GetObservation>";
	
	private SOSService victim;
	
	public SOSServiceXMLExtractorTest() {
	}
	
	@Before
	public void setUp() {
		victim = new SOSService();
		
	}
	
	private Document parseDocumentString (String xmlDoc) throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder bld = fac.newDocumentBuilder();
		return bld.parse(new ByteArrayInputStream(xmlDoc.getBytes()));
	}
	
	@Test
	public void testConstructor() throws Exception {
		
		Document doc = parseDocumentString(BASIC_BOUNDING_BOX);
		
		XMLParameterExtractor xpe = victim.new XMLParameterExtractor(doc);
		
		assertNotNull("XMLParameterExtractor", xpe);
	}
	
	@Test 
	public void testSOSRequest() throws Exception {
		Document doc;
		XMLParameterExtractor xpe;
		
		doc = parseDocumentString(BASIC_BOUNDING_BOX);
		xpe = victim.new XMLParameterExtractor(doc);
		assertEquals("BASIC_BOUNDING_BOX", "GetFeatureOfInterest", xpe.getSOSRequest());

		doc = parseDocumentString(SINGLE_FEATURE_OF_INTEREST);
		xpe = victim.new XMLParameterExtractor(doc);
		assertEquals("SINGLE_FEATURE_OF_INTEREST","GetFeatureOfInterest", xpe.getSOSRequest());

		doc = parseDocumentString(MULTIPLE_FEATURE_OF_INTEREST);
		xpe = victim.new XMLParameterExtractor(doc);
		assertEquals("MULTIPLE_FEATURE_OF_INTEREST","GetFeatureOfInterest", xpe.getSOSRequest());

		doc = parseDocumentString(GET_OBSERVATION_1);
		xpe = victim.new XMLParameterExtractor(doc);
		assertEquals("GET_OBSERVATION","GetObservation", xpe.getSOSRequest());
	}
	
	@Test
	public void testSOSFeatures() throws Exception {
		Document doc;
		XMLParameterExtractor xpe;
		List<String> features;
		
		doc = parseDocumentString(BASIC_BOUNDING_BOX);
		xpe = victim.new XMLParameterExtractor(doc);
		features = xpe.getSOSFeatures();
		assertEquals("BASIC_BOUNDING_BOX", 0, features.size());

		doc = parseDocumentString(SINGLE_FEATURE_OF_INTEREST);
		xpe = victim.new XMLParameterExtractor(doc);
		features = xpe.getSOSFeatures();
		{
			String[] expected = {"ab.mon.45"};
			assertEquals("SINGLE_FEATURE_OF_INTEREST", Arrays.asList(expected), features);
		}
		
		doc = parseDocumentString(MULTIPLE_FEATURE_OF_INTEREST);
		xpe = victim.new XMLParameterExtractor(doc);
		features = xpe.getSOSFeatures();
		{
			String[] expected = {"ab.mon.45","ab.mon.46","ab.mon.47","ab.mon.48"};
			assertEquals("MULTIPLE_FEATURE_OF_INTEREST", Arrays.asList(expected), features);
		}

		doc = parseDocumentString(GET_OBSERVATION_1);
		xpe = victim.new XMLParameterExtractor(doc);
		features = xpe.getSOSFeatures();
		{
			String[] expected = {"ab.mon.45"};
			assertEquals("GET_OBSERVATION_1", Arrays.asList(expected), features);
		}
		
		doc = parseDocumentString(GET_OBSERVATION_3);
		xpe = victim.new XMLParameterExtractor(doc);
		features = xpe.getSOSFeatures();
		{
			String[] expected = {"ab.mon.42","ab.mon.43","ab.mon.44"};
			assertEquals("GET_OBSERVATION_3", Arrays.asList(expected), features);
		}
	}
	
	@Test
	public void testSOSBoundingBox() throws Exception {
		Document doc;
		XMLParameterExtractor xpe;
		
		doc = parseDocumentString(BASIC_BOUNDING_BOX);
		xpe = victim.new XMLParameterExtractor(doc);
		assertEquals("BASIC_BOUNDING_BOX features", 0, xpe.getSOSFeatures().size());
		BoundingBox bbox = xpe.getSOSBoundingBox();
		assertNotNull("BASIC_BOUNDING_BOX value", bbox);
		assertEquals("BASIC_BOUNDING_BOX srsName", "urn:ogc:def:crs:EPSG::4326", bbox.getSrsName());
		String coords[] = {"50.5","-116","51.6","-114.3"};
		assertArrayEquals("BASIC_BOUNDING_BOX coords", coords, bbox.getCoordinates());
	}
	
	// negative tests
	@Test(expected=IllegalArgumentException.class)
	public void testBadSpatialFilter() throws Exception{
		String boundingBox = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<sos:GetFeatureOfInterest  service=\"SOS\" version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" xmlns:fes=\"http://www.opengis.net/fes/2.0\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:sams=\"http://www.opengis.net/spatialSampling/2.0\">\n" +
				"       <sos:spatialFilter>\n" +
				"             <fes:IsCloseTo>\n" +
				"                    <fes:ValueReference>sams:shape</fes:ValueReference>\n" +
				"                    <gml:Envelope srsName=\"urn:ogc:def:crs:EPSG::4326\">\n" +
				"                           <gml:lowerCorner>50.5 -116</gml:lowerCorner>\n" +
				"                           <gml:upperCorner>51.6 -114.3</gml:upperCorner>\n" +
				"                    </gml:Envelope>\n" +
				"             </fes:IsCloseTo>\n" +
				"       </sos:spatialFilter>\n" +
				"</sos:GetFeatureOfInterest>\n";

		Document doc;
		XMLParameterExtractor xpe;
		
		doc = parseDocumentString(boundingBox);
		xpe = victim.new XMLParameterExtractor(doc);

		assertNull("bbox with bad path operator", xpe.getSOSBoundingBox());
	}
	
	@Test(expected=SAXParseException.class)
	public void testIllformedDocument() throws Exception {
		String illFormed = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<sos:GetFeatureOfInterest  service=\"SOS\" version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" xmlns:fes=\"http://www.opengis.net/fes/2.0\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:sams=\"http://www.opengis.net/spatialSampling/2.0\">\n" +
				"       <sos:spatialFilterNOT>\n" +
				"             <fes:IsCloseTo>\n" +
				"                    <fes:ValueReference>sams:shape</fes:ValueReference>\n" +
				"                    <gml:Envelope srsName=\"urn:ogc:def:crs:EPSG::4326\">\n" +
				"                           <gml:lowerCorner>50.5 -116</gml:lowerCorner>\n" +
				"                           <gml:upperCorner>51.6 -114.3</gml:upperCorner>\n" +
				"                    </gml:Envelope>\n" +
				"             </fes:IsCloseTo>\n" +
				"       </sos:spatialFilter>\n" +
				"</sos:GetFeatureOfInterest>\n";

		Document doc;
		XMLParameterExtractor xpe;
		
		doc = parseDocumentString(illFormed);
		xpe = victim.new XMLParameterExtractor(doc);

		assertNull("bbox with bad path operator", xpe.getSOSBoundingBox());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMissingSRSName() throws Exception {
		String noSRSName = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<sos:GetFeatureOfInterest  service=\"SOS\" version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" xmlns:fes=\"http://www.opengis.net/fes/2.0\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:sams=\"http://www.opengis.net/spatialSampling/2.0\">\n" +
				"       <sos:spatialFilter>\n" +
				"             <fes:Intersects>\n" +
				"                    <fes:ValueReference>sams:shape</fes:ValueReference>\n" +
				"                    <gml:Envelope>\n" +
				"                           <gml:lowerCorner>50.5 -116</gml:lowerCorner>\n" +
				"                           <gml:upperCorner>51.6 -114.3</gml:upperCorner>\n" +
				"                    </gml:Envelope>\n" +
				"             </fes:Intersects>\n" +
				"       </sos:spatialFilter>\n" +
				"</sos:GetFeatureOfInterest>\n";
		Document doc;
		XMLParameterExtractor xpe;
		
		doc = parseDocumentString(noSRSName);
		xpe = victim.new XMLParameterExtractor(doc);

		assertNull("missing SRS name", xpe.getSOSBoundingBox());

	}

}