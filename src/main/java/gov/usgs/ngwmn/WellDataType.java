package gov.usgs.ngwmn;


public enum WellDataType {
	LOG         ("text/xml", "xml", null, "logElement|construction"),
	LITHOLOGY   ("text/xml", "xml", LOG,  "logElement"),
	CONSTRUCTION("text/xml", "xml", LOG,  "construction"),
	WATERLEVEL  ("text/xml", "xml", null, "TimeValuePair"),
	QUALITY     ("text/xml", "xml", null, "Result"),
	REGISTRY	("text/xml", "xml", null, "row", false),
	ALL			("application/zip", "zip", null, "n/a", false); // TODO ALL asdf

	
	public final String contentType;
	public final String suffix;
	public final String rowElementName;
	public final WellDataType aliasFor;
	public final boolean cachable;
	

	private WellDataType(String contentType, String suffix, WellDataType alias, String rowElementName) {
		this(contentType, suffix, alias, rowElementName, true);		
	}
	
	private WellDataType(String contentType, String suffix, WellDataType alias, String rowElementName, boolean cachable) {
		this.contentType        = contentType;
		this.suffix             = suffix;
		this.rowElementName     = rowElementName;
		this.aliasFor           = (alias == null) ? this : alias;
		this.cachable 			= cachable;
	}

	public String makeFilename(String wellName) {
		return wellName + "_" + this.name() + "." + this.suffix;
	}

	public boolean isCachable() {
		return cachable;
	}
}
