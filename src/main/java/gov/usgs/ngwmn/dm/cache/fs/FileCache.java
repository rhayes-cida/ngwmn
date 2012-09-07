package gov.usgs.ngwmn.dm.cache.fs;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.io.FileInputInvoker;
import gov.usgs.ngwmn.dm.io.Invoker;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.TempfileOutputStream;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.NullOutputStream;

public class FileCache implements Cache {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private File basedir;

	public static final String BASEDIR_JNDI_NAME = "java:comp/env/GWDP/FileCache/basedir";
	
	private final WellDataType wdt;
	
	public FileCache() {
		this(WellDataType.ALL); // TODO ALL asdf
	}
	public FileCache(WellDataType type) {
		wdt = type;
	}
	
	@Override
	public WellDataType getDatatype() {
		return wdt;
	}

	private String filename(Specifier spec) {
		// Note that there is no requirement to decode a cache file name; 
		// having the file name be human-readable is helpful for debugging,
		// not required for system operation
		return spec.getAgencyID()+"_"+spec.getFeatureID()+"_"+spec.getTypeID();
	}
	
	private String safeFileName(Specifier spec) {
		String starter = filename(spec);
		String scrambled = DigestUtils.md5Hex(starter);
		return scrambled;
	}
	
	private boolean isSafeFilename(String fn) {
		return fn.matches("[A-Z a-z\\.0-9_-]+");
	}
	
	protected final File contentFile(Specifier spec) {
		
		if (isDisabled()) {
			logger.warn("Calling contentFile with disabled cache");
			return null;
		}
		
		String fname = filename(spec);
		if (fname.isEmpty()) {
			// can't happen, but let's be safe
			logger.error("Generated empty file name for spec {}", spec);
			throw new RuntimeException("Generated empty file name");
		}
		if ( ! isSafeFilename(fname)) {
			String sfname = safeFileName(spec);
			logger.warn("had to encode {} to {}", fname, sfname);
			fname = sfname;			
		}
		File v = new File(basedir,fname);
		return v;
	}
	
	public boolean isDisabled() {
		return basedir == null;
	}
	
	/**
	 * @see gov.usgs.ngwmn.dm.cache.Cache#putter(gov.usgs.ngwmn.dm.spec.Specifier)
	 */
	@Override
	public OutputStream destination(Specifier spec)
			throws IOException
	{
		if (isDisabled()) {
			return new NullOutputStream();
		}
		
		File file = contentFile(spec);
		File tmpFile = File.createTempFile("LDR", "." + spec.getTypeID().suffix);
		
		OutputStream tmp = new TempfileOutputStream(file, tmpFile);
		logger.info("Created tempfile output for {}", spec);
		return tmp;
	}
	
	/**
	 * @see gov.usgs.ngwmn.dm.cache.Cache#get(gov.usgs.ngwmn.dm.spec.Specifier, java.io.OutputStream)
	 */
	@Override
	public boolean fetchWellData(final Specifier spec, Pipeline pipe) 
			throws IOException
	{
		if (isDisabled()) {
			return false;
		}
		
		Invoker i = new FileInputInvoker();
		pipe.setInvoker(i);
		
		pipe.setInputSupplier( new Supplier<InputStream>() {
			@Override
			public InputStream initialize() throws IOException {
				File file = contentFile(spec);
				return new FileInputStream(file);
			}
		});
		
		return true;
	}
	
	
	@Override
	public InputStream retrieve(String id) throws IOException {
		if (isDisabled()) {
			throw new IOException("Null base directory, file cache disabled");
		}
		
		File f = new File(basedir,id);
		
		return new FileInputStream(f);
	}

	private static long copyTo(InputStream is, OutputStream os) 
			throws IOException 
	{
		long ct = ByteStreams.copy(is, os);
		
		return ct;
	}

	public static long copyStream(InputStream is, OutputStream os) 
			throws IOException
	{
		return copyTo(is, os);
	}
	
	
	public File getBasedir() {
		return basedir;
	}

	public void setBasedir(File basedir) throws IOException {
		if (basedir == null) {
			logger.warn("Disabling file cache");
			this.basedir = null;
			return;
		}
		
		if ( ! basedir.exists() ) {
			boolean ok = basedir.mkdirs();
			if ( ! ok) {
				logger.warn("Failed to create base dir {}", basedir);
			}
		}
		if ( ! basedir.exists() ) {
			throw new IOException("Base directory does not exist");			
		}
		if ( ! basedir.isDirectory() ) {
			throw new IOException("Base dir not a directory");
		}
		if ( ! basedir.canRead() ) {
			throw new IOException("Cannot read base directory");
		}
		this.basedir = basedir;
	}

	public boolean contains(Specifier spec) {
		if (isDisabled()) {
			return false;
		}
		
		File f = contentFile(spec);
		
		if (f == null) {
			logger.warn("no such file spec as {}", f);
			return false;
		}
		if ( ! f.exists() ) {
			logger.info("no cached file {}", f);
			return false;
		}
		if ( ! f.canRead() ) {
			logger.warn("file exists but not readable {}", f);
		}
		return true;
	}

	@Override
	public CacheInfo getInfo(Specifier spec) {
		if (isDisabled()) {
			return null;
		}
		
		File f = contentFile(spec);

		boolean exists = f.exists() && f.canRead();
		Date created = null;
		long sz = -1;
		Date modified = null;
		String md5 = null;
		
		if (exists) {
			modified = new Date( f.lastModified() );
			// Java 6 does not provide access to file create time
			// Java 7 does
			sz = f.length();
		}
		
		return new CacheInfo(created, exists, modified, sz, md5,"Y");
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileCache [basedir=").append(basedir).
			// append(", wdt=").append(wdt).
			append(isDisabled() ? ",disabled" : "").
			append("]");
		return builder.toString();
	}
	
	@Override
	public int cleanCache(int daysToRetain, int countToRetain) {
		// No old versions are retained, anyway
		return 0;
	}
	
	
}
