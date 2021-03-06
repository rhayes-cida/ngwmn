package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.WellDataType;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WellLogInspector implements Inspector {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private DataSource ds;
	
	
	@Override
	public WellDataType forDataType() {
		return WellDataType.LOG;
	}

	@Override
	public boolean acceptable(int cachekey) throws Exception {
		Connection conn = ds.getConnection();
		try {
			CallableStatement stat = conn.prepareCall("{call GW_DATA_PORTAL.INSPECT_WELL_LOG_DATA(?)}");
			stat.setInt(1, cachekey);
			
			boolean did = stat.execute();
			logger.debug("finished update, got {}", did);
			
			// TODO would be convenient if stored proc contained a select to supply this result set
			// (but that's seriously inconvenient in Oracle. sigh.)
			PreparedStatement ps = conn.prepareStatement(
					"SELECT wldq.md5,wldq.lithologyCount,wldq.constructionCount,wldq.wellCount " +
					"FROM GW_DATA_PORTAL.LOG_DATA_QUALITY wldq, GW_DATA_PORTAL.log_cache wlc " +
					"WHERE wldq.md5 = wlc.md5 AND wlc.log_cache_id = ?");
			ps.setInt(1, cachekey);
			
			int totct = 0;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String md5 = rs.getString(1);
				int lithologyCt = rs.getInt(2);
				int constructionCt = rs.getInt(3);
				int wellCount = rs.getInt(4);
				
				logger.debug("Stats for well log, id={} md5={}: lith {} const {} wellCt {}",
						new Object[] {cachekey, md5, lithologyCt, constructionCt, wellCount});
				
				totct += (lithologyCt + constructionCt + wellCount);
			}
			return totct > 0;
		} finally {
			conn.close();
		}
	}

	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

	
}
