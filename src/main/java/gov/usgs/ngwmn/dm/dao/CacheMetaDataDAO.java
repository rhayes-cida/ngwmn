package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.WellDataType;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheMetaDataDAO {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private CacheMetaDataMapper mapper;
	private DataSource dataSource;
	
	
	public CacheMetaDataDAO(CacheMetaDataMapper mapper) {
		super();
		this.mapper = mapper;
	}

	public void setDataSource(DataSource ds) {
		dataSource = ds;
	}
	
	public int countByExample(CacheMetaDataExample example) {
		return mapper.countByExample(example);
	}

	public List<CacheMetaData> selectByExample(CacheMetaDataExample example) {
		return mapper.selectByExample(example);
	}

	public List<CacheMetaData> listByAgencyCd(String agencyCd) {
		return mapper.listByAgencyCd(agencyCd);
	}

	public List<CacheMetaData> listAll() {
		return mapper.listAll();
	}
	
	public List<CacheMetaData> listAllByFetchDate() {
		return mapper.listAllByFetchDate();
	}

	public void updateStatistics() throws Exception {
		Connection conn = dataSource.getConnection();
		try {
			CallableStatement s = conn.prepareCall("{call GW_DATA_PORTAL.UPDATE_CACHE_META_DATA}");
			int uc = s.executeUpdate();
			logger.info("Updated cache meta data, result = {}", uc);
		} finally {
			conn.close();
		}
	}
	
	public void updateStatsForWell(WellRegistryKey well) throws Exception {
		Connection conn = dataSource.getConnection();
		try {
			CallableStatement s = conn.prepareCall("{call GW_DATA_PORTAL.UPDATE_CMD_FOR_WELL(?,?)}");
			s.setString(1, well.getAgencyCd());
			s.setString(2, well.getSiteNo());
			int uc = s.executeUpdate();
			logger.info("Updated cache meta data, result = {}", uc);
		} finally {
			conn.close();
		}		
	}
	
	public void updateCacheMetaData() {
		mapper.updateCacheMetaData();
	}

	public CacheMetaData get(WellRegistryKey well, WellDataType type) {
		CacheMetaDataKey key = new CacheMetaDataKey();
		key.setAgencyCd(well.getAgencyCd());
		key.setSiteNo(well.getSiteNo());
		key.setDataType(type.name());
		return mapper.selectByPrimaryKey(key);
	}
}
