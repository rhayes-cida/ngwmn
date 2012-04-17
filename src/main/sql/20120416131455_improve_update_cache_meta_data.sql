--// improve update_cache_meta_data
-- Migration SQL that makes the change goes here.

alter table CACHE_META_DATA
modify (
       most_recent_fetch_dt timestamp
);

create or replace
procedure UPDATE_CACHE_META_DATA
as
begin
	
	delete from CACHE_META_DATA;

	insert into CACHE_META_DATA(agency_cd, site_no, data_type, success_ct, fail_ct, most_recent_fetch_dt)
	(select distinct
		wr.agency_cd, 
		wr.site_no, 
		dtc.data_stream,
		(select count(*) from fetch_log f1 
		 where f1.agency_cd = wr.agency_cd and f1.site_no = wr.site_no and f1.data_stream = dtc.data_stream 
     		 and f1.status = 'DONE' and f1.data_source is not null) success_ct,
		(select count(*) from fetch_log f2 
		 where f2.agency_cd = wr.agency_cd and f2.site_no = wr.site_no and f2.data_stream = dtc.data_stream 
     		 and f2.status = 'FAIL' and f2.data_source is not null) fail_ct,
		(select max(started_at) from fetch_log f3 
		 where f3.agency_cd = wr.agency_cd and f3.site_no = wr.site_no and f3.data_stream = dtc.data_stream 
     		 and f3.status = 'DONE' and f3.data_source is not null) last_start
	from well_registry wr, data_type_cd dtc
	where wr.display_flag = '1'
	);

end UPDATE_CACHE_META_DATA;

--//@UNDO
-- SQL to undo the change goes here.

-- no need to undo, the changes are upward compatible
