package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;


public class TsvOutputStream extends CsvOutputStream {

	
	public TsvOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public String getSeparator() {
		return "\t";
	}
	
	protected void logEntry() {
		logger.trace("TSV Format Row");
	}
}
