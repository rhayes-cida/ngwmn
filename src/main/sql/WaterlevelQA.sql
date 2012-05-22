select 
	qc.waterlevel_cache_id,
	xq.*
from 
	waterlevel_cache qc,
	
XMLTable(
XMLNAMESPACES(
  'http://www.wron.net.au/waterml2' AS "wml2",
  'http://www.opengis.net/om/2.0' AS "om",
  'http://www.opengis.net/swe/2.0' AS "swe"),
'for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element
let 
$p := $r/wml2:TimeValuePair,
$t := $p/wml2:time, 
$q := $p/wml2:value/swe:Quantity,
$v := $q/swe:value,
$u := $q/swe:uom/@code
return 
<r>
<dt>{$t}</dt>
<val>{$v}</val>
<units>{$u}</units>
</r>
'
passing qc.xml
columns 
"DT" varchar(40) path 'dt',
"VAL" varchar(40) path 'val',
"UNITS" varchar(40) path 'units'
) xq;


-- ahah! don't need to construct intermediate XML fragment
select 
	qc.waterlevel_cache_id,
	qc.agency_cd,
	qc.site_no,
	xq.*
from 
	waterlevel_cache qc,
	
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
"VAL" varchar(40) path 'wml2:value/swe:Quantity/swe:value',
"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
) xq

where qc.site_no = '410155074060201';

-- ahah! don't need to construct intermediate XML fragment
select 
	wlc.waterlevel_cache_id,
	wlc.agency_cd,
	wlc.site_no,
	min(xq.DT) first,
	max(xq.DT) last,
	count(xq.VAL) ct
from 
	waterlevel_cache wlc,
	
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
passing wlc.xml
columns 
"DT" varchar(40) path 'wml2:time',
"VAL" varchar(40) path 'wml2:value/swe:Quantity/swe:value',
"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
) xq

group by wlc.waterlevel_cache_id;


create table waterlevel_data_quality as
select 
	qc.waterlevel_cache_id,
	qc.agency_cd,
	qc.site_no,
	min(xq.DT) first,
	max(xq.DT) last,
	count(xq.VAL) ct
from 
	waterlevel_cache qc,
	
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
"VAL" varchar(40) path 'wml2:value/swe:Quantity/swe:value',
"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
) xq

group by qc.waterlevel_cache_id, qc.agency_cd, qc.site_no;

-- single cache row
create table waterlevel_data_quality as

select 
	min(xq.DT) first,
	max(xq.DT) last,
	count(xq.VAL) ct
from 
	waterlevel_cache qc,
	
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
"VAL" varchar(40) path 'wml2:value/swe:Quantity/swe:value',
"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
) xq
where qc.waterlevel_cache_id = 110;

