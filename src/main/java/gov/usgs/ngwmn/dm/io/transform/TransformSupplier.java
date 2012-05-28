package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Encoding;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;


public class TransformSupplier extends Supplier<OutputStream> {

	protected Supplier<OutputStream> upstream;
	protected Encoding encoding;
	
	public TransformSupplier(Supplier<OutputStream> output, Encoding encode) {
		upstream = output;
		encoding = encode;
	}
	
	@Override
	public Supplier<OutputStream> makeEntry(Specifier spec) {
		Supplier<OutputStream> entry = upstream.makeEntry(spec);
		return new TransformSupplier(entry, encoding);
	}
	
	@Override
	public OutputStream initialize() throws IOException {
		
		OutputStream os = upstream.begin();
		
		if (encoding==null) return os;
		
		switch (encoding) {
			case NONE:
				return os;
			case TSV:
				return new TsvOutputStream(os);
			case XLSX: // TODO need to impl
				throw new NotImplementedException();
			default:   // Default to CSV
			case CSV:
				return new CsvOutputStream(os);
		}
	}

}
