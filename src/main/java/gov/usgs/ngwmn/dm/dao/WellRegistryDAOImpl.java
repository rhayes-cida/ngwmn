package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryExample;
import java.util.List;

public class WellRegistryDAOImpl implements WellRegistryDAO {

	private WellRegistryMapper mapper;

	public WellRegistryDAOImpl() {
		super();
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

	@SuppressWarnings("unchecked")
	public List<WellRegistry> selectByExample(WellRegistryExample example) {
		return mapper.selectByExample(example);
	}

	@Override
	public WellRegistry findByKey(String agencyID, String wellID) {
		WellRegistryKey key = new WellRegistryKey(agencyID, wellID);
		return mapper.selectByPrimaryKey(key);
	}
	
	@Override
	public List<WellRegistry> selectAll() {
		WellRegistryExample filter = new WellRegistryExample();
		// Only look for displayed wells
		filter.createCriteria().andDisplayFlagEqualTo("1");
		return selectByExample(filter);
	}

}