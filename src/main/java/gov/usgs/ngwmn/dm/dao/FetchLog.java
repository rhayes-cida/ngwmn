package gov.usgs.ngwmn.dm.dao;

import java.util.Date;

public class FetchLog {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.FETCHLOG_ID
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private Integer fetchlogId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.AGENCY_CD
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String agencyCd;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.SITE_NO
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String siteNo;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.DATA_SOURCE
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String source;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.STARTED_AT
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private Date startedAt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.STATUS
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String status;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.PROBLEM
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String problem;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.CT
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private Integer ct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.ELAPSED_SEC
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private Double elapsedSec;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.SPECIFIER
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String specifier;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.FETCH_LOG.FETCHER
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	private String fetcher;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.FETCHLOG_ID
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.FETCHLOG_ID
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public Integer getFetchlogId() {
		return fetchlogId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.FETCHLOG_ID
	 * @param fetchlogId  the value for GW_DATA_PORTAL.FETCH_LOG.FETCHLOG_ID
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setFetchlogId(Integer fetchlogId) {
		this.fetchlogId = fetchlogId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.AGENCY_CD
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.AGENCY_CD
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getAgencyCd() {
		return agencyCd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.AGENCY_CD
	 * @param agencyCd  the value for GW_DATA_PORTAL.FETCH_LOG.AGENCY_CD
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setAgencyCd(String agencyCd) {
		this.agencyCd = agencyCd;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.SITE_NO
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.SITE_NO
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getSiteNo() {
		return siteNo;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.SITE_NO
	 * @param siteNo  the value for GW_DATA_PORTAL.FETCH_LOG.SITE_NO
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setSiteNo(String siteNo) {
		this.siteNo = siteNo;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.DATA_SOURCE
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.DATA_SOURCE
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getSource() {
		return source;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.DATA_SOURCE
	 * @param source  the value for GW_DATA_PORTAL.FETCH_LOG.DATA_SOURCE
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.STARTED_AT
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.STARTED_AT
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public Date getStartedAt() {
		return startedAt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.STARTED_AT
	 * @param startedAt  the value for GW_DATA_PORTAL.FETCH_LOG.STARTED_AT
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.STATUS
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.STATUS
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.STATUS
	 * @param status  the value for GW_DATA_PORTAL.FETCH_LOG.STATUS
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.PROBLEM
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.PROBLEM
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getProblem() {
		return problem;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.PROBLEM
	 * @param problem  the value for GW_DATA_PORTAL.FETCH_LOG.PROBLEM
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setProblem(String problem) {
		this.problem = problem;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.CT
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.CT
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public Integer getCt() {
		return ct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.CT
	 * @param ct  the value for GW_DATA_PORTAL.FETCH_LOG.CT
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setCt(Integer ct) {
		this.ct = ct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.ELAPSED_SEC
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.ELAPSED_SEC
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public Double getElapsedSec() {
		return elapsedSec;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.ELAPSED_SEC
	 * @param elapsedSec  the value for GW_DATA_PORTAL.FETCH_LOG.ELAPSED_SEC
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setElapsedSec(Double elapsedSec) {
		this.elapsedSec = elapsedSec;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.SPECIFIER
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.SPECIFIER
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getSpecifier() {
		return specifier;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.SPECIFIER
	 * @param specifier  the value for GW_DATA_PORTAL.FETCH_LOG.SPECIFIER
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.FETCH_LOG.FETCHER
	 * @return  the value of GW_DATA_PORTAL.FETCH_LOG.FETCHER
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public String getFetcher() {
		return fetcher;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.FETCH_LOG.FETCHER
	 * @param fetcher  the value for GW_DATA_PORTAL.FETCH_LOG.FETCHER
	 * @mbggenerated  Sat Mar 31 14:26:07 CDT 2012
	 */
	public void setFetcher(String fetcher) {
		this.fetcher = fetcher;
	}

	public void setWell(WellRegistryKey key) {
		setAgencyCd(key.getAgencyCd());
		setSiteNo(key.getSiteNo());
	}
	
	public WellRegistryKey getWell() {
		return new WellRegistryKey(getAgencyCd(), getSiteNo());
	}
}