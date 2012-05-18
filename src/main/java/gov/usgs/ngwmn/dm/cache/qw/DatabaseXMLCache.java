package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.WellDataType;
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
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.lob.LobHandler;


public class DatabaseXMLCache implements Cache {

	private static Logger logger = LoggerFactory.getLogger(DatabaseXMLCache.class);
	
	private final String tablename;
	private final WellDataType wdt;
	
	public DatabaseXMLCache(DataSource ds, WellDataType datatype, LobHandler h) {
		this.ds = ds;
		wdt = datatype;
		this.tablename = wdt.name() + "_CACHE";
		this.handler = h;
	}

	private DataSource ds;
	// private PreparedStatement insert;
	private LobHandler handler;
	
	
	@Override
	public WellDataType getDatatype() {
		return wdt;
	}


	@Override
	public OutputStream destination(final Specifier well) throws IOException {
		try {
			// TODO This is a rather ugly.
			
			final WellRegistryKey key = new WellRegistryKey(well.getAgencyID(), well.getFeatureID());
			final Connection conn = ds.getConnection();
			
			final Clob clob = conn.createClob();
			// TODO Ascii?
			OutputStream bos = clob.setAsciiStream(1);
			
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
				OutputStream digested = new DigestOutputStream(bos, md5);
				bos = digested;
			} catch (NoSuchAlgorithmException e) {
				logger.warn("Problem getting MD5 digest, will not record hash for " + well, e);
			}
			final MessageDigest md5final = md5; // Wow, now I feel so threadsafe. Not.
			
			OutputStream fos = new FilterOutputStream(bos) {

				@Override
				public void close() throws IOException {
					super.close();
					try {
						long length = clob.length();
						logger.debug("About to insert, clob.length={}", length);
						String hash = null;
						if (md5final != null) {
							byte[] raw = md5final.digest();
							char[] hex = Hex.encodeHex(raw);
							hash = new String(hex);
						}
						insert(conn, key, clob, hash);
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
		String md5 = null;
		
		try {
		Connection conn = ds.getConnection();
			try {
				// TODO Do not really need to fetch all rows.
				PreparedStatement ps = conn.prepareStatement("SELECT " +
						" fetch_date " +
						",dbms_lob.getlength(xmltype.getclobval(xml)) sz " +
						",md5 " +
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
					md5 = rs.getString(3);
					logger.trace("Row fetch_date {}, length {}", modified, length);
				}
			} finally {
				conn.close();
			}
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		}
		
		CacheInfo val = new CacheInfo(created, exists, modified, length, md5);
		return val;
	}

	// Cannot do the insert until the Clob has been filled up
	private void insert(Connection conn, WellRegistryKey key, Clob clob, String hash) 
			throws SQLException
	{
		String SQLTEXT = "INSERT INTO GW_DATA_PORTAL."+tablename+"(agency_cd,site_no,fetch_date,xml,md5) VALUES (" +
				"?, ?, ?, XMLType(?), ?)";
		
		int[] pkColumns = {1};
		PreparedStatement s = conn.prepareStatement(SQLTEXT, pkColumns);
		
		s.setString(1, key.getAgencyCd());
		s.setString(2, key.getSiteNo());
		s.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
		s.setClob(4, clob);
		s.setString(5, hash);
				
		s.executeUpdate();
		
		ResultSet gkrs = s.getGeneratedKeys();
		while (gkrs.next()) {
			logger.info("Generated key {}", gkrs.getBigDecimal(1));
		}
		
	}

	public int cleanCache(WellRegistryKey key) throws Exception {
		
		String cleanable = 
				"select t1."+tablename+"_id from GW_DATA_PORTAL."+tablename+" t1 "+
				" where t1.fetch_date < (select max(t2.fetch_date) from GW_DATA_PORTAL."+tablename+" t2"+
				"                     where t2.md5 = t1.md5"+
				"                     and t2.agency_cd = t1.agency_cd"+
				"                     and t2.site_no = t1.site_no)"+
				" AND t1.xml is not null " +
				" AND t1.agency_cd = ? " +
				" AND t1.site_no = ? ";
		
		String update = 
				"update GW_DATA_PORTAL."+tablename+" " +
				"set xml = null " +
				"where "+tablename+"_id = ? ";
		
		logger.debug("query is {}", cleanable);
		int ct = 0;
		Connection conn = ds.getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement(cleanable);
			
			pstmt.setString(1, key.getAgencyCd());
			pstmt.setString(2, key.getSiteNo());
			
			List<Integer> killist = new ArrayList<Integer>();
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				killist.add(rs.getInt(1));
			}
			pstmt.close();
			
			PreparedStatement killer = conn.prepareStatement(update);
			for (Integer i : killist) {
				killer.setInt(1, i);
				int uct = killer.executeUpdate();
				logger.info("cleaned {} result {}", i, uct);
				ct += uct;
			}
		} finally {
			conn.close();
		}
		
		return ct;
	}
	
	public int fixMD5() throws Exception {
		String select = "SELECT cachetable."+tablename+"_id id, cachetable.xml.getCLOBVal() "+
				"FROM GW_DATA_PORTAL."+tablename+" cachetable " +
				"WHERE cachetable.md5 IS NULL " +
				"AND cachetable.xml IS NOT NULL ";
		String update = "UPDATE GW_DATA_PORTAL."+tablename+" " +
				"SET md5 = ? " +
				"WHERE "+tablename+"_id = ? " +
						"AND md5 IS NULL ";

		int ct = 0;
		Connection conn = ds.getConnection();
		try {
			PreparedStatement query = conn.prepareStatement(select);
			PreparedStatement setter = conn.prepareStatement(update);

			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				int idx = rs.getInt(1);
				InputStream xis = rs.getAsciiStream(2);

				logger.info("Working on {}", idx);
				
				String md5 = null;
				try {
					md5 = md5digest(xis);
				} finally {
					xis.close();
				}
				setter.setString(1, md5);
				setter.setInt(2, idx);

				logger.info("Updating md5 of {} to {}", idx, md5);
				int uc = setter.executeUpdate();
				logger.debug("update count is {}", uc);
				ct += uc;
			}
		} finally {
			conn.close();
		}
		return ct;
	}

	protected String md5digest(InputStream xis)
			throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] buf = new byte[1024];
		while (true) {
			int bct = xis.read(buf);
			if (bct <= 0) {
				break;
			}
			digest.update(buf, 0, bct);
		}

		byte[] raw = digest.digest();
		char[] hex = Hex.encodeHex(raw);
		String md5 = new String(hex);
		return md5;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DatabaseXMLCache [")
				.append("tablename=").append(tablename)
		// 		.append(", ds=").append(ds)
				.append("]");
		return builder.toString();
	}
	
	

}
