package gov.usgs.ngwmn.dm.prefetch;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.PrefetchI;
import gov.usgs.ngwmn.dm.dao.ContextualTest;
import gov.usgs.ngwmn.dm.spec.Specifier;

import org.junit.Before;
import org.junit.Test;

public class PrefetcherTest extends ContextualTest {

	private Prefetcher victim;
	private int ct = 0;
	private int delay = 0;
	
	@Before
	public void setup() {
		victim = ctx.getBean("Prefetcher", Prefetcher.class);
		victim.setBroker(new PrefetchI() {
			
			@Override
			public long prefetchWellData(Specifier spec) throws Exception {
				ct++;
				if (delay > 0) {
					Thread.sleep(delay);
				}
				return spec.hashCode();
			}
		});
	}
	
	@Test
	public void test_count() {
		ct = 0;
		delay = 0;
		victim.setTimeLimit(null);
		victim.setFetchLimit(100);
		PrefetchOutcome outcome = 
				victim.call();
		assertTrue("tried lots", ct > 99);
		assertEquals(PrefetchOutcome.LIMIT_COUNT, outcome);
	}
	
	@Test
	public void test_time() {
		ct = 0;
		delay = 10;
		victim.setTimeLimit(100L);
		victim.setFetchLimit(1000000);
		PrefetchOutcome outcome =
				victim.call();
		assertTrue("tried a few", ct < 20);
		assertEquals(PrefetchOutcome.LIMIT_TIME, outcome);
	}

	@Test
	public void test_all() {
		ct = 0;
		delay = 0;
		victim.setTimeLimit(null);
		victim.setFetchLimit(0);
		PrefetchOutcome outcome =
				victim.call();
		assertTrue("tried lots and lots", ct > 500);
		assertEquals(PrefetchOutcome.FINISHED, outcome);
	}

}
