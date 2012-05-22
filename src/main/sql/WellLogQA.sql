
select 
	qc.log_cache_id,
	xq.*
from 
	log_cache qc,
	
XMLTable(
XMLNAMESPACES(
	'http://www.opengis.net/sampling/1.0' AS "sa",
	'http://www.w3.org/1999/xlink' as "xlink",
	'http://www.nrcan.gc.ca/xml/gwml/1' as "gwml",
	'http://www.opengis.net/wfs' as "wfs",
	'urn:cgi:xmlns:CGI:GeoSciML:2.0' as "gsml",
	'http://www.opengis.net/gml' as "gml",
  'http://www.wron.net.au/waterml2' AS "wml2",
  'http://www.opengis.net/om/2.0' AS "om",
  'http://www.opengis.net/swe/2.0' AS "swe"),
  
'for $w in /wfs:FeatureCollection/gml:featureMember/gwml:WaterWell
let 
 $latlong := $w/sa:position/gml:Point/gml:pos,
 $depth := $w/gwml:wellDepth/gsml:CGI_NumericValue/gsml:principalValue,
 $lithCount := count($w/gwml:logElement),
 $conCount := count($w/gwml:construction)

return 
<well>
  <latlong>{$latlong}</latlong>
  <depth>{$depth}</depth>
  <lith-count>{$lithCount}</lith-count>
  <con-count>{$conCount}</con-count>
</well>
'
passing qc.xml
columns 
"LATLONG" varchar(40) path 'latlong',
"DEPTH" varchar(40) path 'depth',
"LITH_COUNT" varchar(40) path 'lith-count',
"CON_COUNT" varchar(40) path 'con-count'
) xq;


-- try using a union operator
-- FAIL | operator only works for nodes, count returns integer
select 
	qc.log_cache_id,
	xq.*
from 
	log_cache qc,
	
XMLTable(
XMLNAMESPACES(
	'http://www.opengis.net/sampling/1.0' AS "sa",
	'http://www.w3.org/1999/xlink' as "xlink",
	'http://www.nrcan.gc.ca/xml/gwml/1' as "gwml",
	'http://www.opengis.net/wfs' as "wfs",
	'urn:cgi:xmlns:CGI:GeoSciML:2.0' as "gsml",
	'http://www.opengis.net/gml' as "gml",
  'http://www.wron.net.au/waterml2' AS "wml2",
  'http://www.opengis.net/om/2.0' AS "om",
  'http://www.opengis.net/swe/2.0' AS "swe"),
  
'for $w in /wfs:FeatureCollection/gml:featureMember/gwml:WaterWell
let 
 $latlong := $w/sa:position/gml:Point/gml:pos,
 $depth := $w/gwml:wellDepth/gsml:CGI_NumericValue/gsml:principalValue,
 $lithCount := count($w/gwml:logElement),
 $conCount := count($w/gwml:construction),
	$seq := ($latlong | $depth | $lithCount | $conCount)
return $seq
'
passing qc.xml
columns 
"SEQ" varchar(80) path '.'
) xq;


-- this one is good
select 
	qc.log_cache_id,
	xq.*
from 
	log_cache qc,
	
XMLTable(
XMLNAMESPACES(
	'http://www.opengis.net/sampling/1.0' AS "sa",
	'http://www.w3.org/1999/xlink' as "xlink",
	'http://www.nrcan.gc.ca/xml/gwml/1' as "gwml",
	'http://www.opengis.net/wfs' as "wfs",
	'urn:cgi:xmlns:CGI:GeoSciML:2.0' as "gsml",
	'http://www.opengis.net/gml' as "gml",
  	'http://www.wron.net.au/waterml2' AS "wml2",
  	'http://www.opengis.net/om/2.0' AS "om",
  	'http://www.opengis.net/swe/2.0' AS "swe"),
  
'for $w in /wfs:FeatureCollection/gml:featureMember/gwml:WaterWell
let 
 $latlong := $w/sa:position/gml:Point/gml:pos,
 $ll_split := ora:tokenize($latlong, ''\s+''),
 $lat := $ll_split[1],
 $long := $ll_split[2],
 $depth := $w/gwml:wellDepth/gsml:CGI_NumericValue/gsml:principalValue,
 $lithCount := count($w/gwml:logElement),
 $conCount := count($w/gwml:construction)

return 
 <well>
	<lat>{$lat}</lat>
	<long>{$long}</long>
 	<depth>{$depth}</depth>
 	<lith-count>{$lithCount}</lith-count>
 	<con-count>{$conCount}</con-count>
 </well>
'
passing qc.xml
columns 
"LATITUDE" float path 'lat',
"LONGITUDE" float path 'long',
"DEPTH" float path 'depth',
"LITH_COUNT" int path 'lith-count',
"CON_COUNT" int path 'con-count'
) xq;
