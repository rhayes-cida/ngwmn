package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.dm.io.parse.Element;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


public class TsvOutputStream extends OutputStreamTransform {

	
	public TsvOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public String formatRow(List<Element> headers, Map<String, String> rowData) {
		logger.debug("TSV Format Row");
		
		StringBuilder rowText = new StringBuilder(); 
		
		for (Element header : headers) {
			String data = (rowData==null) ? header.displayName : rowData.get(header.fullName);
			data = (data==null) ? "" : data;
			rowText.append(data).append('\t');
		}
		return rowText.toString();
	}
}
