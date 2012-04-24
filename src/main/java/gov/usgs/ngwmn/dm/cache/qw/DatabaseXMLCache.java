package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.lob.LobHandler;


public class DatabaseXMLCache implements Cache {

	private static Logger logger = LoggerFactory.getLogger(DatabaseXMLCache.class);
	
	private final String tablename;
	
	public DatabaseXMLCache(DataSource ds, String tablename, LobHandler h) {
		this.ds = ds;
		this.tablename = tablename;
		this.handler = h;
	}

	private DataSource ds;
	// private PreparedStatement insert;
	private LobHandler handler;
	
	@Override
	public OutputStream destination(final Specifier well) throws IOException {
		try {
			// TODO This is a rather ugly.
			
			final WellRegistryKey key = new WellRegistryKey(well.getAgencyID(), well.getFeatureID());
			final Connection conn = ds.getConnection();
			
			final Clob clob = conn.createClob();
			OutputStream bos = clob.setAsciiStream(1);
			
			OutputStream fos = new FilterOutputStream(bos) {

				@Override
				public void close() throws IOException {
					super.close();
					try {
						long length = clob.length();
						logger.debug("About to insert, clob.length={}", length);
						insert(conn, key, clob);
						conn.commit();
						// TODO Clean up clob?
						clob.free();
						conn.close();
						logger.info("saved data for {}, sz {}", well, length);
					} catch (SQLException sqle) {
						throw new IOException(sqle);
					}
				}
			};
			
			return fos;
		} catch (SQLException sqle) {
			throw new IOException(sqle);
		} finally {
			// is there anything we can do here?
		}
	}

	
	// TODO code review : the complexity on this method is too high such that the final return is not even called.
	@Override
	public boolean fetchWellData(final Specifier spec, Pipeline pipe)
			throws IOException {

		try {
			final Connection conn = ds.getConnection();
			// To use the getCLOBVal function, the table alias must be explicit; use of the implicit alias fails with error ORA-00904
			String query = "SELECT cachetable.fetch_date, cachetable.xml.getCLOBVal() FROM GW_DATA_PORTAL."+tablename+" cachetable WHERE cachetable.agency_cd = ? and cachetable.site_no = ? order by cachetable.fetch_date DESC";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setMaxRows(1);
			ps.setString(1, spec.getAgencyID());
			ps.setString(2, spec.getFeatureID());

			Timestamp fetch_date = null;
			InputStream stream = null;

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				fetch_date = rs.getTimestamp(1);
				stream = handler.getClobAsAsciiStream(rs, 2);
			}

			logger.debug("got stream for specifier {}, fetch_date={}, length={}", new Object[]{spec, fetch_date, (stream==null)?-1:stream.available()});

			if (stream == null) {
				conn.close();
				return false;
			}

			stream = new ConnectionClosingInputStream(conn, stream);
			Supplier<InputStream> supply = new SimpleSupplier<InputStream>(stream);

			pipe.setInputSupplier(supply);

			return true;
		} catch (SQLException e1) {
			throw new IOException(e1);
		}
	}

	private static class ConnectionClosingInputStream extends FilterInputStream {

		private Connection conn;
		
		public ConnectionClosingInputStream(Connection conn, InputStream in) {
			super(in);
			this.conn = conn;
		}

		@Override
		public void close() throws IOException {
			try {
				super.close();
			} finally {
				try {
					conn.close();
				} catch (SQLException ioe) {
					throw new IOException(ioe);
				}
			}
		}
	};
	
	@Override
	public boolean contains(Specifier spec) {
		Connection conn;
		try {
			conn = ds.getConnection();
			try {
				String query = "SELECT count(*) FROM GW_DATA_PORTAL."+tablename+" WHERE agency_cd = ? and site_no = ? ";
				PreparedStatement ps = conn.prepareStatement(query);
				try {
					ps.setString(1, spec.getAgencyID());
					ps.setString(2, spec.getFeatureID());
					
					ResultSet rs = ps.executeQuery();
					
					int ct = -1;
					while (rs.next()) {
						ct = rs.getInt(1);
					}
	
					return (ct > 0);
				} finally {
					ps.close();
				}
			} finally {
				conn.close();
			}
		} catch (SQLException sqle) {
			return false;
		}
	}

	@Override
	public CacheInfo getInfo(Specifier spec) {
		Timestamp created = null;
		Timestamp modified = null;
		boolean exists = false;
		long length = 0;
		
		try {
		Connection conn = ds.getConnection();
			try {
				PreparedStatement ps = conn.prepareStatement("SELECT fetch_date, " +
						"dbms_lob.getlength(xmltype.getclobval(xml)) sz " +
						"from GW_DATA_PORTAL."+tablename+" " +
						"where agency_cd = ? and site_no = ? " +
						"order by fetch_date ASC ");
				ps.setString(1, spec.getAgencyID());
				ps.setString(2, spec.getFeatureID());
			
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					exists = true;
					if (created == null) {
						created = rs.getTimestamp(1);
					}
					modified = rs.getTimestamp(1);
					length = rs.getLong(2);
					logger.trace("Row fetch_date {}, length {}", modified, length);
				}
			} finally {
				conn.close();
			}
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		}
		
		CacheInfo val = new CacheInfo(created, exists, modified, length);
		return val;
	}

	// Cannot do the insert until the Clob has been filled up
	private void insert(Connection conn, WellRegistryKey key, Clob clob) 
			throws SQLException
	{
		String SQLTEXT = "INSERT INTO GW_DATA_PORTAL."+tablename+"(agency_cd,site_no,fetch_date,xml) VALUES (" +
				"?, ?, ?, XMLType(?))";
		
		PreparedStatement s = conn.prepareStatement(SQLTEXT);
		
		s.setString(1, key.getAgencyCd());
		s.setString(2, key.getSiteNo());
		s.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
		s.setClob(4, clob);
				
		s.execute();
		
	}

}
