package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

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


public class QWTableCache implements Cache {

	private static Logger logger = LoggerFactory.getLogger(QWTableCache.class);
	
	public QWTableCache(DataSource ds) {
		this.ds = ds;
	}

	private DataSource ds;
	// private PreparedStatement insert;
	
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

	@Override
	public boolean fetchWellData(Specifier spec, Pipeline pipe)
			throws IOException {
		
		Connection conn;		
		try {
			conn = ds.getConnection();
			try {
				// To use the getCLOBVal function, the table alias must be explicit; use of the implicit alias fails with error ORA-00904
				String query = "SELECT qw.fetch_date, qw.xml.getCLOBVal() FROM QW qw WHERE qw.agency_cd = ? and qw.site_no = ? order by qw.fetch_date DESC";
				PreparedStatement ps = conn.prepareStatement(query);
				try {
					ps.setMaxRows(1);
					ps.setString(1, spec.getAgencyID());
					ps.setString(2, spec.getFeatureID());
					
					Clob clob = null;
					Timestamp fetch_date = null;
					
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						fetch_date = rs.getTimestamp(1);
						clob = rs.getClob(2);
					}
					
					logger.debug("got clob for specifier {}, fetch_date={}, length={}", new Object[]{spec, fetch_date, clob.length()});
					
					// So. Really. Does this change anything about the thread safety of the program?
					final Clob flob = clob;
					
					if (clob != null) {
						Supplier<InputStream> supp = new Supplier<InputStream>() {
			
							@Override
							public InputStream get() {
								try {
									return flob.getAsciiStream();
								} catch (SQLException e) {
									throw new RuntimeException(e);
								}
							}
							
						};
						pipe.setInputSupplier(supp);
						return true;
					}
			
					return false;
				} finally {
					// Can I do this while the Clob is dangling?
					// Or do I need to close the statement after the input stream is finished?
					// ps.close();
				}
			} catch (SQLException sqle) {
				throw new IOException(sqle);
			} finally {
				// conn.close();
			}
		} catch (SQLException e1) {
			throw new IOException(e1);
		}
	}

	@Override
	public boolean contains(Specifier spec) {
		Connection conn;
		try {
			conn = ds.getConnection();
			try {
				String query = "SELECT count(*) FROM QW WHERE agency_cd = ? and site_no = ? ";
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
						"from QW " +
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
					logger.debug("Row fetch_date {}, length {}", modified, length);
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

	// TODO Is this legit? Can we do the insert before the Clob is filled up?
	// The alternative would be to create the row with an empty clob, then select 
	// it for update before writing into the clob.
	private void insert(Connection conn, WellRegistryKey key, Clob clob) 
			throws SQLException
	{
		String SQLTEXT = "INSERT INTO qw(agency_cd,site_no,fetch_date,xml) VALUES (" +
				"?, ?, ?, XMLType(?))";
		
		PreparedStatement s = conn.prepareStatement(SQLTEXT);
		
		s.setString(1, key.getAgencyCd());
		s.setString(2, key.getSiteNo());
		s.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
		s.setClob(4, clob);
				
		s.execute();
		
	}

}
