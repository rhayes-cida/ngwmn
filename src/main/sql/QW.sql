create table gw_data_portal.qw (
qw_id integer primary key,
agency_cd varchar2(20) not null,
site_no varchar2(16) not null,

fetch_date timestamp not null,
xml XMLType -- store as binary xml
,

foreign key (agency_cd,site_no) references gw_data_portal.well_registry(agency_cd,site_no)
);

create sequence qw_seq;

create trigger qw_insert_trigger
before insert on gw_data_portal.qw
for each row
begin
  select coalesce(:new.qw_id, qw_seq.nextval) into :new.qw_id from dual;
  select coalesce(:new.fetch_date, current_date) into :new.fetch_date from dual;
end;

