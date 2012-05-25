package gov.usgs.ngwmn.dm.io.transform;

import java.util.List;
import java.util.Map;

import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.Parser;

public interface OutputStreamTransform {
	void setParser(Parser parser);
	String formatRow(List<Element> headers, Map<String, String> rowData);
}
