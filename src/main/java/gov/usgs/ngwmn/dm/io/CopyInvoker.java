package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;

public class CopyInvoker implements Invoker {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void invoke(InputStream is, OutputStream os, PipeStatistics stats)
			throws IOException {
		long ct = ByteStreams.copy(is,os);
		os.close(); // TODO are we sure that we close here and not at the servlet layer or allow the the container to manage this?
		stats.incrementCount(ct);
		logger.info("Copied input to destination, stats={}", new Object[]{stats});
	}

}
