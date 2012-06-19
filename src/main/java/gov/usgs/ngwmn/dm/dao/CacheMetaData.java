package gov.usgs.ngwmn.dm.dao;

import java.util.Date;

public class CacheMetaData extends CacheMetaDataKey {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.SUCCESS_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Integer successCt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.FAIL_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Integer failCt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.FIRST_DATA_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Date firstDataDt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.LAST_DATA_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Date lastDataDt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_SUCCESS_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Date mostRecentSuccessDt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_ATTEMPT_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Date mostRecentAttemptDt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_FAIL_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Date mostRecentFailDt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.FETCH_PRIORITY
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Integer fetchPriority;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.EMPTY_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Integer emptyCt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_EMPTY_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	private Date mostRecentEmptyDt;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.SUCCESS_CT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.SUCCESS_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Integer getSuccessCt() {
		return successCt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.SUCCESS_CT
	 * @param successCt  the value for GW_DATA_PORTAL.CACHE_META_DATA.SUCCESS_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setSuccessCt(Integer successCt) {
		this.successCt = successCt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.FAIL_CT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.FAIL_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Integer getFailCt() {
		return failCt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.FAIL_CT
	 * @param failCt  the value for GW_DATA_PORTAL.CACHE_META_DATA.FAIL_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setFailCt(Integer failCt) {
		this.failCt = failCt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.FIRST_DATA_DT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.FIRST_DATA_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Date getFirstDataDt() {
		return firstDataDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.FIRST_DATA_DT
	 * @param firstDataDt  the value for GW_DATA_PORTAL.CACHE_META_DATA.FIRST_DATA_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setFirstDataDt(Date firstDataDt) {
		this.firstDataDt = firstDataDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.LAST_DATA_DT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.LAST_DATA_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Date getLastDataDt() {
		return lastDataDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.LAST_DATA_DT
	 * @param lastDataDt  the value for GW_DATA_PORTAL.CACHE_META_DATA.LAST_DATA_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setLastDataDt(Date lastDataDt) {
		this.lastDataDt = lastDataDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_SUCCESS_DT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_SUCCESS_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Date getMostRecentSuccessDt() {
		return mostRecentSuccessDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_SUCCESS_DT
	 * @param mostRecentSuccessDt  the value for GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_SUCCESS_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setMostRecentSuccessDt(Date mostRecentSuccessDt) {
		this.mostRecentSuccessDt = mostRecentSuccessDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_ATTEMPT_DT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_ATTEMPT_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Date getMostRecentAttemptDt() {
		return mostRecentAttemptDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_ATTEMPT_DT
	 * @param mostRecentAttemptDt  the value for GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_ATTEMPT_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setMostRecentAttemptDt(Date mostRecentAttemptDt) {
		this.mostRecentAttemptDt = mostRecentAttemptDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_FAIL_DT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_FAIL_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Date getMostRecentFailDt() {
		return mostRecentFailDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_FAIL_DT
	 * @param mostRecentFailDt  the value for GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_FAIL_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setMostRecentFailDt(Date mostRecentFailDt) {
		this.mostRecentFailDt = mostRecentFailDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.FETCH_PRIORITY
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.FETCH_PRIORITY
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Integer getFetchPriority() {
		return fetchPriority;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.FETCH_PRIORITY
	 * @param fetchPriority  the value for GW_DATA_PORTAL.CACHE_META_DATA.FETCH_PRIORITY
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setFetchPriority(Integer fetchPriority) {
		this.fetchPriority = fetchPriority;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.EMPTY_CT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.EMPTY_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Integer getEmptyCt() {
		return emptyCt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.EMPTY_CT
	 * @param emptyCt  the value for GW_DATA_PORTAL.CACHE_META_DATA.EMPTY_CT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setEmptyCt(Integer emptyCt) {
		this.emptyCt = emptyCt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_EMPTY_DT
	 * @return  the value of GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_EMPTY_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public Date getMostRecentEmptyDt() {
		return mostRecentEmptyDt;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_EMPTY_DT
	 * @param mostRecentEmptyDt  the value for GW_DATA_PORTAL.CACHE_META_DATA.MOST_RECENT_EMPTY_DT
	 * @mbggenerated  Tue Jun 19 12:15:56 CDT 2012
	 */
	public void setMostRecentEmptyDt(Date mostRecentEmptyDt) {
		this.mostRecentEmptyDt = mostRecentEmptyDt;
	}
}