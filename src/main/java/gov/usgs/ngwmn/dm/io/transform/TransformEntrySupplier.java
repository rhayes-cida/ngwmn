package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.parse.DataRowParser;
import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.HeadersListener;
import gov.usgs.ngwmn.dm.io.parse.Parser;
import gov.usgs.ngwmn.dm.io.parse.PostParser;
import gov.usgs.ngwmn.dm.io.parse.WaterPortalPostParserFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class TransformEntrySupplier extends Supplier<OutputStream> {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected Supplier<OutputStreamTransform> upstream;
	protected EntryDescription entryDesc;
	protected boolean skipHeaders;
	protected WellDataType dataType;
	protected HeadersListener headerListener;

	private List<Element> headers;
	
	public TransformEntrySupplier(Supplier<OutputStreamTransform> output, EntryDescription entryDesc,
			WellDataType type, boolean skipHeaders, HeadersListener listener) {
		
		upstream         = output;
		dataType         = type; // add this to the entry desc
		this.entryDesc   = entryDesc;
		this.skipHeaders = skipHeaders;
		headerListener   = listener;
	}
	public TransformEntrySupplier(Supplier<OutputStreamTransform> output, EntryDescription entryDesc,
			WellDataType type, boolean skipHeaders, List<Element> headers) {
		this(output, entryDesc, type, skipHeaders, (HeadersListener)null);
		this.headers = headers;
	}
	
	
	@Override
	public OutputStream initialize() throws IOException {
		OutputStreamTransform ost = upstream.begin();
		Parser parser = makeParser();
		ost.setParser(parser);
		if (skipHeaders) {
			ost.skipHeaders();
			// headers can be null - headers will be extracted from the stream if null
			ost.setHeaders(headers);
		} else {
			// if we have no headerListener then we are going to use the given headers
			parser.addHeaderListener(headerListener);
		}
		return ost;
	}


	protected DataRowParser makeParser() {
		// TODO this might be a bit too tightly coupled
		PostParser postParser = new WaterPortalPostParserFactory().make(dataType);
		appendIdentifierColumns(postParser);
		
		// TODO this might be a bit too tightly coupled
		DataRowParser parser = new DataRowParser(postParser);
		
		// TODO specific to water level
		parser.setRowElementName( dataType.rowElementName );
		
		
		return parser;
	}


	protected void appendIdentifierColumns(PostParser parser) {
		if (entryDesc==null) return; // if null than there are no cols
		
		Map<String,String> cols = entryDesc.constColumns();
		for (String col : cols.keySet()) {
			parser.addConstColumn(col, cols.get(col));
		}
	}

}
