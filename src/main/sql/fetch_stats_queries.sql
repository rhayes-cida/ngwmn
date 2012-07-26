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
				(select count(*) FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs1
				 WHERE trunc(cs1.fetch_date) = trunc(fl.started_at) 
				 AND cs1.published = 'Y') success,
				(select count(*) FROM GW_DATA_PORTAL.WATERLEVEL_CACHE_STATS cs2
				 WHERE trunc(cs2.fetch_date) = trunc(fl.started_at) 
				 AND cs2.published = 'N') failure,
				 count(*) trials
								 
				from GW_DATA_PORTAL.fetch_log fl
				where fl.data_source IS NOT NULL
				group BY trunc(fl.started_at)
				order by trunc(fl.started_at) ASC;
				

