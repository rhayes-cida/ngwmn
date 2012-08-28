select 
	
    qc.waterlevel_cache_id,
    
    cume_dist(
    (select max(wll.depth) 
      from gw_data_portal.waterlevel_latest_view wll
      where wll.waterlevel_cache_id = qc.waterlevel_cache_id)
    )
    within group (
      order by xq.depth
    ) cumulative_distribution,
    
    percent_rank(
    (select max(wll.depth) 
      from gw_data_portal.waterlevel_latest_view wll
      where wll.waterlevel_cache_id = qc.waterlevel_cache_id)
    )
    within group (
      order by xq.depth
    ) percent_rank
    
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
		"DEPTH" number path 'wml2:value/swe:Quantity/swe:value'
		) xq
    
where  qc.agency_cd='TWDB' and qc.site_no='1449806'

group by qc.waterlevel_cache_id;


select 
	
    qc.waterlevel_cache_id,
    xq.*
    
	from 
		gw_data_portal.waterlevel_cache qc,
		gw_data_portal.well_registry wr,
	
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
		"DEPTH" number path 'wml2:value/swe:Quantity/swe:value',
		"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code',
    "LAST_DEPTH" number path 'wml2:value/swe:Quantity/swe:value[last()]'
		) xq
where qc.agency_cd = wr.agency_cd and qc.site_no = wr.site_no
 and wr.agency_cd='TWDB' and wr.site_no='1449806'

and qc.published = 'Y';



select 
	qc.waterlevel_cache_id,
    xq.*
    
	from 
		gw_data_portal.waterlevel_cache qc,
	
		XMLTable(
		XMLNAMESPACES(
		  'http://www.wron.net.au/waterml2' AS "wml2",
		  'http://www.opengis.net/om/2.0' AS "om",
		  'http://www.opengis.net/swe/2.0' AS "swe"),
		  
		'for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element[last()]

		let 
		$p := $r/wml2:TimeValuePair
		
		return $p
		'
		  
		passing qc.xml
		columns 
    "DT" varchar2(12) path 'ora:replace(substring(wml2:time,1,10),"-00","-01")',
		"DEPTH" number path 'wml2:value/swe:Quantity/swe:value',
		"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
		) xq
  
  where qc.published = 'Y'
-- where qc.agency_cd='TWDB' and qc.site_no='1449806';
;


select * from
GW_DATA_PORTAL.waterlevel_cache,
GW_DATA_PORTAL.waterlevel_latest_view
where waterlevel_cache.waterlevel_cache_id = gw_data_portal.waterlevel_latest_view.waterlevel_cache_id
and waterlevel_cache.agency_cd='TWDB' and waterlevel_cache.site_no='1449806';
;

select * from gw_data_portal.waterlevel_percentile_view;


select * from
GW_DATA_PORTAL.waterlevel_cache,
GW_DATA_PORTAL.waterlevel_percentile_view
where waterlevel_cache.waterlevel_cache_id = waterlevel_percentile_view.waterlevel_cache_id
and waterlevel_cache.agency_cd='TWDB' and waterlevel_cache.site_no='1449806';
;

select 
	  qc.md5,
	  min(f_date(xq.DT)) first,
	  max(f_date(xq.DT)) last,
	  count(xq.VAL) ct
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
		"DT" varchar2(12) path 'ora:replace(substring(wml2:time,1,10),"-00","-01")',
		"VAL" number path 'wml2:value/swe:Quantity/swe:value',
		"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
		) xq