package gov.usgs.ngwmn.dm.prefetch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

public class WaterlevelRankStatsWorker {

	private DataSource ds;
	
	String query = " " +
			"SELECT max(waterlevel_cache_id) " +
			"FROM gw_data_portal.waterlevel_cache_stats " +
			"WHERE published = 'Y' " +
			"AND sample_ct > 0 " +
			"AND NOT EXISTS ( " +
			"  SELECT * FROM gw_data_portal.waterlevel_data_stats ds " +
			"  WHERE ds.waterlevel_cache_id = waterlevel_cache_stats.waterlevel_cache_id " +
			"  ) ";
	
	public int updateOne() {
		JdbcTemplate template = new JdbcTemplate(ds);
		
		
		// get one waterlevel sample that needs to be updated
		final int id = template.queryForInt(query);
		
		List<SqlParameter> params = Collections.emptyList();
		template.call(new CallableStatementCreator() {
			
			@Override
			public CallableStatement createCallableStatement(Connection con)
					throws SQLException {
				// TODO Should mark id as unrankable if stored procedure call fails
				CallableStatement v = con.prepareCall("{call GW_DATA_PORTAL.GATHER_WATERLEVEL_ORDER_STATS(?)}");
				v.setInt(1, id);
				return v;
			}
		}, params);
		
		return id;
	}

	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

}
