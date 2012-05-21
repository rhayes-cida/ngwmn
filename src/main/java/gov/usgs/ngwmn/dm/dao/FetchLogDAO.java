package gov.usgs.ngwmn.dm.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchLogDAO {
	private FetchLogMapper mapper;
	
	public FetchLogDAO(FetchLogMapper mapper) {
		super();
		this.mapper = mapper;
	}

	public void insertId(FetchLog item) {
		mapper.insertId(item);
	}

	public FetchLog mostRecent(WellRegistryKey well) {	
		return mapper.selectLatestByWell(well.getAgencyCd(), well.getSiteNo());
	}
	
	public List<FetchLog> byWell(WellRegistryKey well) {
		FetchLogExample selector = new FetchLogExample();
		selector.createCriteria()
			.andAgencyCdEqualTo(well.getAgencyCd())
			.andSiteNoEqualTo(well.getSiteNo());
		// newest first
		selector.setOrderByClause("started_at DESC");
		
		return mapper.selectByExample(selector);
	}
	
	public FetchLog select(Integer fetchlogId) {
		return mapper.selectByPrimaryKey(fetchlogId);
	}

	public int update(FetchLog record) {
		return mapper.updateByPrimaryKey(record);
	}

	public List<Map<String, Object>> statisticsByDay(Date day) {
		List<Map<String,Object>> v = mapper.statisticsByDay(day);
		return v;
	}

}
