package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class QWTableCache implements Cache {

	private Connection conn;
	// private PreparedStatement insert;
	
	@Override
	public OutputStream destination(Specifier well) throws IOException {
		try {
			// TODO Will this work? 
			// TODO We need to capture the close event and commit the transaction on success
			
			WellRegistryKey key = new WellRegistryKey(well.getAgencyID(), well.getFeatureID());
			Clob clob = insert(key);
			return clob.setAsciiStream(1);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean fetchWellData(Specifier spec, Pipeline pipe)
			throws IOException {
		
		try {
			// To use the getCLOBVal function, the table alias must be explicit; use of the implicit alias fails with error ORA-00904
			String query = "SELECT qw.xml.getCLOBVal() FROM QW qw WHERE qw.agency_cd = ? and qw.site_no = ? order by qw.fetch_date DESC";
			PreparedStatement ps = conn.prepareStatement(query);
			try {
				ps.setMaxRows(1);
				ps.setString(1, spec.getAgencyID());
				ps.setString(2, spec.getFeatureID());
				
				Clob clob = null;
				
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					clob = rs.getClob(1);
				}
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
				ps.close();
			}
		} catch (SQLException sqle) {
			throw new IOException(sqle);
		}
	}

	@Override
	public boolean contains(Specifier spec) {
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
			
		} catch (SQLException sqle) {
			return false;
		}
	}

	@Override
	public CacheInfo getInfo(Specifier spec) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO Is this legit? Can we do the insert before the Clob is filled up?
	// The alternative would be to create the row with an empty clob, then select 
	// it for update before writing into the clob.
	private Clob insert(WellRegistryKey key)
			throws Exception
	{
		String SQLTEXT = "INSERT INTO qw(agency_cd,site_no,fetch_date,xml) VALUES (" +
				"?, ?, ?, XMLType(?))";

		Clob value = conn.createClob();
		
		PreparedStatement s = conn.prepareStatement(SQLTEXT);
		
		s.setString(1, key.getAgencyCd());
		s.setString(2, key.getSiteNo());
		s.setDate(3, new java.sql.Date(System.currentTimeMillis()));
		s.setClob(4, value);
				
		s.execute();
		
		return value;
	}

}
