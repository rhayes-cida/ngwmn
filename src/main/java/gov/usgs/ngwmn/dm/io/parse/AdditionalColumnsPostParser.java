package gov.usgs.ngwmn.dm.io.parse;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdditionalColumnsPostParser implements PostParser {
	private final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected final Map<String, String> additionalCols;
	
	public AdditionalColumnsPostParser() {
		this( null );
	}
	public AdditionalColumnsPostParser(Map<String, String> newCols) {
		if (newCols==null) {
			additionalCols = new LinkedHashMap<String,String>();
		} else {
			additionalCols = new LinkedHashMap<String,String>(newCols);
		}
	}
	
	@Override
	public List<Element> refineHeaderColumns(List<Element> headers) {
		// one - inject constant columns
		List<Element> newHeaders = new LinkedList<Element>();

		for (String constCol : additionalCols.keySet()) {
			logger.trace("adding column: {}", constCol);
			newHeaders.add( new Element(constCol, constCol, constCol) );
		}
		// this ensures all new columns are added at the beginning
		newHeaders.addAll(headers);
				
		return newHeaders;
	}


	@Override
	public void refineDataColumns(Map<String, String> data) {
		appendElements(data);
	}
	protected void appendElements(Map<String, String> data) {
		for (String constCol : additionalCols.keySet()) {
			data.put(constCol, additionalCols.get(constCol));
		}
	}

	public void addColumn(String col, String value) {
		additionalCols.put(col, value);
	}

}
