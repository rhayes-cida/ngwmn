package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.ngwmn.dm.io.Supplier;

/* A replacement for TransformEntrySupplier, but not tied to the old XML-to-CSV flattener
 * 
 */
public class CSVOutputStreamSupplier extends Supplier<OutputStream> {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private OutputStream destination;
	private ExecutorService executor;
	private boolean skipHeaders;
	
	public CSVOutputStreamSupplier(OutputStream destination,
			ExecutorService executor, boolean skipHeaders) {
		super();
		this.destination = destination;
		this.executor = executor;
		this.skipHeaders = skipHeaders;
	}

	@Override
	public OutputStream initialize() throws IOException {
		OutputStream value = null;

		DirectCSVOutputStream directCSVOutputStream;
		directCSVOutputStream = new DirectCSVOutputStream(destination);
		directCSVOutputStream.setExecutor(executor);
		directCSVOutputStream.setWrittenHeaders(skipHeaders);
		directCSVOutputStream.ensureInitialized();
		value = directCSVOutputStream;
		
		logger.debug("initialize created new output stream {}", value);
		
		return value;
	}

}
