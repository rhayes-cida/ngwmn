package gov.usgs.ngwmn.dm.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;

import org.junit.Before;
import org.junit.Test;
public class PipeStatisticsTest {

	private PipeStatistics victim;
	
	@Before
	public void setup() {
		victim = new PipeStatistics();
		assertEquals("coun beforet",0, victim.getCount());
	}
	
	@Test
	public void testIncrementCount() {
		victim.incrementCount(999);
		victim.incrementCount(21);
		
		assertEquals("count",1020, victim.getCount());
	}

	@Test
	public void testSetStatus() {
		victim.setStatus(Status.OPEN);
		assertNull("pre-finish end time", victim.getEnd());
		assertFalse(victim.isDone());
		
		victim.markStart();
		victim.markEnd(Status.FAIL);
		assertNotNull("post-finish done time", victim.getEnd());
		
		assertTrue(victim.isDone());
	}

	@Test
	public void testGetElapsedMSec() throws Exception {
		assertNull("initial elapsed", victim.getElapsedMSec());
		
		victim.markStart();
		assertNull("elapsed time afgter start", victim.getElapsedMSec());
		
		Thread.sleep(200);
		victim.markEnd(Status.DONE);
		
		Long et = victim.getElapsedMSec();
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
}
