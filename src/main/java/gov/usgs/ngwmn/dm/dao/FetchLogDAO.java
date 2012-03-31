package gov.usgs.ngwmn.dm.dao;

import java.util.List;

public class FetchLogDAO {
	private FetchLogMapper mapper;
	
	public FetchLogDAO(FetchLogMapper mapper) {
		super();
		this.mapper = mapper;
	}

	@Deprecated
	/** use #insertId instead
	 * @param item
	 */
	public void insert(FetchLog item) {
		mapper.insert(item);
	}
	
	public void insertId(FetchLog item) {
		mapper.insertId(item);
	}

	public List<FetchLog> byWell(WellRegistryKey well) {
		FetchLogExample selector = new FetchLogExample();
		selector.createCriteria()
			.andAgencyCdEqualTo(well.getAgencyCd())
			.andSiteNoEqualTo(well.getSiteNo());
		selector.setOrderByClause("started_at DESC");
		
		return mapper.selectByExample(selector);
	}
	
	public FetchLog select(Integer fetchlogId) {
		return mapper.selectByPrimaryKey(fetchlogId);
	}

	public int update(FetchLog record) {
		return mapper.updateByPrimaryKey(record);
	}
	
	
}
