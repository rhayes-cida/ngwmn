package gov.usgs.ngwmn.dm.spec;

import static org.junit.Assert.assertEquals;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.WellRegistry;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LatLongResolverTests {

	private List<WellRegistry> wellsList;
	private Specification spect;

	
	@Before
	public void setUp() throws Exception {
		spect      = new Specification();
		
		wellsList = new LinkedList<WellRegistry>();
		WellRegistry well;
		for (int w=0; w<3; w++) {
			well = new WellRegistry("a"+w,"s"+w,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
			wellsList.add(well);
		}
	}

	@After
	public void tearDown() throws Exception {
		spect     = null;
		wellsList = null;
	}

	@Test
	public void test_iterator() {
		LatLongResolver resolver = new LatLongResolver(null) {
			protected List<WellRegistry> fetchWells(Specification spec) {
				return wellsList;
			};
		};
		
		int count =0;
		for (Specifier sp : resolver.specIterator(spect, WellDataType.LOG)) {
			assertEquals("a"+count, sp.getAgencyID());
			count++;
		}
		assertEquals(wellsList.size(), count);
	}

}