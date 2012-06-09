package gov.usgs.ngwmn.dm.cache.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class FileCache_FilenameTest extends FileCache {

	@Before
	public void setup() throws IOException {
		File tmp = new File("/tmp");
		File basedir = new File(tmp,"fscache_test_dir");
		basedir.mkdirs();
		setBasedir(basedir);
	}
	
	@Test
	public void testBaseContentFile() throws IOException {
		Specifier spec = new Specifier("SAFE","safe",WellDataType.LOG);
		
		File f = super.contentFile(spec);
		assertTrue("pre-existing condition", f.createNewFile()||f.exists());
	}

	@Test
	public void testUnsafeFeatureID() throws IOException {
		Specifier spec = new Specifier("SAFE",":very/unsafe/Feature!ID",WellDataType.LOG);
		
		File f = super.contentFile(spec);
		f.createNewFile();
		assertTrue("funny feature id", f.exists());
	}
	
	@Test
	public void testUnsafeAgencyID() throws IOException {
		Specifier spec = new Specifier("un/safe/\u0003Agency/ID","safe",WellDataType.LOG);
		
		File f = super.contentFile(spec);
		f.createNewFile();
		assertTrue("funny agency id", f.exists());
	}
	
	@Test
	public void testRepeatable() {
		Specifier spec = new Specifier("AGID","safe",WellDataType.LOG);
		
		File f1 = super.contentFile(spec);
		File f2 = super.contentFile(spec);
		
		assertEquals("same cache file", f1,f2);
	}

	@Test
	public void testUsesAgency() {
		Specifier spec = new Specifier("ONE","safe",WellDataType.LOG);
		File f1 = super.contentFile(spec);
		
		spec = new Specifier("TWO","safe",WellDataType.LOG);
		File f2 = super.contentFile(spec);
		
		assertFalse("should not be same cache file", f1.equals(f2));
	}
	
	@Test
	public void testUsesFeature() {
		Specifier spec = new Specifier("AGID","feature",WellDataType.LOG);
		File f1 = super.contentFile(spec);
		
		spec = new Specifier("AGID","creature",WellDataType.LOG);
		File f2 = super.contentFile(spec);
		
		assertFalse("should not be same cache file", f1.equals(f2));
		
	}
	
	@Test
	public void testUsesType() {
		Specifier spec = new Specifier("AGID","safe",WellDataType.WATERLEVEL);
		File f1 = super.contentFile(spec);
		
		spec = new Specifier("AGID","safe",WellDataType.QUALITY);
		File f2 = super.contentFile(spec);
		
		assertFalse("same cache file", f1.equals(f2));
		
	}
	
	@Test
	public void testPreservesSpace() {
		Specifier spec = new Specifier("AGID","safe name with space",WellDataType.WATERLEVEL);
		
		File f1 = super.contentFile(spec);
		assertTrue("feature ID is human-readable", f1.getName().contains(spec.getFeatureID()));
	}
	
}
