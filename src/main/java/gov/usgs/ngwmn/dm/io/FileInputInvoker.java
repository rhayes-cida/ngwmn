package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
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
	public void invoke(InputStream is, OutputStream os, PipeStatistics stats) throws IOException {
		FileCache.copyStream(is, os, stats);
		// output was closed by copyStream.
		String outputDescription;
		if (os instanceof ByteArrayOutputStream) {
			outputDescription = os.getClass().getCanonicalName();
		} else {
			outputDescription = String.valueOf(os);
		}
		logger.info("Copied {} to destination {}, stats={}", new Object[]{is, outputDescription, stats});
	}

}
