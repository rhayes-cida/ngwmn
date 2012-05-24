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
	
	@Before
	public void setup() {
		victim = ctx.getBean("Prefetcher", Prefetcher.class);
		victim.setBroker(new PrefetchI() {
			
			@Override
			public long prefetchWellData(Specifier spec) throws Exception {
				ct++;
				return spec.hashCode();
			}
		});
	}
	
	@Test
	public void test() {
		victim.run();
		assertTrue("tried lots", ct > 99);
	}

}
