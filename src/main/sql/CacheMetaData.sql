drop table CACHE_META_DATA;

create table CACHE_META_DATA (
	AGENCY_CD              VARCHAR2(20 BYTE)      NOT NULL,
    SITE_NO                VARCHAR2(16 BYTE)      NOT NULL,

    DATA_TYPE				VARCHAR2(24 BYTE)	DEFAULT 'ALL' NOT NULL,
    
    SUCCESS_CT				INTEGER,
    FAIL_CT					INTEGER,
    FIRST_DATA_DT			DATE,
    LAST_DATA_DT			DATE,
    MOST_RECENT_FETCH_DT	DATE,
    
	PRIMARY KEY (AGENCY_CD, SITE_NO, DATA_TYPE),
	FOREIGN KEY (AGENCY_CD, SITE_NO) 
	REFERENCES GW_DATA_PORTAL.WELL_REGISTRY (AGENCY_CD, SITE_NO) 
);

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



