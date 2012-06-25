package gov.usgs.ngwmn.dm.io.parse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostParser {

	List<Element> refineHeaderColumns(List<Element> headers);
	void refineDataColumns(Map<String, String> data);
	
	Set<String> getRemoveColumns();
}
