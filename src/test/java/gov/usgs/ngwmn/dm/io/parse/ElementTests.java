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

}
