package gov.usgs.ngwmn.admin;

import gov.usgs.ngwmn.WellDataType;

public class SiteSelector {
	private String agency_cd;
	private String site_id;
	private WellDataType wdt;
	
	public String getAgency_cd() {
		return agency_cd;
	}
	public void setAgency_cd(String agency_cd) {
		this.agency_cd = agency_cd;
	}
	
	public String getSite_id() {
		return site_id;
	}
	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}
	
	public WellDataType getDataType() {
		return wdt;
	}
	public void setDataType(WellDataType wdt) {
		this.wdt = wdt;
	}
	
	
}
