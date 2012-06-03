package gov.usgs.ngwmn.dm.io;

import java.util.Map;

public interface EntryDescription {
	
	String getName();
	
	void setExtension(String ext);

	Map<String,String> getConstColumns();
}
