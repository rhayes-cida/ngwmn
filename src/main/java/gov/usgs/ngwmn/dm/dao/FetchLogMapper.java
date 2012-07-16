package gov.usgs.ngwmn.dm.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogExample;

public interface FetchLogMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	int countByExample(FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	int deleteByExample(FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	@Delete({ "delete from GW_DATA_PORTAL.FETCH_LOG",
			"where FETCHLOG_ID = #{fetchlogId,jdbcType=NUMERIC}" })
	int deleteByPrimaryKey(Integer fetchlogId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	@Insert({
			"insert into GW_DATA_PORTAL.FETCH_LOG (FETCHLOG_ID, AGENCY_CD, ",
			"SITE_NO, DATA_SOURCE, ",
			"STARTED_AT, STATUS, ",
			"PROBLEM, CT, ELAPSED_SEC, ",
			"SPECIFIER, FETCHER, ",
			"DATA_STREAM)",
			"values (#{fetchlogId,jdbcType=NUMERIC}, #{agencyCd,jdbcType=VARCHAR}, ",
			"#{siteNo,jdbcType=VARCHAR}, #{source,jdbcType=VARCHAR}, ",
			"#{startedAt,jdbcType=TIMESTAMP}, #{status,jdbcType=CHAR}, ",
			"#{problem,jdbcType=VARCHAR}, #{ct,jdbcType=NUMERIC}, #{elapsedSec,jdbcType=FLOAT}, ",
			"#{specifier,jdbcType=VARCHAR}, #{fetcher,jdbcType=VARCHAR}, ",
			"#{dataStream,jdbcType=VARCHAR})" })
	int insert(FetchLog record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	int insertSelective(FetchLog record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	List<FetchLog> selectByExample(FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	@Select({
			"select",
			"FETCHLOG_ID, AGENCY_CD, SITE_NO, DATA_SOURCE, STARTED_AT, STATUS, PROBLEM, CT, ",
			"ELAPSED_SEC, SPECIFIER, FETCHER, DATA_STREAM",
			"from GW_DATA_PORTAL.FETCH_LOG",
			"where FETCHLOG_ID = #{fetchlogId,jdbcType=NUMERIC}" })
	@ResultMap("BaseResultMap")
	FetchLog selectByPrimaryKey(Integer fetchlogId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	int updateByExampleSelective(@Param("record") FetchLog record,
			@Param("example") FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	int updateByExample(@Param("record") FetchLog record,
			@Param("example") FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	int updateByPrimaryKeySelective(FetchLog record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Fri May 25 11:07:09 CDT 2012
	 */
	@Update({ "update GW_DATA_PORTAL.FETCH_LOG",
			"set AGENCY_CD = #{agencyCd,jdbcType=VARCHAR},",
			"SITE_NO = #{siteNo,jdbcType=VARCHAR},",
			"DATA_SOURCE = #{source,jdbcType=VARCHAR},",
			"STARTED_AT = #{startedAt,jdbcType=TIMESTAMP},",
			"STATUS = #{status,jdbcType=CHAR},",
			"PROBLEM = #{problem,jdbcType=VARCHAR},",
			"CT = #{ct,jdbcType=NUMERIC},",
			"ELAPSED_SEC = #{elapsedSec,jdbcType=FLOAT},",
			"SPECIFIER = #{specifier,jdbcType=VARCHAR},",
			"FETCHER = #{fetcher,jdbcType=VARCHAR},",
			"DATA_STREAM = #{dataStream,jdbcType=VARCHAR}",
			"where FETCHLOG_ID = #{fetchlogId,jdbcType=NUMERIC}" })
	int updateByPrimaryKey(FetchLog record);

	@SelectKey(statement="select gw_data_portal.fetch_log_seq.nextval from dual", resultType = int.class, before = true, keyProperty = "fetchlogId") 
	@Insert({
		"insert into GW_DATA_PORTAL.FETCH_LOG (FETCHLOG_ID, AGENCY_CD, ",
		"SITE_NO, DATA_SOURCE, ",
		"STARTED_AT, STATUS, ",
		"PROBLEM, CT, ELAPSED_SEC, ",
		"SPECIFIER, FETCHER, DATA_STREAM)",
		"values (#{fetchlogId, jdbcType=NUMERIC}, #{agencyCd,jdbcType=VARCHAR}, ",
		"#{siteNo,jdbcType=VARCHAR}, #{source,jdbcType=VARCHAR}, ",
		"#{startedAt,jdbcType=TIMESTAMP}, #{status,jdbcType=CHAR}, ",
		"#{problem,jdbcType=VARCHAR}, #{ct,jdbcType=NUMERIC}, #{elapsedSec,jdbcType=FLOAT}, ",
		"#{specifier,jdbcType=VARCHAR}, #{fetcher,jdbcType=VARCHAR}, #{dataStream,jdbcType=VARCHAR})" })
	@Options(useGeneratedKeys=true, keyProperty="fetchlogId", keyColumn="FETCHLOG_ID")
	int insertId(FetchLog record);
	
	@Select({
		"select * from ",
		"(SELECT FETCHLOG_ID, AGENCY_CD, SITE_NO, DATA_SOURCE, STARTED_AT, STATUS, PROBLEM, CT, ",
		" ELAPSED_SEC, SPECIFIER, FETCHER, DATA_STREAM",
		" from GW_DATA_PORTAL.FETCH_LOG",
		" where AGENCY_CD = #{agency_cd} and SITE_NO = #{site_no} ",
		" AND STARTED_AT IS NOT NULL ",
		" ORDER BY STARTED_AT DESC ",
		") ",
		"WHERE ROWNUM = 1 ",
		""})
	@ResultMap("BaseResultMap")
	FetchLog selectLatestByWell(@Param("agency_cd") String agency_cd, @Param("site_no") String site_no);
	
	@Select({
		"select agency_cd, data_stream, status, count(*) ct, avg(elapsed_sec) avg",
		"from gw_data_portal.fetch_log",
		"where fetcher = 'WebRetriever' and data_source is not null",
		"and started_at between (#{day,jdbcType=DATE} - 12/24) and (#{day,jdbcType=DATE} + 12/24)",
		"group by agency_cd, data_stream, status",
		"order by agency_cd, data_stream, status",
		""})
	List<Map<String,Object>> statisticsByDay(@Param("day") Date day);

	/* Ordering by SPECIFIER as a proxy to DATA_SOURCE, seems that is null */
	@Select({
		" SELECT FETCHLOG_ID, AGENCY_CD, SITE_NO, DATA_SOURCE, STARTED_AT, STATUS, PROBLEM, CT, ",
		" ELAPSED_SEC, SPECIFIER, FETCHER, DATA_STREAM",
		" from GW_DATA_PORTAL.FETCH_LOG",
		" where AGENCY_CD = #{agency_cd} and SITE_NO = #{site_no} ",
		" ORDER BY SPECIFIER, STARTED_AT DESC ",
		""})
	@ResultMap("BaseResultMap")
	List<FetchLog> fetchHistory(@Param("agency_cd") String agency_cd, @Param("site_no") String site_no);
}