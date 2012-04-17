package gov.usgs.ngwmn.functional;

import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.spec.LatLongResolver;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LatLongResolverIntegrationTests  extends ContextualTest {

	private LatLongResolver resolver;
	private Specification   spec;
	
	@Before
	public void setUp() throws Exception {
		spec = new Specification();
		
		resolver = ctx.getBean("LatLongResolver", LatLongResolver.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_iterator_continentalUS() {
/*		
		Latitude/Longitude Coordinate Ranges within the continental United States:
			Latitude values range from 24 to 49 degrees.
			Longitude values range from -65 to -124 degrees.
			Alaska Latitude values range from 50 to 71 and Longitude from -129 to -168.
*/
		spec.setLatitudeNorth(49);
		spec.setLatitudeSouth(24);
		spec.setLongitudeEast(-65);
		spec.setLongitudeWest(-124);
		
		int count =0;
		for (Specifier sp : resolver.specIterator(spec)) {
			count += (sp != null) ? 1 : 0;
		}
		assertTrue(1 < count);
	}

}