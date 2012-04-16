package gov.usgs.ngwmn.dm.dao;

import java.util.List;

public class CacheMetaDataDAO {
	private CacheMetaDataMapper mapper;
	
	public CacheMetaDataDAO(CacheMetaDataMapper mapper) {
		super();
		this.mapper = mapper;
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
	
}
