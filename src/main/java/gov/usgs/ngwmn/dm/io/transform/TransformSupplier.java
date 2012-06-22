package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.HeaderChangeListener;
import gov.usgs.ngwmn.dm.spec.Encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransformSupplier extends Supplier<OutputStream> 
		implements HeaderChangeListener, HeaderWrittenListener {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected Supplier<OutputStream> upstream;
	protected EntryDescription entryDesc;
	protected Encoding encoding;
	protected OutputStreamTransform ost;
	protected boolean skipHeaders;
	protected WellDataType dataType;
	
	// this supplier will be a listener for the headers on the first entry
	// it will supply subsequent entries with these headers to preserve column ordinal
	protected List<Element> masterHeaders; // previous headers

	public TransformSupplier(Supplier<OutputStream> output, WellDataType type, Encoding encode) {
		upstream = output;
		dataType = type;
		encoding = (encode==null) ? Encoding.NONE : encode;
	}
	
	
	@Override
	public void headersChanged(List<Element> headers) {
		masterHeaders = headers;
	}
	
	@Override
	public Supplier<OutputStream> makeEntry(EntryDescription entryDesc) {
		logger.trace("making entry for {}",entryDesc);
		entryDesc.extension( encoding.extension() );
		
		if (ost == null) {
			throw new RuntimeException("Cannot makeEntry on a uninitialized supplier.");
		}
		ost.addHeaderListener(this);
		
		Supplier<OutputStreamTransform> sos = new SimpleSupplier<OutputStreamTransform>(ost);
		TransformEntrySupplier transformer;
		if (skipHeaders) {
			// we must have master headers by now because we are skipping them so we pass-on what we have
			transformer  = new TransformEntrySupplier(sos, entryDesc, dataType, skipHeaders, masterHeaders);
		} else {
			// we need master headers so listen for them
			transformer  = new TransformEntrySupplier(sos, entryDesc, dataType, skipHeaders, this);
		}
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

	@Override
	public void headersWritten() {
		skipHeaders = true;
	}
}
