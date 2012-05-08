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
	public long invoke(InputStream is, OutputStream os)
			throws IOException {
		long ct = ByteStreams.copy(is,os);

		// see the Pipeline.invoke and note that it calls Supplier.end
		// the end should close the stream or an individual stream entry based on its requirements
		//os.close(); // TODO are we sure that we close here and not at the servlet layer or allow the the container to manage this?
		
		logger.info("Copied input to destination, ct={}", ct);
		
		return ct;
	}

}
