package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PrefetchControllerIntegrationTest extends ContextualTest {

	private PrefetchController victim;
	
	@BeforeClass
	public static void setEnvironment() {
		System.setProperty("ngwmn_prefetch_count_limit", "3");
		System.setProperty("ngwmn_prefetch_ms_limit","2000000");
	}
	
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

	@Test
	public void testMulti() {
		List<Future<PrefetchOutcome>> started = victim.startInParallel();
		assertNotNull(started);
		for (Future<PrefetchOutcome> oc : started) {
			try {
				System.out.printf("Done, %s\n", oc.get());
			}
			catch (Exception e) {
				System.err.printf("Exception %s\n", e);
			}
		}
		// poll for one fetch to finish
		while (true) {
			System.out.println("Trolling for one task to finish");
			int finct = 0;
			for (Future<PrefetchOutcome> oc : started) {
				if (oc.isDone()) {
					finct++;
				}
			}
			System.out.printf("Trolling, finct=%d\n", finct);
			if (finct > 0) {
				break;
			}
			// give them a chance to get something done
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		for (Future<PrefetchOutcome> oc : started) {
			oc.cancel(true);
			assertTrue("cancelled or finished one", oc.isDone());
		}
	}
}
