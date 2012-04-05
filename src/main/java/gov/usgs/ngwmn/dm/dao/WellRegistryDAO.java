package gov.usgs.ngwmn.dm.dao;

import java.util.List;

public class WellRegistryDAO {

	private WellRegistryMapper mapper;

	public WellRegistryDAO() {
	}

	public WellRegistryMapper getMapper() {
		return mapper;
	}

	public void setMapper(WellRegistryMapper mapper) {
		this.mapper = mapper;
	}

	public int countByExample(WellRegistryExample example) {
		Integer count = mapper.countByExample(example);
		return count;
	}

	public List<WellRegistry> selectByExample(WellRegistryExample example) {
		return mapper.selectByExample(example);
	}

	public WellRegistry findByKey(String agencyID, String wellID) {
		WellRegistryKey key = new WellRegistryKey(agencyID, wellID);
		return mapper.selectByPrimaryKey(key);
	}
	
	public List<WellRegistry> selectAll() {
		WellRegistryExample filter = new WellRegistryExample();
		// Only look for displayed wells
		filter.createCriteria().andDisplayFlagEqualTo("1");
		filter.setOrderByClause("AGENCY_CD, SITE_NO");
		return selectByExample(filter);
	}

	public List<WellRegistry> selectByAgency(String agencyId) {
		return mapper.selectByAgency(agencyId);
	}

	public List<WellRegistry> selectByState(int fips) {
		WellRegistryExample filter = new WellRegistryExample();
		// Only look for displayed wells
		filter.createCriteria()
			.andDisplayFlagEqualTo("1")
			.andStateCdEqualTo(fips);
		filter.setOrderByClause("AGENCY_CD, SITE_NO");
		return selectByExample(filter);
	}
	
}