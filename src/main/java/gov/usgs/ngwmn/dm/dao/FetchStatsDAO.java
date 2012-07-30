package gov.usgs.ngwmn.dm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;
import javax.sql.RowSet;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.sun.rowset.CachedRowSetImpl;

// Not using MyBatis as this generates bulk data which I don't wish to represent as objects.

public class FetchStatsDAO {
	
	private DataSource datasource;

	public FetchStatsDAO(DataSource ds) {
		this.datasource = ds;
	}
	
	String timeSeriesQuery = 
			"select "+

			"trunc(fl.started_at) fetch_date, "+
							
			"(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1 "+
											 "where trunc(cs1.fetch_date) = trunc(fl.started_at) "+
											 "and cs1.published = 'Y') success, "+
							
			"(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2 "+
											 "where trunc(cs2.fetch_date) = trunc(fl.started_at) "+
											 "and cs2.published = 'N') \"EMPTY\", "+
											 
			"count(distinct fail_log.fetchlog_id) fail, "+

			"count(*) attempts "+

			"from "+
			"(select * from GW_DATA_PORTAL.fetch_log "+
			 "where fetch_log.data_stream = 'WATERLEVEL' "+
			 "and fetcher = 'WebRetriever' ) fl "+
			 
			 "left join GW_DATA_PORTAL.fetch_log fail_log "+
			 "on (fail_log.fetchlog_id = fl.fetchlog_id and fail_log.status = 'FAIL') "+
			 
			"group by trunc(fl.started_at) "+
			"order by trunc(fl.started_at) asc";

	String timeSeriesAgencyQuery = 
			"select "+

			"trunc(fl.started_at) fetch_date, "+
							
			"(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1 "+
											 "where trunc(cs1.fetch_date) = trunc(fl.started_at) " +
											 "and agency_cd = :agency "+
											 "and cs1.published = 'Y') success, "+
							
			"(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2 "+
											 "where trunc(cs2.fetch_date) = trunc(fl.started_at) " +
											 "and agency_cd = :agency "+
											 "and cs2.published = 'N') \"EMPTY\", "+
											 
			"count(distinct fail_log.fetchlog_id) fail, "+

			"count(*) attempts "+

			"from "+
			"(select * from GW_DATA_PORTAL.fetch_log "+
			 "where fetch_log.data_stream = 'WATERLEVEL' " +
			 "and agency_cd = :agency "+
			 "and fetcher = 'WebRetriever' ) fl "+
			 
			 "left join GW_DATA_PORTAL.fetch_log fail_log "+
			 "on (fail_log.fetchlog_id = fl.fetchlog_id and fail_log.status = 'FAIL') "+
			 
			"group by trunc(fl.started_at) "+
			"order by trunc(fl.started_at) asc";

	public RowSet overallTimeSeries() throws SQLException {
		
		Connection conn = datasource.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(timeSeriesQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = ps.executeQuery();
			
			CachedRowSetImpl value = new CachedRowSetImpl();
			value.populate(rs);
			
			return value;
		}
		finally {
			conn.close();
		}
	}
	
	public RowSet successTimeSeries() throws SQLException {
		String query = 
				"select agency_cd, trunc(fetch_date) fetched, count(*) ct " +
				"from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS " +
				"where published = 'Y' " +
				"group by agency_cd,trunc(fetch_date) ";
		
		Connection conn = datasource.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = ps.executeQuery();
			
			CachedRowSetImpl value = new CachedRowSetImpl();
			value.populate(rs);
			
			return value;
		}
		finally {
			conn.close();
		}
	}

	public RowSet failTimeSeries() throws SQLException {
		String query = 
				"select agency_cd, trunc(fetch_date) fetched, count(*) ct " +
				"from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS " +
				"where coalesce(published, 'N') = 'N' " +
				"group by agency_cd,trunc(fetch_date) ";
		
		Connection conn = datasource.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = ps.executeQuery();
			
			CachedRowSetImpl value = new CachedRowSetImpl();
			value.populate(rs);
			
			return value;
		}
		finally {
			conn.close();
		}
	}

	public <T> T timeSeriesAgencyData(String agency, ResultSetExtractor<T> rse)
	throws SQLException
	{
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
		
		Map<String,String> params = Collections.singletonMap("agency", agency);
		T value = template.query(timeSeriesAgencyQuery, params,  rse);
		
		return value;
	}
	
	public <T> T timeSeriesData(ResultSetExtractor<T> rse) 
	throws SQLException {
		JdbcTemplate t = new JdbcTemplate(datasource);
		T value = t.query(timeSeriesQuery, rse);
		
		return value;
		
	}
	
	
	/**
	 * Raw data from waterlevel_cache_stats view; for all agencies if argument is null.
	 * 
	 * @param agency
	 * @return
	 * @throws SQLException
	 */
	public <T> T waterlevelData(String agency, ResultSetExtractor<T> rse) throws SQLException {
		String query = "SELECT * " +
				"FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS " +
				"WHERE agency_cd = coalesce(?,agency_cd) " +
				"";
		
		JdbcTemplate t = new JdbcTemplate(datasource);
		T value = t.query(query, rse, agency);
		
		return value;		
	}
}
