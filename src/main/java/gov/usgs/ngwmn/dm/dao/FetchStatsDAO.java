package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.WellDataType;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

// Not using MyBatis as this generates bulk data which I don't wish to represent as objects.

public class FetchStatsDAO {
	
	private DataSource datasource;
	private WellDataType type;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public FetchStatsDAO(DataSource ds, WellDataType t) {
		this.datasource = ds;
		type = t;
	}
	
	String timeSeriesQuery = "select * " + 
			"from ( " + 
			"  select status, trunc(started_at) FETCH_DATE " + 
			"  from gw_data_portal.fetch_log " + 
			"  where started_at is not null " +
			"  and fetch_log.data_stream = :stream " + 
			"  and fetcher = 'PrefetchI' " + 
			"  ) " + 
			"pivot( " + 
			"  count(*) " + 
			"  for status " + 
			"  in ('DONE' as \"DONE\",'FAIL' as \"FAIL\",'EMPY' \"EMPTY\", 'SKIP' \"SKIP\") " + 
			"  ) " + 
			"order by FETCH_DATE desc";

	String timeSeriesAgencyQuery = 
			"select * " + 
					"from ( " + 
					"  select status, trunc(started_at) FETCH_DATE " + 
					"  from gw_data_portal.fetch_log " + 
					"  where started_at is not null " +
					"  and fetch_log.data_stream = :stream " + 
					"  and agency_cd = :agency "+
					"  and fetcher = 'PrefetchI' " + 
					"  ) " + 
					"pivot( " + 
					"  count(*) " + 
					"  for status " + 
					"  in ('DONE' as \"DONE\",'FAIL' as \"FAIL\",'EMPY' \"EMPTY\", 'SKIP' \"SKIP\") " + 
					"  ) " + 
					"order by FETCH_DATE desc";

	String dateAgencyQuery = 
			"select * " + 
					"from ( " + 
					"  select status, trunc(started_at) FETCH_DATE " + 
					"  from gw_data_portal.fetch_log " + 
					"  where trunc(started_at) = :dt " +
					"  and fetch_log.data_stream = :stream " + 
					"  and agency_cd = :agency "+
					"  and fetcher = 'PrefetchI' " + 
					"  ) " + 
					"pivot( " + 
					"  count(*) " + 
					"  for status " + 
					"  in ('DONE' as \"DONE\",'FAIL' as \"FAIL\",'EMPY' \"EMPTY\", 'SKIP' \"SKIP\") " + 
					"  ) ";

	public <T> T timeSeriesAgencyData(String agency, ResultSetExtractor<T> rse)
	throws SQLException
	{
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		
		Map<String,String> params = new HashMap<String,String>(2);
		params.put("agency", agency);
		params.put("stream", type.name());
		logger.debug("calling timeSeriesAgencyData({})", params);
		try {
			T value = template.query(timeSeriesAgencyQuery, params,  rse);
		
			return value;
		} catch (DataAccessException sqe) {
			logger.warn("Problem in timeSeriesAgencyData", sqe);
			throw sqe;
		}
	}
	
	public <T> T dateAgencyData(String agency, Date d, ResultSetExtractor<T> rse)
	throws SQLException
	{
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		
		java.sql.Date sDte = new java.sql.Date(d.getTime());
		
		Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("agency", agency);
		params.put("stream", type.name());
		params.put("dt", sDte);
		logger.debug("calling datedAgencyData({})", params);
		try {
			T value = template.query(dateAgencyQuery, params,  rse);
		
			return value;
		} catch (DataAccessException sqe) {
			logger.warn("Problem in timeSeriesAgencyData", sqe);
			throw sqe;
		}
	}
			
	public <T> T timeSeriesData(ResultSetExtractor<T> rse) 
	throws SQLException {
		NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(datasource);
		
		Map<String,String> params = Collections.singletonMap("stream", type.name());

		logger.debug("calling timeSeriesData({})", params);
		T value = t.query(timeSeriesQuery, params, rse);
		
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
