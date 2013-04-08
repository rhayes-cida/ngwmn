package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

/* A replacement for TransformEntrySupplier, but not tied to the old XML-to-CSV flattener
 * 
 */
public class DirectQualityCSVOutputStreamSupplier extends Supplier<OutputStream> {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private OutputStream destination;
	private ExecutorService executor;
	private boolean skipHeaders;
	private EntryDescription ed;
	
	public DirectQualityCSVOutputStreamSupplier(OutputStream destination,
			ExecutorService executor, boolean skipHeaders, EntryDescription entryDesc) {
		super();
		this.destination = destination;
		this.executor = executor;
		this.skipHeaders = skipHeaders;
		this.ed = entryDesc;
		
	}

	@Override
	public OutputStream initialize() throws IOException {
		OutputStream value = null;

		Specifier spec = ed.getSpecifier();

		DirectQualityCSVOutputStream directCSVOutputStream = new DirectQualityCSVOutputStream(destination);
		if (spec != null && spec.isBoundedDates()) {
			directCSVOutputStream.setBeginDate(spec.getBeginDate());
			directCSVOutputStream.setEndDate(spec.getEndDate());
		}
		directCSVOutputStream.setExecutor(executor);
		directCSVOutputStream.setWrittenHeaders(skipHeaders);
		logger.debug("initialize stream for specifier {}", spec);
		if (spec != null) {
			directCSVOutputStream.setEncoding(spec.getEncoding());
			directCSVOutputStream.setAgency(spec.getAgencyID());
			directCSVOutputStream.setSite(spec.getFeatureID());
		}
		directCSVOutputStream.ensureInitialized();
		value = directCSVOutputStream;
		
		logger.debug("initialize created new output stream {}", value);
		
		return value;
	}

}
