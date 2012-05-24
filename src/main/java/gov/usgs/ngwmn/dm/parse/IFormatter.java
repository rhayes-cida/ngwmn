package gov.usgs.ngwmn.dm.parse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamReader;


public interface IFormatter {
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String DEFAULT_FILE_NAME = "data";

	public void dispatch(XMLStreamReader in, HttpServletResponse response) throws IOException;
	public void dispatch(XMLStreamReader in, OutputStream out) throws IOException;
	public void dispatch(XMLStreamReader in, Writer out) throws IOException;
	public String getMimeType();
	public String getFileSuffix();
	public String getFileName();
	public void setFileName(String fileName);
	public boolean isNeedsCompleteFirstRow();

}
