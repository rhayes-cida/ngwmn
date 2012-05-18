package gov.usgs.ngwmn.dm.cache.qw;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaterQualityInspector implements Inspector {

	private DataSource ds;
	private transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	String bhq = 
			"select " +
			"   qc.md5," +
			"	xq.cn," +
			"	count(xq.dt)," +
			" 	min(xq.dt)," +
			"	max(xq.dt) " +
			"from " +
			" 	quality_cache qc," +
			"	XMLTable(" +
			"'for $r in /*:Results/Result " +
			" return " +
			"   <r>" +
			"      <cn>{$r/*:ResultDescription/*:CharacteristicName}</cn>" +
			"      <dt>{$r/date}</dt>" +
			"   </r>' " +
			" passing qc.xml" +
			" columns " +
			"  \"CN\" varchar(80) path 'cn', " +
			"  \"DT\" date path 'dt'" +
			") xq " +
			"WHERE quality_cache_id = ? " +
			"group by md5, xq.cn ";
	
	@Override
	public boolean acceptable(int cachekey) throws Exception {
		Connection conn = ds.getConnection();
		try {
			PreparedStatement stat = conn.prepareStatement(bhq);
			stat.setInt(1, cachekey);
			
			int totct = 0;
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				String md5= rs.getString(1);
				String name = rs.getString(2);
				int ct = rs.getInt(3);
				Date frst = rs.getDate(4);
				Date lst = rs.getDate(5);
				
				logger.debug("Stats for quality, id={} md5={}: nm {} ct {} min {} max {}",
						new Object[] {cachekey, md5, name, ct, frst, lst});
				
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
