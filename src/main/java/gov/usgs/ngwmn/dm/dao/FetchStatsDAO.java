package gov.usgs.ngwmn.dm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.sql.RowSet;

import com.sun.rowset.CachedRowSetImpl;

// Not using MyBatis as this generates bulk data which I don't wish to represent as objects.

public class FetchStatsDAO {
	
	private DataSource datasource;

	public FetchStatsDAO(DataSource ds) {
		this.datasource = ds;
	}
	
	public RowSet overallTimeSeries() throws SQLException {
		String query = 
				"select trunc(fl.started_at) fetched, " +
				"(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1" +
				"				 where trunc(cs1.fetch_date) = trunc(fl.started_at) " +
				"				 and cs1.published = 'Y') success," +
				"				(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2" +
				"				 where trunc(cs2.fetch_date) = trunc(fl.started_at) " +
				"				 and cs2.published = 'N') failure," +
				"				 count(*) trials" +
				"		 from GW_DATA_PORTAL.fetch_log fl" +
				"				where fl.data_source is not null" +
				"				group by trunc(fl.started_at) " +
				" order by trunc(fl.started_at) asc ";
		
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


}
