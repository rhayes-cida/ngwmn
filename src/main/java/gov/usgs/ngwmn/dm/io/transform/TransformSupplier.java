package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.parse.DataRowParser;
import gov.usgs.ngwmn.dm.spec.Encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


public class TransformSupplier extends Supplier<OutputStream> {

	protected Supplier<OutputStream> upstream;
	protected Encoding encoding;
	protected EntryDescription entryDesc;
	
	public TransformSupplier(Supplier<OutputStream> output, Encoding encode) {
		upstream = output;
		encoding = (encode==null) ? Encoding.NONE : encode;
	}
	
	
	@Override
	public Supplier<OutputStream> makeEntry(EntryDescription entryDesc) {
		entryDesc.setExtension(encoding.extension());
		
		Supplier<OutputStream> entry = upstream.makeEntry(entryDesc);
		
		TransformSupplier transformer = new TransformSupplier(entry, encoding);
		transformer.entryDesc = entryDesc;
		return transformer;
	}
	
	@Override
	public OutputStream initialize() throws IOException {
		
		OutputStream os = upstream.begin();
		
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
		DataRowParser parser = makeParser();
		ost.setParser(parser);
		return ost;
	}


	public DataRowParser makeParser() {
		// TODO this might be a bit too tightly coupled
		DataRowParser parser = new DataRowParser();
		
		// TODO specific to water level
		parser.setRowElementName("TimeValuePair");
		parser.addIgnoreName("uom");
		
		appendIdentifierColumns(parser);
		
		return parser;
	}


	public void appendIdentifierColumns(DataRowParser parser) {
		if (entryDesc==null) return; // if null than there are no cols
		
		Map<String,String> cols = entryDesc.getConstColumns();
		for (String col : cols.keySet()) {
			parser.addConstColumn(col, cols.get(col));
		}
	}

}
