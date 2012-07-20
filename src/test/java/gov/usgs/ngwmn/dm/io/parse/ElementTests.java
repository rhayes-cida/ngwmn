package gov.usgs.ngwmn.dm.io.parse;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.io.parse.Element;

import org.junit.Test;

public class ElementTests {

	@Test
	public void test_equals_nullIsNotInstanceOfElement() {
		Element element = new Element("full", "local", "displayName");
		assertFalse("null is not an instanceof element", element.equals(null));
		String other = null;
		assertFalse("null is not an instanceof element", element.equals(other));
	}

	@Test
	public void testCamel() {
		assertNull(Element.camelcase(null));
		assertEquals("ThisIsOK", Element.camelcase("this_is_O_K"));
		assertEquals("Alright", Element.camelcase("alRight"));
		assertEquals("",Element.camelcase(""));
		assertEquals("",Element.camelcase("_"));
		assertEquals("",Element.camelcase("__"));
		assertEquals("TwoWords", Element.camelcase("TWO____WORDS___"));
		assertEquals("Oneword", Element.camelcase("____oneword__"));
	}
}
