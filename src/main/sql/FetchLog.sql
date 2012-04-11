-- drop table fetch_log;

create table FETCH_LOG (
	fetchlog_id integer not null,
	AGENCY_CD              VARCHAR2(20 BYTE)      NOT NULL,
    SITE_NO                VARCHAR2(16 BYTE)      NOT NULL,

	data_source varchar2(120),
	started_at timestamp,
	status char(4),
	problem varchar2(80),
	ct integer,
	elapsed_sec real,
	specifier varchar2(80),
	fetcher varchar2(80),
	primary key(fetchlog_id)
);


create sequence fetch_log_seq;

create trigger fetch_log_trigger
	before insert on FETCH_LOG
	for each row
	begin
		select coalesce(:new.fetchlog_id,fetch_log_seq.nextval) into :new.fetchlog_id from dual;
	end;

ALTER TABLE "GW_DATA_PORTAL"."FETCH_LOG" 
ADD ( FOREIGN KEY ("AGENCY_CD", "SITE_NO") 
	REFERENCES "GW_DATA_PORTAL"."WELL_REGISTRY" ("AGENCY_CD", "SITE_NO") VALIDATE )

-- insert into fetch_log(status,agency_cd,site_no) values ('PASS','USGS','009');
-- select * from fetch_log;
-- delete from fetch_log;

alter table gw_data_portal.FETCH_LOG
add DATA_STREAM varchar2(20) ;



