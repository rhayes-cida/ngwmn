package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrefetchControllerIntegrationTest extends ContextualTest {

	private PrefetchController victim;
	
	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean(PrefetchController.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStop() {
		victim.stop();
		assertTrue("survived", true);
	}

	@Test
	public void testStart() {
		if ( ! victim.isEnabled()) {
			victim.enable();
		}
		victim.start();
		assertTrue("survived", true);
	}

	@Test
	public void testEnable() {
		victim.enable();
		assertTrue("survived", true);
		
		assertTrue("enabled", victim.isEnabled());
	}

}
