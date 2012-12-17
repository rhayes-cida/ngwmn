package gov.usgs.ngwmn.dm.io.transform;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnclosingOutputStream extends FilterOutputStream {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public UnclosingOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void close() throws IOException {
		logger.debug("Swallowing close event");
		// super.close()
	}


}
