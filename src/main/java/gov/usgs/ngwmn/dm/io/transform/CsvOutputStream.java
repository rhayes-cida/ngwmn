package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.dm.io.parse.Element;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


public class CsvOutputStream extends OutputStreamTransform {

	
	public CsvOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public String formatRow(List<Element> headers, Map<String, String> rowData) {
		logger.trace("CSV Format Row");
		
		StringBuilder rowText = new StringBuilder(); 
		
		String sep = "";
		for (Element header : headers) {
			String data = (rowData==null) ? header.displayName : rowData.get(header.fullName);
			data = (data==null) ? "" : data;
			rowText.append(sep).append(data);
			sep = ",";
		}
		rowText.append('\n');
		
		return rowText.toString();
	}
}
