package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryExample;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

public interface WellRegistryMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.WELL_REGISTRY
	 * @mbggenerated  Fri Mar 30 10:17:05 CDT 2012
	 */
	int countByExample(WellRegistryExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.WELL_REGISTRY
	 * @mbggenerated  Fri Mar 30 10:17:05 CDT 2012
	 */
	List<WellRegistry> selectByExample(WellRegistryExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table GW_DATA_PORTAL.WELL_REGISTRY
	 * @mbggenerated  Fri Mar 30 10:17:05 CDT 2012
	 */
	@Select({
			"select",
			"AGENCY_CD, SITE_NO, AGENCY_NM, AGENCY_MED, SITE_NAME, DEC_LAT_VA, DEC_LONG_VA, ",
			"HORZ_DATUM, ALT_VA, ALT_DATUM_CD, NAT_AQUIFER_CD, NAT_AQFR_DESC, LOCAL_AQUIFER_NAME, ",
			"QW_SN_FLAG, QW_BASELINE_FLAG, QW_WELL_CHARS, QW_WELL_TYPE, WL_SN_FLAG, WL_BASELINE_FLAG, ",
			"WL_WELL_CHARS, WL_WELL_TYPE, DATA_PROVIDER, QW_SYS_NAME, WL_SYS_NAME, INSERT_DATE, ",
			"MY_SITEID, DISPLAY_FLAG, WL_DATA_PROVIDER, QW_DATA_PROVIDER, LITH_DATA_PROVIDER, ",
			"CONST_DATA_PROVIDER, WELL_DEPTH, WELL_DEPTH_UNITS, LINK, WL_WELL_PURPOSE, QW_WELL_PURPOSE, ",
			"WL_WELL_PURPOSE_NOTES, QW_WELL_PURPOSE_NOTES, STATE_CD, COUNTY_CD, UPDATE_DATE, ",
			"ALT_UNITS, LOCAL_AQUIFER_CD", "from GW_DATA_PORTAL.WELL_REGISTRY",
			"where AGENCY_CD = #{agencyCd,jdbcType=VARCHAR}",
			"and SITE_NO = #{siteNo,jdbcType=VARCHAR}" })
	@ResultMap("BaseResultMap")
	WellRegistry selectByPrimaryKey(WellRegistryKey key);
}