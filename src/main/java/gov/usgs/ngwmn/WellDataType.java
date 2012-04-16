package gov.usgs.ngwmn;

public enum WellDataType {
	LOG("text/xml", "xml"),
	WATERLEVEL("text/xml", "xml"),
	QUALITY("text/xml", "xml"),
	ALL("application/zip", "zip");
	
	public final String contentType;
	public final String suffix;

	private WellDataType(String contentType, String suffix) {
		this.contentType = contentType;
		this.suffix = suffix;
	}
	
	public String makeFilename(String wellName) {
		return wellName + "_" + this.name() + "." + this.suffix;
	}
}
