package gov.usgs.ngwmn.ogc;

public class SiteID {
	public final String agency;
	public final String site;
	
	// fid is like VW_GWDP_GEOSERVER.NJGS.2288614
	// site id is like NJGS:2288614
	// (agency:site)

	public SiteID(String agency, String site) {
		super();
		this.agency = agency;
		this.site = site;
	}
	
	public String getFid() {
		return WFSService.FEATURE_PREFIX + "." + agency + "." + site;
	}
	
	public String toString() {
		return agency + ":" + site;
	}
	
	public static SiteID fromFid(String fid) {
		String[] parts = fid.split("\\.");
		// Check first part
		if ( ! WFSService.FEATURE_PREFIX.equals(parts[0])) {
			throw new IllegalArgumentException("Expected " + WFSService.FEATURE_PREFIX + ", got " + parts[0]);
		}
		return new SiteID(parts[1], parts[2]);
	}
	
	public static SiteID fromID(String siteId) {
		String[] parts = siteId.split(":");
		return new SiteID(parts[0], parts[1]);
	}
}