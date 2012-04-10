package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.dm.cache.Cache;
import gov.usgs.ngwmn.dm.cache.CacheInfo;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Specifier spec) {
		// TODO Auto-generated method stub
		return false;
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
