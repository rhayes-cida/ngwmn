package gov.usgs.ngwmn.dm.dao;

public class WellRegistryKey {
	private String agencyId;
	private String siteNo;
	
	
	public WellRegistryKey(String agencyId, String siteNo) {
		this.agencyId = agencyId;
		this.siteNo = siteNo;
	}
	
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	public String getSiteNo() {
		return siteNo;
	}
	public void setSiteNo(String siteNo) {
		this.siteNo = siteNo;
	}
	
	
}
