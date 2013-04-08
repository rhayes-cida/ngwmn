package gov.usgs.ngwmn.dm.spec;

public enum Encoding {
	NONE,
	CSV(","),
	TSV("\t"),
	XLSX;

	public String extension() {
		return (this==NONE) ? null : toString().toLowerCase();
	}
	
	private Encoding(String sep) {
		separator = sep;		
	}
	private Encoding() {
		separator = null;
	}
	
	private String separator;
	
	public String getSeparator() {
		return separator;
	}
}
