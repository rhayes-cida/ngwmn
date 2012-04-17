package gov.usgs.ngwmn.dm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataExample;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataKey;

public interface CacheMetaDataMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	int countByExample(CacheMetaDataExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	int deleteByExample(CacheMetaDataExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	@Delete({ "delete from GW_DATA_PORTAL.CACHE_META_DATA",
			"where AGENCY_CD = #{agencyCd,jdbcType=VARCHAR}",
			"and DATA_TYPE = #{dataType,jdbcType=VARCHAR}",
			"and SITE_NO = #{siteNo,jdbcType=VARCHAR}" })
	int deleteByPrimaryKey(CacheMetaDataKey key);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	@Insert({
			"insert into GW_DATA_PORTAL.CACHE_META_DATA (AGENCY_CD, DATA_TYPE, ",
			"SITE_NO, SUCCESS_CT, ",
			"FAIL_CT, FIRST_DATA_DT, ",
			"LAST_DATA_DT, MOST_RECENT_FETCH_DT)",
			"values (#{agencyCd,jdbcType=VARCHAR}, #{dataType,jdbcType=VARCHAR}, ",
			"#{siteNo,jdbcType=VARCHAR}, #{successCt,jdbcType=DECIMAL}, ",
			"#{failCt,jdbcType=DECIMAL}, #{firstDataDt,jdbcType=TIMESTAMP}, ",
			"#{lastDataDt,jdbcType=TIMESTAMP}, #{mostRecentFetchDt,jdbcType=TIMESTAMP})" })
	int insert(CacheMetaData record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	int insertSelective(CacheMetaData record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	List<CacheMetaData> selectByExample(CacheMetaDataExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	@Select({
			"select",
			"AGENCY_CD, DATA_TYPE, SITE_NO, SUCCESS_CT, FAIL_CT, FIRST_DATA_DT, LAST_DATA_DT, ",
			"MOST_RECENT_FETCH_DT", "from GW_DATA_PORTAL.CACHE_META_DATA",
			"where AGENCY_CD = #{agencyCd,jdbcType=VARCHAR}",
			"and DATA_TYPE = #{dataType,jdbcType=VARCHAR}",
			"and SITE_NO = #{siteNo,jdbcType=VARCHAR}" })
	@ResultMap("BaseResultMap")
	CacheMetaData selectByPrimaryKey(CacheMetaDataKey key);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	int updateByExampleSelective(@Param("record") CacheMetaData record,
			@Param("example") CacheMetaDataExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	int updateByExample(@Param("record") CacheMetaData record,
			@Param("example") CacheMetaDataExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	int updateByPrimaryKeySelective(CacheMetaData record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.CACHE_META_DATA
	 * @mbggenerated  Mon Apr 16 17:11:28 CDT 2012
	 */
	@Update({ "update GW_DATA_PORTAL.CACHE_META_DATA",
			"set SUCCESS_CT = #{successCt,jdbcType=DECIMAL},",
			"FAIL_CT = #{failCt,jdbcType=DECIMAL},",
			"FIRST_DATA_DT = #{firstDataDt,jdbcType=TIMESTAMP},",
			"LAST_DATA_DT = #{lastDataDt,jdbcType=TIMESTAMP},",
			"MOST_RECENT_FETCH_DT = #{mostRecentFetchDt,jdbcType=TIMESTAMP}",
			"where AGENCY_CD = #{agencyCd,jdbcType=VARCHAR}",
			"and DATA_TYPE = #{dataType,jdbcType=VARCHAR}",
			"and SITE_NO = #{siteNo,jdbcType=VARCHAR}" })
	int updateByPrimaryKey(CacheMetaData record);

	@Select({
		"select",
		"AGENCY_CD, DATA_TYPE, SITE_NO, SUCCESS_CT, FAIL_CT, FIRST_DATA_DT, LAST_DATA_DT, ",
		"MOST_RECENT_FETCH_DT", 
		"from GW_DATA_PORTAL.CACHE_META_DATA",
		"where AGENCY_CD = #{agencyCd,jdbcType=VARCHAR}",
		"order by AGENCY_CD, SITE_NO, DATA_TYPE"
	})
	@ResultMap("BaseResultMap")
	List<CacheMetaData> listByAgencyCd(String agencyCd);

	@Select({
		"select",
		"AGENCY_CD, DATA_TYPE, SITE_NO, SUCCESS_CT, FAIL_CT, FIRST_DATA_DT, LAST_DATA_DT, ",
		"MOST_RECENT_FETCH_DT", 
		"from GW_DATA_PORTAL.CACHE_META_DATA",
		"WHERE MOST_RECENT_FETCH_DT is not null",
		"order by AGENCY_CD, SITE_NO, DATA_TYPE"
	})
	@ResultMap("BaseResultMap")
	List<CacheMetaData> listAll();
}