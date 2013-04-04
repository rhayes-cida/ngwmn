package gov.usgs.ngwmn.dm.io;

import static org.junit.Assert.*;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.io.aggregate.Flow;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QualityQueryFlowFactoryTest extends ContextualTest {

	private QualityQueryFlowFactory victim;
	
	@Before
	public void setUp() throws Exception {
		victim = new QualityQueryFlowFactory();
		
		victim.setDatasource(getDataSource());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeFlow() throws Exception {
		Specifier spec = new Specifier("USGS","403948085414601",WellDataType.QUALITY);

		Supplier<OutputStream> out = new SimpleSupplier<OutputStream>(System.out);
		Flow x = victim.makeFlow(spec, out);
		
		x.call();
		
		assertTrue("pass", true);
	}

}
