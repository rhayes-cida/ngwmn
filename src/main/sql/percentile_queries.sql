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
;


select 
	
    qc.waterlevel_cache_id,
    
    cume_dist()
    over (
    	partition by qc.waterlevel_cache_id
      	order by xq.depth
    ) cumulative_distribution,
    
    percent_rank() 
	over (
    	partition by qc.waterlevel_cache_id
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
;


-- has promise of being much more efficient!


select * from 
(select 
	
    qc.waterlevel_cache_id,
    xq.position,
    xq.depth,
    xq.sz,
    xq.ord,
    
    cume_dist()
    over (
    	partition by qc.waterlevel_cache_id
      	order by xq.depth
    ) cumulative_distribution,
    
    percent_rank() 
	over (
    	partition by qc.waterlevel_cache_id
      	order by xq.depth
    ) percent_rank
    
	from 
		gw_data_portal.waterlevel_cache qc,
	
		XMLTable(
		XMLNAMESPACES(
		  'http://www.wron.net.au/waterml2' AS "wml2",
		  'http://www.opengis.net/om/2.0' AS "om",
		  'http://www.opengis.net/swe/2.0' AS "swe"),
		  
		'
    let $seq := //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element
    for $r at $pos in $seq

		let 
		$p := $r/wml2:TimeValuePair,
    $depth := $p/wml2:value/swe:Quantity/swe:value,
    $sz := count($seq)
    
		return
     <well>
				<depth>{$depth}</depth>
				<pos>{$pos}</pos>
        <sz>{$sz}</sz>
        <ord>{$sz - $pos}</ord>
 			</well>
		'
		  
		passing qc.xml
		columns 
		"DEPTH" number path 'depth',
    "POSITION" number path 'pos',
    "SZ" number path 'sz',
    "ORD" number path 'ord'
		) xq

WHERE QC.WATERLEVEL_CACHE_ID in (220, 42)
-- and ord = 0
order by position desc)

where ord = 0
;

-- Here's a start at a per-month rank

select 
	
    qc.waterlevel_cache_id,
    xq.depth,
    xq.ord,
    xq.dt,
    xq.month,
    
    cume_dist()
    over (
    	partition by qc.waterlevel_cache_id,xq.month
      	order by xq.depth
    ) cumulative_distribution,
    
    percent_rank() 
	over (
    	partition by qc.waterlevel_cache_id,xq.month
      	order by xq.depth
    ) percent_rank
    
	from 
		gw_data_portal.waterlevel_cache qc,
	
		XMLTable(
		XMLNAMESPACES(
		  'http://www.wron.net.au/waterml2' AS "wml2",
		  'http://www.opengis.net/om/2.0' AS "om",
		  'http://www.opengis.net/swe/2.0' AS "swe"),
		  
		'
    let $seq := //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element,
    	$sz := count($seq)
    
    for $r at $pos in $seq

		let 
		$p := $r/wml2:TimeValuePair,
    	$depth := $p/wml2:value/swe:Quantity/swe:value,
      	$dt := substring($p/wml2:time,1,10),
      	$month := month-from-date(xs:date($dt))
    
	 return
     <well>
     	<dt>{$dt}</dt>
     	<month>{$month}</month>
		<depth>{$depth}</depth>
		<pos>{$pos}</pos>
        <sz>{$sz}</sz>
        <ord>{$sz - $pos}</ord>
 	</well>
		'
		  
		passing qc.xml
		columns 
		"DT" date path 'dt',
    	"MONTH" number path 'month',
		"DEPTH" number path 'depth',
    	"POSITION" number path 'pos',
    	"SZ" number path 'sz',
    	"ORD" number path 'ord'
		) xq
    
    where waterlevel_cache_id = 220
;
