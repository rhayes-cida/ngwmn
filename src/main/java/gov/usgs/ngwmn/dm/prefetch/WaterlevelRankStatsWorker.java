package gov.usgs.ngwmn.dm.prefetch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

import ch.qos.logback.classic.Logger;

public class WaterlevelRankStatsWorker {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
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
	
	String countq = " " +
			"SELECT count(*) " +
			"FROM gw_data_portal.waterlevel_cache_stats " +
			"WHERE published = 'Y' " +
			"AND sample_ct > 0 " +
			"AND NOT EXISTS ( " +
			"  SELECT * FROM gw_data_portal.waterlevel_data_stats ds " +
			"  WHERE ds.waterlevel_cache_id = waterlevel_cache_stats.waterlevel_cache_id " +
			"  ) ";

	private int backoff = 0;
	
	public int updateOne() {
		if (backoff > 0) {
			backoff--;
			logger.trace("backing off ct = {}", backoff);
			return -backoff;
		}
		
		JdbcTemplate template = new JdbcTemplate(ds);
		
		if (logger.isInfoEnabled()) {
			int count = template.queryForInt(countq);
			logger.info("count of unranked published observations {}", count);
		}

		// get one waterlevel sample that needs to be updated
		try {
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
		} catch (EmptyResultDataAccessException nothingToDo) {
			backoff = 10;
			logger.info("Nothing to do, will skip {} tries", backoff);
			return -1;
		}
	}

	public int count() {
		JdbcTemplate template = new JdbcTemplate(ds);
		final int count = template.queryForInt(countq);
		
		return count;
	}
	
	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

}
