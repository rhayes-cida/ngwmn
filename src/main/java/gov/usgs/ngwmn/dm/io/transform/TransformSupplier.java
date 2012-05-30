package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.parse.DataRowParser;
import gov.usgs.ngwmn.dm.spec.Encoding;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;


public class TransformSupplier extends Supplier<OutputStream> {

	protected Supplier<OutputStream> upstream;
	protected Encoding encoding;
	
	// this is not necessary, but it is more accurate.
	// when upstream is zipped we should not really encode the main stream - entires only.
	// it does not hurt because no data is sent to the main stream.
	// data is only sent along entries
	protected boolean  entriesOnly;


	public TransformSupplier(Supplier<OutputStream> output, Encoding encode) {
		this(output,encode,false);
	}
	public TransformSupplier(Supplier<OutputStream> output, Encoding encode, boolean entriesOnly) {
		upstream = output;
		encoding = (encode==null) ? Encoding.NONE : encode;
		this.entriesOnly = entriesOnly;
	}


	@Override
	public Supplier<OutputStream> makeEntry(Specifier spec) {
		Supplier<OutputStream> entry = upstream.makeEntry(spec);
		return new TransformSupplier(entry, encoding);
	}

	@Override
	public OutputStream initialize() throws IOException {
		
		OutputStream os = upstream.begin();
		
		if (entriesOnly) return os;
		
		OutputStreamTransform ost;
		
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
		// TODO this might be a bit too tightly coupled
		DataRowParser parser = new DataRowParser();
		
		// TODO specific to water level
		parser.setRowElementName("TimeValuePair");
		parser.addIgnoreName("uom");
		
		ost.setParser(parser);
		return ost;
	}

}
