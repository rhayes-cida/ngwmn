package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.dm.dao.LogDataQuality;
import gov.usgs.ngwmn.dm.dao.LogDataQualityExample;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface LogDataQualityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    int countByExample(LogDataQualityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    int deleteByExample(LogDataQualityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    @Delete({
        "delete from GW_DATA_PORTAL.LOG_DATA_QUALITY",
        "where MD5 = #{md5,jdbcType=CHAR}"
    })
    int deleteByPrimaryKey(String md5);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    @Insert({
        "insert into GW_DATA_PORTAL.LOG_DATA_QUALITY (MD5, DEPTH, LATITUDE, ",
        "LONGITUDE, LITHOLOGYCOUNT, ",
        "CONSTRUCTIONCOUNT)",
        "values (#{md5,jdbcType=CHAR}, #{depth,jdbcType=FLOAT}, #{latitude,jdbcType=FLOAT}, ",
        "#{longitude,jdbcType=FLOAT}, #{lithologycount,jdbcType=DECIMAL}, ",
        "#{constructioncount,jdbcType=DECIMAL})"
    })
    int insert(LogDataQuality record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    int insertSelective(LogDataQuality record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    List<LogDataQuality> selectByExample(LogDataQualityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    @Select({
        "select",
        "MD5, DEPTH, LATITUDE, LONGITUDE, LITHOLOGYCOUNT, CONSTRUCTIONCOUNT",
        "from GW_DATA_PORTAL.LOG_DATA_QUALITY",
        "where MD5 = #{md5,jdbcType=CHAR}"
    })
    @ResultMap("BaseResultMap")
    LogDataQuality selectByPrimaryKey(String md5);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    int updateByExampleSelective(@Param("record") LogDataQuality record, @Param("example") LogDataQualityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    int updateByExample(@Param("record") LogDataQuality record, @Param("example") LogDataQualityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    int updateByPrimaryKeySelective(LogDataQuality record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.LOG_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    @Update({
        "update GW_DATA_PORTAL.LOG_DATA_QUALITY",
        "set DEPTH = #{depth,jdbcType=FLOAT},",
          "LATITUDE = #{latitude,jdbcType=FLOAT},",
          "LONGITUDE = #{longitude,jdbcType=FLOAT},",
          "LITHOLOGYCOUNT = #{lithologycount,jdbcType=DECIMAL},",
          "CONSTRUCTIONCOUNT = #{constructioncount,jdbcType=DECIMAL}",
        "where MD5 = #{md5,jdbcType=CHAR}"
    })
    int updateByPrimaryKey(LogDataQuality record);
}