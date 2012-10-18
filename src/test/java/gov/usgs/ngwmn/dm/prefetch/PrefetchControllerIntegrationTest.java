package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PrefetchControllerIntegrationTest extends ContextualTest {

	private PrefetchController victim;
	
	@BeforeClass
	public static void setEnvironment() {
		System.setProperty("ngwmn_prefetch_count_limit", "3");
		System.setProperty("ngwmn_prefetch_ms_limit","20000");
		
	}
	
	@Before
	public void setUp() throws Exception {
		// Have to load my own so the system properties take effect.
		ApplicationContext myCtx = new ClassPathXmlApplicationContext("applicationContextTest.xml");
		victim = myCtx.getBean(PrefetchController.class);
		victim.setApplicationContext(myCtx);
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

	@Test
	public void testMulti() {
		Map<String, Future<PrefetchOutcome>> started = victim.startInParallel();
		
		assertNotNull(started);
		
		Collection<Future<PrefetchOutcome>> futures = null;
		synchronized (started) {
			futures = started.values();
		}
		
		// poll for all fetches to finish			
		while ( ! victim.checkOutcomes()) {
			System.out.println("Trolling for tasks to finish");
			// give them a chance to get something done
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}				
		}
		System.out.printf("Trolling done\n");
		
		for (Future<PrefetchOutcome> oc : futures) {
			oc.cancel(true);
			assertTrue("cancelled or finished one", oc.isDone());
		}
	}
}
