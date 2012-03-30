-- drop table fetch_log;

create table FETCH_LOG (
	fetchlog_id integer not null,
	data_source varchar2(120),
	started_at timestamp,
	status char(4),
	problem varchar2(80),
	ct integer,
	elapsed_sec real,
	specifier varchar2(80),
	primary key(fetchlog_id)
);