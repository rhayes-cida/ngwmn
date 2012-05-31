select count(*) from GW_DATA_PORTAL.WATERLEVEL_CACHE;

select * from GW_DATA_PORTAL.WATERLEVEL_DATA_QUALITY;

select * from GW_DATA_PORTAL.WATERLEVEL_CACHE;

select * from
GW_DATA_PORTAL.WATERLEVEL_CACHE left join GW_DATA_PORTAL.WATERLEVEL_DATA_QUALITY
on WATERLEVEL_CACHE.md5 = WATERLEVEL_DATA_QUALITY.md5;

	select 
	  qc.waterlevel_cache_id,
	  xq.*
	from 
		gw_data_portal.waterlevel_cache qc,
	
		XMLTable(
		XMLNAMESPACES(
		  'http://www.wron.net.au/waterml2' AS "wml2"
		),
		  
		'for $r in //wml2:time

		return $r
		'
		  
		passing qc.xml
		columns 
		"TIM" varchar(40) path '.'
		) xq
		where waterlevel_cache_id > 207
		;
		
select 
	  qc.waterlevel_cache_id,
	  xq.TZ
	from 
		gw_data_portal.waterlevel_cache qc,
	
		XMLTable(
		XMLNAMESPACES(
		  'http://www.wron.net.au/waterml2' AS "wml2",
		  'http://www.opengis.net/om/2.0' AS "om",
		  'http://www.opengis.net/swe/2.0' AS "swe"),
		  
		'for $r in //*:TimeZone | //*:timeZone

		return $r
		'
		  
		passing qc.xml
		columns 
		"TZ" varchar(40) path '.'
		) xq;
		
create or replace
function date_kind(dt in varchar2) return varchar2
deterministic
is 
tt varchar(20);
begin

-- 1994-07-21T14:00:00-07:00
select case
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-00$') then 'zero-day'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T00:00:00\.000$') then 'datetime_zeroms'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}[+-]\d{2}:\d{2}$') then 'datetime_millisecond_tz'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}$') then 'datetime_millisecond'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T00:00:00[+-]\d{2}:\d{2}$') then 'datetime_zerosec_tz'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}[+-]\d{2}:\d{2}$') then 'datetime_second_tz'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$') then 'datetime_second'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}[+-]\d{2}:\d{2}$') then 'datetime_minute_tz'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$') then 'datetime_minute'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}$') then 'month'
when REGEXP_LIKE(dt, '^\d{4}-\d{2}-\d{2}$') then 'date'
else '?'
end
into tt
from dual;

return tt;

end;

create table waterlevel_data as
select 
	qc.waterlevel_cache_id,
	xq.DT,
	xq.VAL,
    xq.units
from 
	gw_data_portal.waterlevel_cache qc,
	
	XMLTable(
		XMLNAMESPACES(
		  'http://www.wron.net.au/waterml2' AS "wml2",
		  'http://www.opengis.net/om/2.0' AS "om",
		  'http://www.opengis.net/swe/2.0' AS "swe"),
		  
		'for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element

		let 
		$p := $r/wml2:TimeValuePair
		
		return $p
		'
		  
		passing qc.xml
		columns 
		"DT" varchar(40) path 'wml2:time',
		"VAL" number path 'wml2:value/swe:Quantity/swe:value',
		"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
	) xq;

select date_kind(dt), count(*)
from waterlevel_data
group by date_kind(dt);

