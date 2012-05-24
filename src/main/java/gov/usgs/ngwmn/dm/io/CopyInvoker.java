package gov.usgs.ngwmn.dm.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

public class CopyInvoker implements Invoker {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public long invoke(InputStream is, OutputStream os) throws IOException {
		long ct = ByteStreams.copy(is,os);

		logger.info("Copied input to destination, ct={}", ct);
		
		return ct;
	}

}
