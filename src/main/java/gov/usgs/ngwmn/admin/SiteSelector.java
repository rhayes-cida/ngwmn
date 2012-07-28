package gov.usgs.ngwmn.admin;

import gov.usgs.ngwmn.WellDataType;

public class SiteSelector {
	private String agency_cd;
	private String site_id;
	private WellDataType wdt;
	
	public String getAgency() {
		return agency_cd;
	}
	public void setAgency(String agency_cd) {
		this.agency_cd = agency_cd;
	}
	
	public String getSiteId() {
		return site_id;
	}
	public void setSiteId(String site_id) {
		this.site_id = site_id;
	}
	
	public WellDataType getDataType() {
		return wdt;
	}
	public void setDataType(WellDataType wdt) {
		this.wdt = wdt;
	}
	
	public SiteSelector(String agency_cd, String site_id, WellDataType wdt) {
		super();
		this.agency_cd = agency_cd;
		this.site_id = site_id;
		this.wdt = wdt;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SiteSelector " +
				"[agency_cd=").append(agency_cd)
				.append(", site_id=").append(site_id)
				.append(", wdt=").append(wdt)
				.append("]");
		return builder.toString();
	}
	
	
	
	
}
