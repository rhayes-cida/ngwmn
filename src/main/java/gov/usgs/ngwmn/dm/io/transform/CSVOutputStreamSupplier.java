package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

/* A replacement for TransformEntrySupplier, but not tied to the old XML-to-CSV flattener
 * 
 */
public class CSVOutputStreamSupplier extends Supplier<OutputStream> {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private OutputStream destination;
	private ExecutorService executor;
	private boolean skipHeaders;
	private EntryDescription ed;
	// TODO This is horrible reach-around
	private WellRegistryDAO registry;
	
	public CSVOutputStreamSupplier(OutputStream destination,
			ExecutorService executor, boolean skipHeaders, EntryDescription entryDesc) {
		super();
		this.destination = destination;
		this.executor = executor;
		this.skipHeaders = skipHeaders;
		this.ed = entryDesc;
		
		this.registry = WellRegistryDAO.getInstance();
	}

	@Override
	public OutputStream initialize() throws IOException {
		OutputStream value = null;

		Specifier spec = ed.getSpecifier();

		DirectCSVOutputStream directCSVOutputStream;
		if (spec != null && spec.isBoundedDates()) {
			DirectCSVOutputStreamWithDates dateBoundedStream = new DirectCSVOutputStreamWithDates(destination);
			dateBoundedStream.setBeginDate(spec.getBeginDate());
			dateBoundedStream.setEndDate(spec.getEndDate());
			directCSVOutputStream = dateBoundedStream;
		} else {
			directCSVOutputStream = new DirectCSVOutputStream(destination);
		}
		directCSVOutputStream.setExecutor(executor);
		directCSVOutputStream.setWrittenHeaders(skipHeaders);
		logger.debug("initialize stream for specifier {}", spec);
		if (spec != null) {
			directCSVOutputStream.setAgency(spec.getAgencyID());
			directCSVOutputStream.setSite(spec.getFeatureID());

			// Horrid reach-around
			if (registry != null) {
				WellRegistry well = registry.findByKey(spec.getAgencyID(), spec.getFeatureID());
				if (well != null) {
					Double elevation = well.getAltVa();
					logger.debug("Discovered elevation {} for {}", elevation, well);
					directCSVOutputStream.setElevation(elevation);
				}
			}
		}
		directCSVOutputStream.ensureInitialized();
		value = directCSVOutputStream;
		
		logger.debug("initialize created new output stream {}", value);
		
		return value;
	}

}
