package gov.usgs.ngwmn.dm.io.parse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostParser {

	List<Element> refineHeaderColumns(Collection<Element> headers);
	
	// return true if headers changed
	boolean refineDataColumns(Map<String, String> data, List<Element> headers);
	
	void addConstColumn(String col, String string);
	Set<String> getRemoveColumns();
}
