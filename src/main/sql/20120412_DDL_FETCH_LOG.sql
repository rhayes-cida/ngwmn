use gw_data_portal;

alter table gw_data_portal.FETCH_LOG
add DATA_STREAM varchar2(20) ;

update FETCH_LOG
set data_stream='QUALITY' where data_stream is null and specifier like '%typeID=QUALITY%';

update FETCH_LOG
set data_stream='ALL' where data_stream is null and specifier like '%typeID=ALL%';

update FETCH_LOG
set data_stream='WATERLEVEL' where data_stream is null and specifier like '%typeID=WATERLEVEL%';

update FETCH_LOG
set data_stream='LOG' where data_stream is null and specifier like '%typeID=LOG%';

create or replace procedure UPDATE_CACHE_META_DATA
as
begin
	
	delete from CACHE_META_DATA;

	insert into CACHE_META_DATA(agency_cd, site_no, data_type, success_ct, fail_ct, most_recent_fetch_dt)
	(select distinct
		agency_cd, 
		site_no, 
		data_stream,
		(select count(*) from fetch_log f2 
		 where f2.agency_cd = f1.agency_cd and f2.site_no = f1.site_no and f2.data_stream = f1.data_stream and f2.status = 'DONE') success_ct,
		(select count(*) from fetch_log f2 
		 where f2.agency_cd = f1.agency_cd and f2.site_no = f1.site_no and f2.data_stream = f1.data_stream and f2.status = 'FAIL') fail_ct,
		(select max(started_at) from fetch_log f2 
		 where f2.agency_cd = f1.agency_cd and f2.site_no = f1.site_no and f2.data_stream = f1.data_stream and f2.status = 'DONE') last_start
	from fetch_log f1);

end UPDATE_CACHE_META_DATA;

