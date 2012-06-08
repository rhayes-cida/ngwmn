package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.WellDataType;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaterLevelInspector implements Inspector {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private DataSource ds;
	
	
	@Override
	public WellDataType forDataType() {
		return WellDataType.WATERLEVEL;
	}

	@Override
	public boolean acceptable(int cachekey) throws Exception {
		Connection conn = ds.getConnection();
		try {
			CallableStatement stat = conn.prepareCall("{call GW_DATA_PORTAL.INSPECT_WATERLEVEL_DATA(?)}");
			stat.setInt(1, cachekey);
			
			boolean did = stat.execute();
			logger.debug("finished update, got {}", did);
			
			// It would be convenient if stored proc contained a select to supply this result set,
			// except that's not easy in Oracle.
			PreparedStatement ps = conn.prepareStatement(
					"SELECT wldq.md5,wldq.firstDate,wldq.lastDate,wldq.ct " +
					"FROM GW_DATA_PORTAL.WATERLEVEL_DATA_QUALITY wldq, GW_DATA_PORTAL.waterlevel_cache wlc " +
					"WHERE wldq.md5 = wlc.md5 AND wlc.waterlevel_cache_id = ?");
			ps.setInt(1, cachekey);
			
			int totct = 0;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String md5= rs.getString(1);
				Date frst = rs.getDate(2);
				Date lst = rs.getDate(3);
				int ct = rs.getInt(4);
				
				logger.debug("Stats for waterlevel, id={} md5={}: ct {} min {} max {}",
						new Object[] {cachekey, md5, ct, frst, lst});
				
				totct += ct;
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
