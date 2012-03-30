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

create sequence fetch_log_seq;

create trigger fetch_log_trigger
	before insert on FETCH_LOG
	for each row
	begin
		select fetch_log_seq.nextval into :new.fetchlog_id from dual;
	end;

-- insert into fetch_log(status) values ('FAIL');
-- select * from fetch_log;
-- delete from fetch_log;



