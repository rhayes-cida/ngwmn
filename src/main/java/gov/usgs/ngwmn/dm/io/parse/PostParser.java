package gov.usgs.ngwmn.dm.io.parse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostParser {

	List<Element> refineHeaderColumns(Collection<Element> headers);
	void refineDataColumns(Map<String, String> data);
	
	void addConstColumn(String col, String string);
	Set<String> getRemoveColumns();
}
