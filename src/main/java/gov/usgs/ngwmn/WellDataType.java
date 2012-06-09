package gov.usgs.ngwmn;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum WellDataType {
	LOG("text/xml", "xml", "TimeValuePair", "uom"),        // TODO correct rowElementName and ignoreElementNames
	WATERLEVEL("text/xml", "xml", "TimeValuePair", "uom"),
	QUALITY("text/xml", "xml", "TimeValuePair", "uom");    // TODO correct rowElementName and ignoreElementNames
	
	public final String contentType;
	public final String suffix;
	public final String rowElementName;
	
	private final Set<String> ignoreElementNames;

	private WellDataType(String contentType, String suffix, 
			String rowElementName, String ... ignoreElementNames) {
		
		this.contentType        = contentType;
		this.suffix             = suffix;
		this.rowElementName     = rowElementName;
		this.ignoreElementNames = new HashSet<String>(Arrays.asList(ignoreElementNames));
		
	}
	
	public String makeFilename(String wellName) {
		return wellName + "_" + this.name() + "." + this.suffix;
	}
	
	public Set<String> getIgnoreElmentNames() {
		return Collections.unmodifiableSet(ignoreElementNames);
	}
}
