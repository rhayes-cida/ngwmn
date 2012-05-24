package gov.usgs.ngwmn.dm.dao;

public class CacheMetaDataKey {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.AGENCY_CD
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	private String agencyCd;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.DATA_TYPE
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	private String dataType;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.SITE_NO
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	private String siteNo;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.AGENCY_CD
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.AGENCY_CD
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	public String getAgencyCd() {
		return agencyCd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.AGENCY_CD
	 * @param agencyCd  the value for GW_DATA_PORTAL.CACHE_META_DATA.AGENCY_CD
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	public void setAgencyCd(String agencyCd) {
		this.agencyCd = agencyCd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.DATA_TYPE
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.DATA_TYPE
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.DATA_TYPE
	 * @param dataType  the value for GW_DATA_PORTAL.CACHE_META_DATA.DATA_TYPE
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.SITE_NO
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.SITE_NO
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	public String getSiteNo() {
		return siteNo;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.SITE_NO
	 * @param siteNo  the value for GW_DATA_PORTAL.CACHE_META_DATA.SITE_NO
	 * @mbggenerated  Thu Apr 19 14:52:23 CDT 2012
	 */
	public void setSiteNo(String siteNo) {
		this.siteNo = siteNo;
	}

	
	// Primary keys should always have equals.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agencyCd == null) ? 0 : agencyCd.hashCode());
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((siteNo == null) ? 0 : siteNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheMetaDataKey other = (CacheMetaDataKey) obj;
		if (agencyCd == null) {
			if (other.agencyCd != null)
				return false;
		} else if (!agencyCd.equals(other.agencyCd))
			return false;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (siteNo == null) {
			if (other.siteNo != null)
				return false;
		} else if (!siteNo.equals(other.siteNo))
			return false;
		return true;
	}
	
}