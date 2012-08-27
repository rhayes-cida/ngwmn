SELECT agency_cd, site_no, 
(select count(*) ct
FROM GW_DATA_PORTAL.LOG_CACHE c
WHERE published = 'Y' AND xml IS NOT NULL and c.agency_cd = r.agency_cd and c.site_no = r.site_no) log_ct,
(select count(*) ct
FROM GW_DATA_PORTAL.fetch_log f
WHERE data_source IS NOT NULL and f.agency_cd = r.agency_cd and f.site_no = r.site_no and f.data_stream='LOG') log_tries,

(select count(*) ct
FROM GW_DATA_PORTAL.QUALITY_CACHE c
WHERE published = 'Y' AND xml IS NOT NULL and c.agency_cd = r.agency_cd and c.site_no = r.site_no) quality_ct,
(select count(*) ct
FROM GW_DATA_PORTAL.fetch_log f
WHERE data_source IS NOT NULL and f.agency_cd = r.agency_cd and f.site_no = r.site_no and f.data_stream='QUALITY') quality_tries,

(select count(*) ct
FROM GW_DATA_PORTAL.WATERLEVEL_CACHE c
WHERE published = 'Y' AND xml IS NOT NULL and c.agency_cd = r.agency_cd and c.site_no = r.site_no) wl_ct,
(select count(*) ct
FROM GW_DATA_PORTAL.fetch_log f
WHERE data_source IS NOT NULL and f.agency_cd = r.agency_cd and f.site_no = r.site_no and f.data_stream='WATERLEVEL') wl_tries

from GW_DATA_PORTAL.well_registry r;
