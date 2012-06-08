package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.parse.DataRowParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class TransformEntrySupplier extends Supplier<OutputStream> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected Supplier<OutputStreamTransform> upstream;
	protected EntryDescription entryDesc;
	protected boolean skipHeaders;
	protected WellDataType dataType;
	
	public TransformEntrySupplier(Supplier<OutputStreamTransform> output, EntryDescription entryDesc,
			WellDataType type, boolean skipHeaders) {
		
		upstream = output;
		dataType = type; // add this to the entry desc
		this.entryDesc   = entryDesc;
		this.skipHeaders = skipHeaders;
	}
	
	
	@Override
	public OutputStream initialize() throws IOException {
		OutputStreamTransform ost = upstream.begin();
		ost.setParser( makeParser() );
		if (skipHeaders) {
			ost.skipHeaders();
		}
		return ost;
	}


	protected DataRowParser makeParser() {
		// TODO this might be a bit too tightly coupled
		DataRowParser parser = new DataRowParser();
		
		// TODO specific to water level
		parser.setRowElementName( dataType.rowElementName );
		parser.addIgnoreNames( dataType.getIgnoreElmentNames() );
		
		appendIdentifierColumns(parser);
		
		return parser;
	}


	protected void appendIdentifierColumns(DataRowParser parser) {
		if (entryDesc==null) return; // if null than there are no cols
		
		Map<String,String> cols = entryDesc.constColumns();
		for (String col : cols.keySet()) {
			parser.addConstColumn(col, cols.get(col));
		}
	}

}
