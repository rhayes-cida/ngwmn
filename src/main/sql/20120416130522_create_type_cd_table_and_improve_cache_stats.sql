--// create type_cd table and improve cache stats
-- Migration SQL that makes the change goes here.

create table DATA_TYPE_CD (
       DATA_STREAM varchar2(20) PRIMARY KEY,
       DESCRIPTION varchar2(256)
);

insert into DATA_TYPE_CD(DATA_STREAM,DESCRIPTION) VALUES ('QUALITY', 'Water quality data');
insert into DATA_TYPE_CD(DATA_STREAM,DESCRIPTION) VALUES ('LOG', 'Well log information (construction and lithology');
insert into DATA_TYPE_CD(DATA_STREAM,DESCRIPTION) VALUES ('WATERLEVEL', 'Water level data');
insert into DATA_TYPE_CD(DATA_STREAM,DESCRIPTION) VALUES ('ALL', 'Post-aggregation spreadsheet data stream');

--//@UNDO
-- SQL to undo the change goes here.

-- drop table DATA_TYPE_CD;
