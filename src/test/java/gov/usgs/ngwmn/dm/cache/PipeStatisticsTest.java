package gov.usgs.ngwmn.dm.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;

import org.junit.Before;
import org.junit.Test;
public class PipeStatisticsTest {

	private PipeStatistics stats;
	
	@Before
	public void setup() {
		stats = new PipeStatistics();
		assertEquals("coun beforet",0, stats.getCount());
	}
	
	@Test
	public void testIncrementCount() {
		stats.incrementCount(999);
		stats.incrementCount(21);
		
		assertEquals("count",1020, stats.getCount());
	}

	@Test
	public void testSetStatus() {
		stats.setStatus(Status.OPEN);
		assertNull("pre-finish end time", stats.getEnd());
		assertFalse(stats.isDone());
		
		stats.markStart();
		stats.markEnd(Status.FAIL);
		assertNotNull("post-finish done time", stats.getEnd());
		
		assertTrue(stats.isDone());
	}

	@Test
	public void testGetElapsedMSec() throws Exception {
		assertNull("initial elapsed", stats.getElapsedMSec());
		
		stats.markStart();
		assertNull("elapsed time afgter start", stats.getElapsedMSec());
		
		Thread.sleep(200);
		stats.markEnd(Status.DONE);
		
		Long et = stats.getElapsedMSec();
		assertNotNull("elapsed time after finish", et);
		assertTrue("elapsed time is reasonable",et >= 200);
	}

	@Test
	public void test4char() {
		assertEquals("DONE", Status.DONE.as4Char());
		assertEquals("FAIL", Status.FAIL.as4Char());
		assertEquals("OPEN", Status.OPEN.as4Char());
		assertEquals("STAR", Status.STARTED.as4Char());
		
		assertEquals(Status.DONE, Status.by4Char("DONE"));
		assertEquals(Status.FAIL, Status.by4Char("FAIL"));
		assertEquals(Status.OPEN, Status.by4Char("OPEN"));
		assertEquals(Status.STARTED, Status.by4Char("STAR"));
	}
	
	@Test(expected=RuntimeException.class)
	public void test_markEnd_whenNotStarted_throws() {
		stats.markEnd(Status.DONE);
	}
	
	@Test(expected=RuntimeException.class)
	public void test_markStart_whenNotOpen_throws() {
		boolean fine = false;
		try {
			stats.markStart();
			stats.markEnd(Status.DONE);
			fine = true;
		} finally {
			assertTrue("the initial setup should not throw",fine);
		}
		// this should throw
		stats.markStart();
	}
	
	
	@Test
	public void test_setStatusDone_markEndCalled() {
		final Set<String> called = new HashSet<String>();
		
		stats = new PipeStatistics() {
			@Override
			public synchronized void markEnd(Status endStatus) {
				called.add("markEnd");
			}
		};
		stats.setStatus(Status.DONE);
		assertTrue("expect markEnd to be called on setStatus done", called.contains("markEnd"));
	}
}
