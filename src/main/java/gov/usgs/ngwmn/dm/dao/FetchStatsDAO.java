package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.WellDataType;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

// Not using MyBatis as this generates bulk data which I don't wish to represent as objects.

public class FetchStatsDAO {
	
	private DataSource datasource;
	private WellDataType type;

	public FetchStatsDAO(DataSource ds, WellDataType t) {
		this.datasource = ds;
		type = t;
	}
	
	String timeSeriesQuery = 
			"select "+

			"trunc(fl.started_at) fetch_date, "+
							
			"(select count(*) from GW_DATA_PORTAL.%1$s_CACHE_STATS cs1 "+
											 "where trunc(cs1.fetch_date) = trunc(fl.started_at) "+
											 "and cs1.published = 'Y') success, "+
							
			"(select count(*) from GW_DATA_PORTAL.%1$s_CACHE_STATS cs2 "+
											 "where trunc(cs2.fetch_date) = trunc(fl.started_at) "+
											 "and cs2.published = 'N') \"EMPTY\", "+
											 
			"count(distinct fail_log.fetchlog_id) fail, "+

			"count(*) attempts "+

			"from "+
			"(select * from GW_DATA_PORTAL.fetch_log "+
			 "where fetch_log.data_stream = '%1$s' "+
			 "and (fetcher = 'WebRetriever' or fetcher = 'PrefetchI') ) fl "+
			 
			 "left join GW_DATA_PORTAL.fetch_log fail_log "+
			 "on (fail_log.fetchlog_id = fl.fetchlog_id and fail_log.status = 'FAIL') "+
			 
			"group by trunc(fl.started_at) "+
			"order by trunc(fl.started_at) asc";

	String timeSeriesAgencyQuery = 
			"select "+

			"trunc(fl.started_at) fetch_date, "+
							
			"(select count(*) from GW_DATA_PORTAL.%1$s_CACHE_STATS cs1 "+
											 "where trunc(cs1.fetch_date) = trunc(fl.started_at) " +
											 "and agency_cd = :agency "+
											 "and cs1.published = 'Y') success, "+
							
			"(select count(*) from GW_DATA_PORTAL.%1$s_CACHE_STATS cs2 "+
											 "where trunc(cs2.fetch_date) = trunc(fl.started_at) " +
											 "and agency_cd = :agency "+
											 "and cs2.published = 'N') \"EMPTY\", "+
											 
			"count(distinct fail_log.fetchlog_id) fail, "+

			"count(*) attempts "+

			"from "+
			"(select * from GW_DATA_PORTAL.fetch_log "+
			 "where fetch_log.data_stream = '%1$s' " +
			 "and agency_cd = :agency "+
			 "and (fetcher = 'WebRetriever' or fetcher = 'PrefetchI') ) fl "+
			 
			 "left join GW_DATA_PORTAL.fetch_log fail_log "+
			 "on (fail_log.fetchlog_id = fl.fetchlog_id and fail_log.status = 'FAIL') "+
			 
			"group by trunc(fl.started_at) "+
			"order by trunc(fl.started_at) asc";

	public <T> T timeSeriesAgencyData(String agency, ResultSetExtractor<T> rse)
	throws SQLException
	{
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		
		Map<String,String> params = Collections.singletonMap("agency", agency);
		String query = String.format(timeSeriesAgencyQuery, type.name());
		T value = template.query(query, params,  rse);
		
		return value;
	}
	
	public <T> T timeSeriesData(ResultSetExtractor<T> rse) 
	throws SQLException {
		JdbcTemplate t = new JdbcTemplate(datasource);
		
		String query = String.format(timeSeriesQuery, type.name());
		T value = t.query(query, rse);
		
		return value;
		
	}
	
	
	/**
	 * Raw data from cache statistics view; for all agencies if argument is null.
	 * 
	 * @param agency
	 * @return
	 * @throws SQLException
	 */
	public <T> T viewData(String agency, ResultSetExtractor<T> rse) throws SQLException {
		String query = "SELECT * " +
				"FROM GW_DATA_PORTAL.%1$s_CACHE_STATS " +
				"WHERE agency_cd = coalesce(?,agency_cd) " +
				"";
		
		query = String.format(query, type.name());
		JdbcTemplate t = new JdbcTemplate(datasource);
		T value = t.query(query, rse, agency);
		
		return value;		
	}
	
	public <T> T welldataAgeData(ResultSetExtractor<T> rse) throws SQLException {
		String query = 
				"select well_registry.agency_cd, well_registry.site_no, max(fetch_date) publication_date " +
				"from gw_data_portal.well_registry  " +
				"      left join gw_data_portal.%1$s_cache_stats " +
				"on well_registry.agency_cd = %1$s_cache_stats.agency_cd " +
				"   and well_registry.site_no = %1$s_cache_stats.site_no " +
				"where published = 'Y' or published is null " +
				"group by well_registry.agency_cd, well_registry.site_no ";
		
		query = String.format(query, type.name());
		JdbcTemplate t = new JdbcTemplate(datasource);
		T value = t.query(query, rse);
		
		return value;		
	}
		
}
