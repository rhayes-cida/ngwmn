package gov.usgs.ngwmn.dm.cache;

import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

public class SpecifierTests {

	Specifier spec;
	
	@Before
	public void setUp() {
		spec = new Specifier();
		spec.setAgencyID("agency");
		spec.setFeatureID("well");
		spec.setTypeID(WellDataType.LOG);
	}
	
	@Test
	public void test_check_noEmptyFeatureId() {
		spec.setFeatureID("");
		try {
			spec.check();
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (InvalidParameterException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void test_check_noEmptyAgencyId() {
		spec.setAgencyID("");
		try {
			spec.check();
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (InvalidParameterException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void test_check_notNullAgencyId() {
		spec.setAgencyID(null);
		try {
			spec.check();
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (InvalidParameterException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void test_check_noNullTypeId() {
		spec.setTypeID(null);
		try {
			spec.check();
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (InvalidParameterException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void test_check_noNullFeatureId() {
		spec.setFeatureID(null);
		try {
			spec.check();
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (InvalidParameterException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


}
