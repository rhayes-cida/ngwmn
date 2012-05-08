package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.fs.FileCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInputInvoker implements Invoker {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public long invoke(InputStream is, OutputStream os) throws IOException {
		long ct = FileCache.copyStream(is, os);
		// output was closed by copyStream.
		String outputDescription;
		if (os instanceof ByteArrayOutputStream) {
			outputDescription = os.getClass().getCanonicalName();
		} else {
			outputDescription = String.valueOf(os);
		}
		logger.info("Copied {} to destination {}, ct={}", new Object[]{is, outputDescription, ct});
		return ct;
	}

}
