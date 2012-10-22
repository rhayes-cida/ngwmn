package gov.usgs.ngwmn.dm.prefetch;

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class WaterlevelRankStatsWorker {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private DataSource ds;
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private int backoff = 0;

	public Observation latestObservation(int cacheId) {
		String query = 
				"select * from \n" + 
				"(\n" + 
				"\n" + 
				"select \n" + 
				"	\n" + 
				"    qc.waterlevel_cache_id,\n" + 
				"    xq.dt,\n" + 
				"    xq.timetext,\n" + 
				"    xq.depth,\n" + 
				"    xq.month\n" + 
				"    \n" + 
				"    \n" + 
				"    \n" + 
				"	from \n" + 
				"		gw_data_portal.waterlevel_cache qc,\n" + 
				"	\n" + 
				"		XMLTable(\n" + 
				"		XMLNAMESPACES(\n" + 
				"		  'http://www.wron.net.au/waterml2' AS \"wml2\",\n" + 
				"		  'http://www.opengis.net/om/2.0' AS \"om\",\n" + 
				"		  'http://www.opengis.net/swe/2.0' AS \"swe\"),\n" + 
				"		  \n" + 
				"		'\n" + 
				"    \n" + 
				"    for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element\n" + 
				"		let \n" + 
				"      $p := $r/wml2:TimeValuePair,\n" + 
				"    	$depth := $p/wml2:value/swe:Quantity/swe:value,\n" + 
				"      $fulldate := $p/wml2:time,\n" + 
				"      	$dt := substring($p/wml2:time,1,10),\n" + 
				"      	$dtclean := if ($dt castable as xs:date)\n" + 
				"      				then $dt\n" + 
				"      				else null,\n" + 
				"      	$month := month-from-date(xs:date($dtclean))\n" + 
				"    where exists($depth) and exists($dt) and exists($fulldate)\n" + 
				"      \n" + 
				"	 return\n" + 
				"     <well>\n" + 
				"      <timetext>{$fulldate}</timetext>\n" + 
				"     	<dt>{$dtclean}</dt>\n" + 
				"     	<month>{$month}</month>\n" + 
				"      <depth>{$depth}</depth>\n" + 
				"    </well>\n" + 
				"		'\n" + 
				"		  \n" + 
				"		passing qc.xml\n" + 
				"		columns \n" + 
				"      \"DT\" date path 'dt',\n" + 
				"      \"TIMETEXT\" varchar(40) path 'timetext',\n" + 
				"    	\"MONTH\" number path 'month',\n" + 
				"      \"DEPTH\" number path 'depth'\n" + 
				"		) xq\n" + 
				"    \n" + 
				"    where qc.waterlevel_cache_id = :cacheId \n" + 
				"    \n" + 
				"    order by dt desc nulls last, TIMETEXT desc nulls last\n" + 
				"    )\n" + 
				"where rownum = 1";
		
	    Map<String, ?> parameters = Collections.singletonMap("cacheId", cacheId);

	    BeanPropertyRowMapper<Observation> bprm = new BeanPropertyRowMapper<Observation>(Observation.class);
	    try {
	    	Observation value = jdbcTemplate.queryForObject(query, parameters, bprm);
	    	value.setCacheId(cacheId);

	    	return value;
	    } catch (EmptyResultDataAccessException ERDAE) {
	    	logger.warn("No good data for {}", cacheId);
	    	return null;
	    } catch (TypeMismatchException tme) {
	    	logger.warn("Type mismatch for " + cacheId, tme);
	    	return null;
	    }
	}
	
	public Statistics monthly_stats(Observation obs) {
		String query = "select \n" + 
				"    cume_dist(:depth)\n" + 
				"    within group (\n" + 
				"      	order by xq.depth\n" + 
				"    ) cum_distribution,\n" + 
				"    \n" + 
				"    percent_rank(:depth) \n" + 
				"    within group (\n" + 
				"      	order by xq.depth\n" + 
				"    ) percent_rank,\n" + 
				"    \n" + 
				"    count(*)\n" + 
				"     count,\n" + 
				"    \n" + 
				"    rank(:depth)\n" + 
				"    within group (\n" + 
				"      	order by xq.depth\n" + 
				"    ) rank,\n" + 
				"    \n" + 
				"    min(xq.dt)\n" + 
				"      min_date,\n" + 
				"    \n" + 
				"    max(xq.dt)\n" + 
				"      max_date,\n" + 
				"      \n" + 
				"      min(xq.depth) min_depth,\n" + 
				"      max(xq.depth) max_depth\n" + 
				"    \n" + 
				"    \n" + 
				"	from \n" + 
				"		gw_data_portal.waterlevel_cache qc,\n" + 
				"	\n" + 
				// beginning of XQuery XMLTable
				"	XMLTable(\n" + 
				"	   XMLNAMESPACES(\n" + 
				"		  'http://www.wron.net.au/waterml2' AS \"wml2\",\n" + 
				"		  'http://www.opengis.net/om/2.0' AS \"om\",\n" + 
				"		  'http://www.opengis.net/swe/2.0' AS \"swe\"),\n" + 
				"		  \n" + 
				"		'   " + // start of XQuery expression
				"\n" + 
				"    for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element\n" + 
				"\n" + 
				"	 let \n" + 
				"       $p := $r/wml2:TimeValuePair,\n" + 
				"    	$depth := $p/wml2:value/swe:Quantity/swe:value,\n" + 
				"      	$dt := substring($p/wml2:time,1,10),\n" + 
				"      	$dtclean := if ($dt castable as xs:date)\n" + 
				"      				then $dt\n" + 
				"      				else null,\n" + 
				"      	$month := month-from-date(xs:date($dtclean))\n" + 
				"    where $month = $mn \n" + 
				"	 return\n" + 
				"      <well>\n" + 
				"        <dt>{$dtclean}</dt>\n" + 
				"     	 <month>{$month}</month>\n" + 
				"        <depth>{$depth}</depth>\n" + 
				" 	   </well>\n" + 
				"		'    " + // end of XQuery expression
				"		  \n" + 
				"		passing qc.xml, :month as \"mn\" \n" + 
				"		columns \n" + 
				"       \"DT\" date path 'dt',\n" + 
				"    	\"MONTH\" number path 'month',\n" + 
				"       \"DEPTH\" number path 'depth'\n" + 
				"		) xq\n" + 
				// end of XQuery XMLTable 
				"    where qc.waterlevel_cache_id = :cacheId " +
				"";
		
		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(obs);
	    BeanPropertyRowMapper<Statistics> bprm = new BeanPropertyRowMapper<Statistics>(Statistics.class);

		Statistics value = jdbcTemplate.queryForObject(query, namedParameters, bprm);
		value.setWaterlevel_cache_id(obs.getCacheId());
		
		return value;
	}
	
	public Statistics total_stats(Observation obs) {
		String query = "select \n" + 
				"	\n" + 
				"    cume_dist(:depth)\n" + 
				"    within group (\n" + 
				"      	order by xq.depth\n" + 
				"    ) cum_distribution,\n" + 
				"    \n" + 
				"    percent_rank(:depth) \n" + 
				"    within group (\n" + 
				"      	order by xq.depth\n" + 
				"    ) percent_rank,\n" + 
				"    \n" + 
				"    count(*)\n" + 
				"     count,\n" + 
				"    \n" + 
				"    rank(:depth)\n" + 
				"    within group (\n" + 
				"      	order by xq.depth\n" + 
				"    ) rank,\n" + 
				"    \n" + 
				"    min(xq.dt) min_date,\n" + 
				"    max(xq.dt) max_date,\n" + 
				"    min(xq.depth) min_depth,\n" + 
				"    max(xq.depth) max_depth\n" + 
				"    \n" + 
				"    \n" + 
				"	from \n" + 
				"		gw_data_portal.waterlevel_cache qc,\n" + 
				"	\n" + 
				"		XMLTable(\n" + 
				"		XMLNAMESPACES(\n" + 
				"		  'http://www.wron.net.au/waterml2' AS \"wml2\",\n" + 
				"		  'http://www.opengis.net/om/2.0' AS \"om\",\n" + 
				"		  'http://www.opengis.net/swe/2.0' AS \"swe\"),\n" + 
				"		  \n" + 
				"		'\n" + 
				"    for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element\n" + 
				"\n" + 
				"		let \n" + 
				"      $p := $r/wml2:TimeValuePair,\n" + 
				"    	$depth := $p/wml2:value/swe:Quantity/swe:value,\n" + 
				"      	$dt := substring($p/wml2:time,1,10),\n" + 
				"      	$dtclean := if ($dt castable as xs:date)\n" + 
				"      				then $dt\n" + 
				"      				else null,\n" + 
				"      	$month := month-from-date(xs:date($dtclean))\n" + 
				"	 return\n" + 
				"     <well>\n" + 
				"     	<dt>{$dtclean}</dt>\n" + 
				"     	<month>{$month}</month>\n" + 
				"      <depth>{$depth}</depth>\n" + 
				" 	</well>\n" + 
				"		'\n" + 
				"		  \n" + 
				"		passing qc.xml\n" + 
				"		columns \n" + 
				"      \"DT\" date path 'dt',\n" + 
				"    	\"MONTH\" number path 'month',\n" + 
				"      \"DEPTH\" number path 'depth'\n" + 
				"		) xq\n" + 
				"    where qc.waterlevel_cache_id = :cacheId \n" + 
				"    \n" + 
				"";
		
		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(obs);
	    BeanPropertyRowMapper<Statistics> bprm = new BeanPropertyRowMapper<Statistics>(Statistics.class);

		Statistics value = jdbcTemplate.queryForObject(query, namedParameters, bprm);
		value.setWaterlevel_cache_id(obs.getCacheId());
		return value;
	}
	
	public int updateOne() {
		if (backoff > 0) {
			backoff--;
			logger.trace("backing off ct = {}", backoff);
			return -2;
		}
		resetBackoff();
				
		if (logger.isDebugEnabled()) {
			int count = count();
			logger.debug("count of unranked published observations {}", count);
		}

		// get one waterlevel sample that needs to be updated
		final Integer id = findOneSample();
		if (id != null && id > 0) {
		    Map<String, ?> parameters = Collections.singletonMap("cacheId", id);

			// insert new stats record, ok to fail if it's already there
		    try {
				jdbcTemplate.update(
						"insert into gw_data_portal.waterlevel_data_stats( " + 
						"	waterlevel_cache_id) " +
						"values ( " +
						"	:cacheId " +
						")", parameters);
				logger.debug("inserted record for cacheId {}", id);
		    } catch (DuplicateKeyException dke) {
		    	logger.info("waterlevel data stats already exists for {}, will update", id);
		    }

			Observation latest = latestObservation(id);
			logger.debug("got latest observation: {}", latest);
			if (latest == null) {
				logger.debug("latest is null f id {}", id);
				return -3;
			}
			
			SqlParameterSource latestParameters = new BeanPropertySqlParameterSource(latest);
			jdbcTemplate.update(
					"update gw_data_portal.waterlevel_data_stats " + 
					"set " +
					"depth = :depth, " +
					"dt = :dt, " +
					"month = :month " +
					"where waterlevel_cache_id = :cacheId ", latestParameters);
			logger.debug("updated latest stats for {} month {}", id, latest.getMonth());
			
			// update monthly stats
			Statistics monthly = monthly_stats(latest);
			SqlParameterSource monthlyParameters = new BeanPropertySqlParameterSource(monthly);
			jdbcTemplate.update(
					"update gw_data_portal.waterlevel_data_stats " +
					"set " +
					"monthly_cum_distribution = :cum_distribution, " +
					"monthly_percent_rank = :percent_rank, " +
					"monthly_count = :count, " +
					"monthly_rank =  :rank, " +
					"monthly_min_date = :min_date, " +
					"monthly_max_date = :max_date " +
					"where waterlevel_cache_id = :waterlevel_cache_id ",
					monthlyParameters);
			logger.debug("updated monthy stats for {} month {}", monthly.getWaterlevel_cache_id(), latest.getMonth());
			
			Statistics total = total_stats(latest);
			SqlParameterSource totalParameters = new BeanPropertySqlParameterSource(total);
			jdbcTemplate.update(
					"update gw_data_portal.waterlevel_data_stats " +
					"set " +
					"cumulative_distribution = :cum_distribution, " +
					"percent_rank = :percent_rank, " +
					"count = :count, " +
					"rank =  :rank, " +
					"min_date = :min_date, " +
					"max_date = :max_date " +
					"where waterlevel_cache_id = :waterlevel_cache_id ",
					totalParameters);
			logger.debug("updated total stats for {} to {}", total.getWaterlevel_cache_id(), total);

			return id;
		} else {
			// nothing to do. Stall for a while.
			backoff = nextBackoff();
			logger.info("Nothing to do, will skip {} tries", backoff);
			return -1;
		}
	}
	
	private static final int MIN_BACKOFF = 10;
	private static final int MAX_BACKOFF = 100;
	private int currentBackoff = MIN_BACKOFF;
	
	private int nextBackoff() {
		currentBackoff *= 2;
		if (currentBackoff > MAX_BACKOFF) {
			currentBackoff = MAX_BACKOFF;
		} else if (currentBackoff < MIN_BACKOFF) {
			// safety check
			currentBackoff = MIN_BACKOFF;
		}
		return currentBackoff;
	}
	
	private void resetBackoff() {
		currentBackoff = MIN_BACKOFF;
	}

	public Integer findOneSample() {
		String latestSampleQuery = " " +
				"SELECT max(waterlevel_cache_id) " +
				"FROM gw_data_portal.waterlevel_cache_stats " +
				"WHERE published = 'Y' " +
				"AND sample_ct > 0 " +
				"AND NOT EXISTS ( " +
				"  SELECT * FROM gw_data_portal.waterlevel_data_stats ds " +
				"  WHERE ds.waterlevel_cache_id = waterlevel_cache_stats.waterlevel_cache_id " +
				"  ) ";
		
		try {
			int val = jdbcTemplate.getJdbcOperations().queryForInt(latestSampleQuery);
			if (val == 0) {
				// This is how queryForInt indicates an empty result
				logger.info("No unstatted samples case 1");
				return null;
			}
			logger.debug("found an un-statted sample, id = {}", val);
			return val;
		} catch (EmptyResultDataAccessException nothingToDo) {
			logger.info("No unstatted samples case 2");
			return null;
		}
	}

	public int count() {
		JdbcTemplate template = new JdbcTemplate(ds);
		
		String countq = " " +
				"SELECT count(*) " +
				"FROM gw_data_portal.waterlevel_cache_stats " +
				"WHERE published = 'Y' " +
				"AND sample_ct > 0 " +
				"AND NOT EXISTS ( " +
				"  SELECT * FROM gw_data_portal.waterlevel_data_stats ds " +
				"  WHERE ds.waterlevel_cache_id = waterlevel_cache_stats.waterlevel_cache_id " +
				"  ) ";

		final int count = template.queryForInt(countq);
		
		return count;
	}
	
	public void setDataSource(DataSource ds) {
		this.ds = ds;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
	}

}
