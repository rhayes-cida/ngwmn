package gov.usgs.ngwmn;


public enum WellDataType {
	LOG         ("text/xml", "xml", null, "logElement|construction"),
	LITHOLOGY   ("text/xml", "xml", LOG,  "logElement"),
	CONSTRUCTION("text/xml", "xml", LOG,  "construction"),
	WATERLEVEL  ("text/xml", "xml", null, "TimeValuePair"),
	QUALITY     ("text/xml", "xml", null, "Result"),
	ALL("application/zip", "zip", null, "n/a"); // TODO ALL asdf

	
	public final String contentType;
	public final String suffix;
	public final String rowElementName;
	public final WellDataType aliasFor;
	

	private WellDataType(String contentType, String suffix, WellDataType alias, String rowElementName) {
		
		this.contentType        = contentType;
		this.suffix             = suffix;
		this.rowElementName     = rowElementName;
		this.aliasFor           = (alias == null) ? this : alias;
	}
	
	public String makeFilename(String wellName) {
		return wellName + "_" + this.name() + "." + this.suffix;
	}
}
