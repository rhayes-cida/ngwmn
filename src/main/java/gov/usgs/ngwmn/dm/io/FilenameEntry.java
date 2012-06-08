package gov.usgs.ngwmn.dm.io;

import java.util.HashMap;
import java.util.Map;

public class FilenameEntry implements EntryDescription {

	String baseName;
	String entryName;
	protected String ext;
	Map<String,String> constCols;
	
	
	public FilenameEntry(String baseName) {
		this(baseName, null, null);
	}
	
	public FilenameEntry(String baseName, String entryName) {
		this(baseName, entryName, null);
	}
	
	public FilenameEntry(String baseName, String entryName, Map<String,String> constColumns) {
		this.baseName  = baseName;
		this.entryName = entryName;
		this.constCols = constColumns;
	}
	
	@Override
	public String entryName() {
		if (entryName == null  ||  entryName.length() == 0) {
			return baseName();
		}
		return entryName+"_"+baseName();
	}

	@Override
	public String baseName() {
		if (ext==null) {
			return baseName;
		}
		return baseName+"."+ext;
	}

	@Override
	public void extension(String extension) {
		ext = extension;
	}

	@Override
	public Map<String, String> constColumns() {
		return (constCols==null) ? new HashMap<String, String>() : constCols;
	}

}
