package gov.usgs.ngwmn.dm.dao;

import java.util.List;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

public class WellRegistryDAO implements ApplicationContextAware {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private WellRegistryMapper mapper;
	private DataSource dataSource;
	private static ApplicationContext ctx;
	
	public WellRegistryDAO() {
	}

	public WellRegistryMapper getMapper() {
		return mapper;
	}

	public void setMapper(WellRegistryMapper mapper) {
		this.mapper = mapper;
	}

	public void setDatasource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int countByExample(WellRegistryExample example) {
		Integer count = mapper.countByExample(example);
		return count;
	}

	public List<WellRegistry> selectByExample(WellRegistryExample example) {
		return mapper.selectByExample(example);
	}

	public WellRegistry findByKey(WellRegistryKey key) {
		return mapper.selectByPrimaryKey(key);
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
		WellRegistryExample filter = new WellRegistryExample();
		// Only look for displayed wells
		filter.createCriteria()
			.andDisplayFlagEqualTo("1")
			.andAgencyCdEqualTo(agencyId);
		filter.setOrderByClause("SITE_NO");
		return selectByExample(filter);
	}

	public List<WellRegistry> selectByState(String fips) {
		WellRegistryExample filter = new WellRegistryExample();
		// Only look for displayed wells
		filter.createCriteria()
			.andDisplayFlagEqualTo("1")
			.andStateCdEqualTo(fips);
		filter.setOrderByClause("AGENCY_CD, SITE_NO");
		return selectByExample(filter);
	}
	
	public List<String> agencies() {
		String query = 
				"select distinct agency_cd "+
				"from gw_data_portal.well_registry " +
				"order by agency_cd ASC ";
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		
		List<String> v = template.queryForList(query,String.class);
		
		return v;
	}
	
	public static WellRegistryDAO getInstance() {
		if (ctx != null) {
			return ctx.getBean(WellRegistryDAO.class);
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;
		
		String[] app = applicationContext.getEnvironment().getActiveProfiles();
		logger.info("Spring active profiles: {}", StringUtils.join(app, ","));
	}
}