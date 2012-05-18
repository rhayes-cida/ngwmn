select qc.quality_cache_id, xq.*
from quality_cache qc,
XMLTable('//time' passing qc.xml
columns "t" varchar(40) path '.') as xq;


-- namespace http://qwwebservices.usgs.gov/schemas/WQX-Outbound/2_0/
select qc.quality_cache_id,
-- xq.*, 
-- xq.CN, min(xq.DT), max(xq.DT), count(xq.CN)
min(xq.DT), max(xq.DT), count(xq.DT), xq.CN
from quality_cache qc,
XMLTable(
'/*:Results/Result'
passing qc.xml
columns "CN" varchar(40) path '*:ResultDescription/*:CharacteristicName',
"DT" date path 'date',
"VAL" number path '*:ResultDescription/ResultMeasure/ResultMeasureValue'
) as xq
group by qc.quality_cache_id, xq.CN
;

select qc.quality_cache_id,
xq.* 
-- ,xq.CN, min(xq.DT), max(xq.DT), count(xq.CN)
-- ,min(xq.DT), max(xq.DT), count(xq.DT), xq.CN
from quality_cache qc,
XMLTable(
'/*:Results/Result'
passing qc.xml
columns "CN" varchar(40) path '*:ResultDescription/*:CharacteristicName',
"DT" date path 'date',
"VAL" number path '*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue',
"UNITS" varchar(40) path '*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode'
) as xq
-- group by qc.quality_cache_id, xq.CN
;

select qc.quality_cache_id,
xq.* 
-- ,xq.CN, min(xq.DT), max(xq.DT), count(xq.CN)
-- ,min(xq.DT), max(xq.DT), count(xq.DT), xq.CN
from quality_cache qc,
XMLTable(
'/*:Results/Result'
passing qc.xml
columns "CN" varchar(40) path '*:ResultDescription/*:CharacteristicName',
"DT" date path 'date',
"VAL" number path '*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue',
"UNITS" varchar(40) path '*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode'
) as xq
-- group by qc.quality_cache_id, xq.CN
;


select qc.quality_cache_id,
xq.* 
-- ,xq.CN, min(xq.DT), max(xq.DT), count(xq.CN)
-- ,min(xq.DT), max(xq.DT), count(xq.DT), xq.CN
from quality_cache qc,
XMLTable(
'for $r in /*:Results/Result return $r '
passing qc.xml
columns 
"CN" varchar(40) path '*:ResultDescription/*:CharacteristicName',
"DT" date path 'date',
"VAL" number path '*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue',
"UNITS" varchar(40) path '*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode'
) as xq
-- group by qc.quality_cache_id, xq.CN
;

select qc.quality_cache_id,
xq.* 
from quality_cache qc,
XMLTable(
'for $r in /*:Results/Result 
let 
$d := $r/date, 
$t := $r/time, 
$dt := fn:concat($d, ''T'', $t),
$fdt := xs:dateTime(fn:concat($d, ''T'', $t))[normalize-space($r/time)],
$pdt := xs:dateTime(fn:concat($d, ''T00:00:00''))[not(normalize-space($r/time))],
$ndt := ($fdt,$pdt)[1]
return 
<r>
<d>{$d}</d>
<t>{$t}</t>
<dt>{$dt}</dt>
<ndt>{$ndt}</ndt>
</r>
'
passing qc.xml
columns "DT" date path 'd',
"TM" varchar(12) path 't',
"DTTM" varchar(40) path 'dt',
"NDT" varchar(40) path 'ndt'
) as xq;

