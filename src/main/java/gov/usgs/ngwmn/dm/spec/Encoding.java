package gov.usgs.ngwmn.dm.spec;

public enum Encoding {
	NONE,CSV,TSV,XLSX;

	public String extension() {
		return (this==NONE) ? null : toString().toLowerCase();
	}
}
