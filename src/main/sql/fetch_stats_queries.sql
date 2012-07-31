select * from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS;

select agency_cd,trunc(fetch_date) fetched, coalesce(published, 'N') published, count(*) ct
from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS
-- where agency_cd = 'TWDB'
group by agency_cd,trunc(fetch_date), coalesce(published, 'N');


select to_date('2012-07-11','YYYY-MM-dd') from dual;


				select trunc(fetch_date) fetched,
				(select count(*) FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1
				 WHERE trunc(cs1.fetch_date) = trunc(cs.fetch_date)
				 AND cs1.published = 'Y') success,
				(select count(*) FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2
				 WHERE trunc(cs2.fetch_date) = trunc(cs.fetch_date)
				 AND cs2.published = 'N') failure
								 
				from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs;


				select trunc(fl.started_at) fetched,
				(select count(*) 
				FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1
				 WHERE trunc(cs1.fetch_date) = trunc(fl.started_at) 
				 AND cs1.published = 'Y') success,
				(select count(*) 
				FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2
				 WHERE trunc(cs2.fetch_date) = trunc(fl.started_at) 
				 AND cs2.published = 'N') failure,
				 count(*) trials
								 
				from GW_DATA_PORTAL.fetch_log fl
				where fl.data_source IS NOT NULL
				group BY trunc(fl.started_at)
				order by trunc(fl.started_at) ASC;
				

select 

trunc(fl.started_at) fetched, 
				
(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1
								 where trunc(cs1.fetch_date) = trunc(fl.started_at) 
								 and cs1.published = 'Y') success,
				
(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2
								 where trunc(cs2.fetch_date) = trunc(fl.started_at) 
								 and cs2.published = 'N') "empty",
count(*) attempts 

from GW_DATA_PORTAL.fetch_log fl
where fl.data_source is not null

								group by trunc(fl.started_at) 
				 order by trunc(fl.started_at) asc;
				 
				 
select 

trunc(fl.started_at) fetched, 
				
(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1
								 where trunc(cs1.fetch_date) = trunc(fl.started_at) 
								 and cs1.published = 'Y') success,
				
(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2
								 where trunc(cs2.fetch_date) = trunc(fl.started_at) 
								 and cs2.published = 'N') "empty",
								 
count(distinct fetch_log.fetchlog_id) fail_ct,

count(*) attempts 

from 
(select * from GW_DATA_PORTAL.fetch_log 
 where fetch_log.data_stream = 'WATERLEVEL'
 and fetcher = 'WebRetriever' ) fl
 
 left join GW_DATA_PORTAL.fetch_log 
 on (fetch_log.fetchlog_id = fl.fetchlog_id and fetch_log.status = 'FAIL')
 
group by trunc(fl.started_at)
order by trunc(fl.started_at) asc;
				 
				 
				 
select 

trunc(fl.started_at) fetched, 
				
(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1
								 where trunc(cs1.fetch_date) = trunc(fl.started_at) 
								 and agency_cd = 'USGS'
								 and cs1.published = 'Y') success,
				
(select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2
								 where trunc(cs2.fetch_date) = trunc(fl.started_at) 
								 and agency_cd = 'USGS'
								 and cs2.published = 'N') "empty",
								 
count(distinct fetch_log.fetchlog_id) fail_ct,

count(*) attempts 

from 
(select * from GW_DATA_PORTAL.fetch_log 
 where data_stream = 'WATERLEVEL'
 and agency_cd = 'USGS'
 and fetcher = 'WebRetriever' ) fl
 
 left join GW_DATA_PORTAL.fetch_log fail_log
 on (fail_log.fetchlog_id = fl.fetchlog_id and fail_log.status = 'FAIL')
 
group by trunc(fl.started_at)
order by trunc(fl.started_at) asc;
				 
				 
				 
				 