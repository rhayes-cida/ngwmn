package gov.usgs.ngwmn.dm.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheMetaDataDAO {
	private CacheMetaDataMapper mapper;
	private DataSource dataSource;
	
	private static Logger logger = LoggerFactory.getLogger(CacheMetaDataDAO.class);
	
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
}
