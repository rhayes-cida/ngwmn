package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogExample;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

public interface FetchLogMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	int countByExample(FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	int deleteByExample(FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	@Delete({ "delete from GW_DATA_PORTAL.FETCH_LOG",
			"where FETCHLOG_ID = #{fetchlogId,jdbcType=NUMERIC}" })
	int deleteByPrimaryKey(Integer fetchlogId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	@Insert({
			"insert into GW_DATA_PORTAL.FETCH_LOG (FETCHLOG_ID, AGENCY_CD, ",
			"SITE_NO, DATA_SOURCE, ",
			"STARTED_AT, STATUS, ",
			"PROBLEM, CT, ELAPSED_SEC, ",
			"SPECIFIER, FETCHER)",
			"values (#{fetchlogId,jdbcType=NUMERIC}, #{agencyCd,jdbcType=VARCHAR}, ",
			"#{siteNo,jdbcType=VARCHAR}, #{source,jdbcType=VARCHAR}, ",
			"#{startedAt,jdbcType=TIMESTAMP}, #{status,jdbcType=CHAR}, ",
			"#{problem,jdbcType=VARCHAR}, #{ct,jdbcType=NUMERIC}, #{elapsedSec,jdbcType=FLOAT}, ",
			"#{specifier,jdbcType=VARCHAR}, #{fetcher,jdbcType=VARCHAR})" })
	int insert(FetchLog record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	int insertSelective(FetchLog record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	List<FetchLog> selectByExample(FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	@Select({
			"select",
			"FETCHLOG_ID, AGENCY_CD, SITE_NO, DATA_SOURCE, STARTED_AT, STATUS, PROBLEM, CT, ",
			"ELAPSED_SEC, SPECIFIER, FETCHER", "from GW_DATA_PORTAL.FETCH_LOG",
			"where FETCHLOG_ID = #{fetchlogId,jdbcType=NUMERIC}" })
	@ResultMap("BaseResultMap")
	FetchLog selectByPrimaryKey(Integer fetchlogId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	int updateByExampleSelective(@Param("record") FetchLog record,
			@Param("example") FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	int updateByExample(@Param("record") FetchLog record,
			@Param("example") FetchLogExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
	 */
	int updateByPrimaryKeySelective(FetchLog record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.FETCH_LOG
	 * @mbggenerated  Thu Apr 05 11:33:44 CDT 2012
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
			"FETCHER = #{fetcher,jdbcType=VARCHAR}",
			"where FETCHLOG_ID = #{fetchlogId,jdbcType=NUMERIC}" })
	int updateByPrimaryKey(FetchLog record);

	@SelectKey(statement="select fetch_log_seq.nextval from dual", resultType = int.class, before = true, keyProperty = "fetchlogId") 
	@Insert({
		"insert into GW_DATA_PORTAL.FETCH_LOG (FETCHLOG_ID, AGENCY_CD, ",
		"SITE_NO, DATA_SOURCE, ",
		"STARTED_AT, STATUS, ",
		"PROBLEM, CT, ELAPSED_SEC, ",
		"SPECIFIER, FETCHER)",
		"values (#{fetchlogId, jdbcType=NUMERIC}, #{agencyCd,jdbcType=VARCHAR}, ",
		"#{siteNo,jdbcType=VARCHAR}, #{source,jdbcType=VARCHAR}, ",
		"#{startedAt,jdbcType=TIMESTAMP}, #{status,jdbcType=CHAR}, ",
		"#{problem,jdbcType=VARCHAR}, #{ct,jdbcType=NUMERIC}, #{elapsedSec,jdbcType=FLOAT}, ",
		"#{specifier,jdbcType=VARCHAR}, #{fetcher,jdbcType=VARCHAR})" })
	@Options(useGeneratedKeys=true, keyProperty="fetchlogId", keyColumn="FETCHLOG_ID")
	int insertId(FetchLog record);
}