package gov.usgs.ngwmn;

import static org.junit.Assert.*;

import org.junit.Test;

public class WaterlevelMediatorTest {

	@Test
	public void testBetween() {
		assertTrue(WaterlevelMediator.between(null, "a", null));
		assertTrue(WaterlevelMediator.between("2000", "2001", "2003"));
		assertFalse(WaterlevelMediator.between("2000", "2000", "2000"));
		assertTrue(WaterlevelMediator.between("", "a", "b"));
		assertTrue(WaterlevelMediator.between("aaadc", "aaadc", "aaadc1"));
		assertTrue(WaterlevelMediator.between("a", "a", null));
		assertFalse(WaterlevelMediator.between(null, "a", "a"));
	}
	
	@Test
	public void testExpectedUsage() {
		assertFalse(WaterlevelMediator.between("2011-01-04", "2011-01-03", "2011-01-12"));
		assertFalse(WaterlevelMediator.between("2011-01-04", "2011-01-13", "2011-01-12"));
		assertTrue(WaterlevelMediator.between("2011-01-04", "2011-01-09", "2011-01-12"));		
	}

}
