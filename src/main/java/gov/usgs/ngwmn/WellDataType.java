package gov.usgs.ngwmn;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum WellDataType {
	LOG         ("text/xml", "xml", null, "logElement|construction",
// fullNames to remove from results
//			"logElement/MappedInterval/observationMethod/CGI_TermValue/value/codeSpace",
//			"logElement/MappedInterval/specification/HydrostratigraphicUnit/observationMethod/CGI_TermValue/value/codeSpace",
//			"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/role",
//			"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/role/codeSpace",
//			"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/lithology/ControlledConcept/name/codeSpace",
//			"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/proportion/CGI_TermValue/value",
//			"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/proportion/CGI_TermValue/value/codeSpace",
//			"logElement/MappedInterval/shape/LineString/srsDimension"),
// displayNames to remove from results
			"MappedInterval/observationMethod/CGI_TermValue/value/codeSpace",
			"HydrostratigraphicUnit/observationMethod/CGI_TermValue/value/codeSpace",
			"role",
			"role/codeSpace",
			"name/codeSpace",
			"proportion/CGI_TermValue/value",
			"proportion/CGI_TermValue/value/codeSpace",
			"srsDimension"),
	LITHOLOGY   ("text/xml", "xml", LOG,  "logElement",
			"MappedInterval/observationMethod/CGI_TermValue/value/codeSpace",
			"HydrostratigraphicUnit/observationMethod/CGI_TermValue/value/codeSpace",
			"role",
			"role/codeSpace",
			"name/codeSpace",
			"proportion/CGI_TermValue/value",
			"proportion/CGI_TermValue/value/codeSpace",
			"srsDimension"),
	CONSTRUCTION("text/xml", "xml", LOG,  "construction"),
	WATERLEVEL  ("text/xml", "xml", null, "TimeValuePair", "uom"),
	QUALITY     ("text/xml", "xml", null, "Result"),
	ALL("application/zip", "zip", null, "n/a"); // TODO ALL asdf

	
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
