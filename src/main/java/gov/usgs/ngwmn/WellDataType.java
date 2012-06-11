package gov.usgs.ngwmn;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum WellDataType {
	LOG         ("text/xml", "xml", null, "logElement|construction", "uom"),
	LITHOLOGY   ("text/xml", "xml", LOG,  "logElement", "uom"),
	CONSTRUCTION("text/xml", "xml", LOG,  "construction", "uom"),
	WATERLEVEL  ("text/xml", "xml", null, "TimeValuePair", "uom"),
	QUALITY     ("text/xml", "xml", null, "Result", "uom"),
	ALL("application/zip", "zip", null, "TimeValuePair", "uom"); // TODO ALL asdf
	
	public final String contentType;
	public final String suffix;
	public final String rowElementName;
	public final WellDataType aliasFor;
	
	private final Set<String> ignoreElementNames;
	

	private WellDataType(String contentType, String suffix, WellDataType alias,
			String rowElementName, String ... ignoreElementNames) {
		
		this.contentType        = contentType;
		this.suffix             = suffix;
		this.rowElementName     = rowElementName;
		this.ignoreElementNames = new HashSet<String>(Arrays.asList(ignoreElementNames));
		this.aliasFor           = (alias == null) ? this : alias;
	}
	
	public String makeFilename(String wellName) {
		return wellName + "_" + this.name() + "." + this.suffix;
	}
	
	public Set<String> getIgnoreElmentNames() {
		return Collections.unmodifiableSet(ignoreElementNames);
	}
}
