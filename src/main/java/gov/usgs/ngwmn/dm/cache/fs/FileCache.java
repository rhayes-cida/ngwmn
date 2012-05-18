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

public class FileCache implements Cache {
	private Logger logger = LoggerFactory.getLogger(FileCache.class);
	
	private File basedir;

	public static final String BASEDIR_JNDI_NAME = "java:comp/env/GWDP/FileCache/basedir";
	
	private final WellDataType wdt = WellDataType.ALL;
	
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
	
	/**
	 * @see gov.usgs.ngwmn.dm.cache.Cache#putter(gov.usgs.ngwmn.dm.spec.Specifier)
	 */
	@Override
	public OutputStream destination(Specifier spec)
			throws IOException
	{
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
	
	private static long copyTo(InputStream is, OutputStream os) 
			throws IOException 
	{
		// TODO: measure performance, see if nio might be worthwhile.
		
		byte[] buf = new byte[1024];
		
		long ops = 0;
		while (true) {
			int ict = is.read(buf);
			if (ict <= 0) {
				break;
			}
			os.write(buf,0,ict);
			ops += ict;
		}
		
		//os.close();
		return ops;
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
		
		return new CacheInfo(created, exists, modified, sz, null);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileCache [basedir=").append(basedir).
			// append(", wdt=").append(wdt).
			append("]");
		return builder.toString();
	}
	
	
}