select qc.quality_cache_id,
xq.* 
from quality_cache qc,
XMLTable(
'for $r in /*:Results/Result 
let 
$d := $r/date, 
$t := $r/time, 
$fdt := xs:dateTime(fn:concat($d, ''T'', $t))[normalize-space($r/time)],
$pdt := xs:dateTime(fn:concat($d, ''T00:00:00''))[not(normalize-space($r/time))],
$ndt := ($fdt,$pdt)[1]
return 
<r>
<cn>{$r/*:ResultDescription/*:CharacteristicName}</cn>
<val>{$r/*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue}</val>
<units>{$r/*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode}</units>
<ndt>{$ndt}</ndt>
</r>
'
passing qc.xml
columns 
"CN" varchar(40) path 'cn',
"DT" varchar(40) path 'ndt',
"VAL" varchar(40) path 'val',
"UNITS" varchar(40) path 'units'
) as xq;


-- time zone conversion
select 
timestamp '1986-05-06 12:34:55 CST' t1,
timestamp '1986-05-06 12:34:55 UTC' t2,
cast((timestamp '1986-05-06 12:34:55 CST' at local) as timestamp) t1c,
cast((timestamp '1986-05-06 12:34:55 UTC' at local) as timestamp) t2c,
sys_extract_utc(timestamp '1986-05-06 12:34:55 CST') t1u,
sys_extract_utc(timestamp '1986-05-06 12:34:55 UTC') t1u,
sys_extract_utc(timestamp '1986-05-06 12:34:55') t3
from dual;

select 
 sys_extract_utc(to_timestamp_tz('1986-05-06 12:34:55 CST', 'YYYY-MM-DD HH:MI:SS TZR')) t1
,sys_extract_utc(to_timestamp_tz('1989-08-17 17:25:00 EST', 'YYYY-MM-DD HH24:MI:SS TZR')) t2
from dual;



-- decent solution but does not use timezoen info from data
select qc.quality_cache_id,
xq.cn, xq.val, xq.units,
sys_extract_utc(xq.dt)

from quality_cache qc,
XMLTable(
'for $r in /*:Results/Result 
let 
$d := $r/date, 
$t := $r/time, 
$ndt := if ($t) then xs:dateTime(fn:concat($d, ''T'', $t)) else xs:dateTime(fn:concat($d, ''T00:00:00''))
return 
<r>
<cn>{$r/*:ResultDescription/*:CharacteristicName}</cn>
<val>{$r/*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue}</val>
<units>{$r/*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode}</units>
<ndt>{$ndt}</ndt>
</r>
'
passing qc.xml
columns 
"CN" varchar(40) path 'cn',
"DT" timestamp with time zone path 'ndt',
"VAL" number path 'val',
"UNITS" varchar(40) path 'units'
) as xq;

-- use string concat and let Oracle do timezone conversion (because it understands timezones like CDT, which are non-XML)
select qc.quality_cache_id,
xq.cn, xq.val, xq.units
-- ,sys_extract_utc(to_timestamp_tz(xq.dt,'YYYY-MM-DD HH24:MI:SS TZR')) ndt
,xq.dt
from quality_cache qc,
XMLTable(
'for $r in /*:Results/Result 
let 
$d := $r/date, 
$t := $r/time, 
$z := $r/zone,
$space := '' '',
$dtz := normalize-space(if ($t) then fn:concat($d, $space, $t, $space,  $z) else $d)
return 
<r>
<cn>{$r/*:ResultDescription/*:CharacteristicName}</cn>
<val>{$r/*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue}</val>
<units>{$r/*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode}</units>
<dtz>{$dtz}</dtz>
</r>
'
passing qc.xml
columns 
"CN" varchar(40) path 'cn',
"DT" varchar(99) path 'dtz',
"VAL" number path 'val',
"UNITS" varchar(40) path 'units'
) as xq;

-- how to splat your namespace
select
qc.quality_cache_id
from quality_cache qc
where
XMLExists('//*:CharacteristicName'
passing qc.xml
);

-- so the empty cache ids are:
select
qc.quality_cache_id
from quality_cache qc
where
not XMLExists('//*:CharacteristicName'
passing qc.xml
);

-- have to figure out how to unpack the results of these queries...
select qc.quality_cache_id,
XMLQuery(
'for $cn in //*:CharacteristicName return "count($cn)"'
passing qc.xml
returning content
) cn_ct
from quality_cache qc;

select qc.quality_cache_id,
XMLQuery(
'count(//*:CharacteristicName)'
passing qc.xml
returning content
) cn_ct
from quality_cache qc;

-- aggregate in SQL
select 
	qc.quality_cache_id,
	count(distinct (xq.cn)), 
	min(xq.dt),
	max(xq.dt)
from 
	quality_cache qc,
	
XMLTable(
'for $r in /*:Results/Result 
return 
<r>
<cn>{$r/*:ResultDescription/*:CharacteristicName}</cn>
<dt>{$r/date}</dt>
</r>
'
passing qc.xml
columns 
"CN" varchar(80) path 'cn',
"DT" date path 'dt'
) xq

group by quality_cache_id;

-- per-constituent query
select 
	qc.quality_cache_id,
	xq.cn,
	count(distinct (xq.dt)), 
	min(xq.dt),
	max(xq.dt)
from 
	quality_cache qc,
	
XMLTable(
'for $r in /*:Results/Result 
return 
<r>
<cn>{$r/*:ResultDescription/*:CharacteristicName}</cn>
<dt>{$r/date}</dt>
</r>
'
passing qc.xml
columns 
"CN" varchar(80) path 'cn',
"DT" date path 'dt'
) xq

group by quality_cache_id, xq.cn;

