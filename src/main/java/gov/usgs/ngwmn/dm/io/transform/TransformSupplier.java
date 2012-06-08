package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Encoding;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransformSupplier extends Supplier<OutputStream> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected Supplier<OutputStream> upstream;
	protected EntryDescription entryDesc;
	protected Encoding encoding;
	protected OutputStreamTransform ost;
	protected boolean skipHeaders;
	protected WellDataType dataType;
	
	public TransformSupplier(Supplier<OutputStream> output, WellDataType type, Encoding encode) {
		upstream = output;
		dataType = type;
		encoding = (encode==null) ? Encoding.NONE : encode;
	}
	
	
	@Override
	public Supplier<OutputStream> makeEntry(EntryDescription entryDesc) {
		logger.trace("making entry for {}",entryDesc);
		entryDesc.extension( encoding.extension() );
		
		if (ost == null) {
			throw new RuntimeException("Cannot makeEntry on a uninitialized supplier.");
		}
		Supplier<OutputStreamTransform> sos = new SimpleSupplier<OutputStreamTransform>(ost);
		TransformEntrySupplier transformer  = new TransformEntrySupplier(sos, entryDesc, dataType, skipHeaders);
		skipHeaders = true;
		return transformer;
	}
	
	@Override
	public OutputStream initialize() throws IOException {
		OutputStream os = upstream.begin();
		
		switch (encoding) {
			case NONE:
				return os;
			case TSV:
				ost = new TsvOutputStream(os);
				break;
			case XLSX: // TODO need to impl
				throw new NotImplementedException();
			default:   // Default to CSV
			case CSV:
				ost = new CsvOutputStream(os);
		}
		return ost;
	}

}
