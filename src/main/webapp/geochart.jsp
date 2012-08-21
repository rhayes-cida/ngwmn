<!DOCTYPE html>
<html>
  <head>
    <script type='text/javascript' src='https://www.google.com/jsapi'></script>
    <script type='text/javascript'>
     google.load('visualization', '1', {'packages': ['geochart']});
     google.setOnLoadCallback(drawMarkersMap);

     var errorDisplayID;
     var chart;
     var options = {
    	        region: 'US',
    	        displayMode: 'markers',
    	        colorAxis: {colors: ['green', 'blue']},
    	        sizeAxis: { minValue:1, maxValue: 1}
    	      };

     var sampleQueries = ['select dec_lat_va, dec_long_va from gw_data_portal.well_registry',
						'select dec_lat_va, dec_long_va, '+
                          '(select sum(success_ct) '+
                          'from gw_data_portal.cache_meta_data ' + 
                          'where well_registry.agency_cd = cache_meta_data.agency_cd '+
                          'and well_registry.site_no = cache_meta_data.site_no) sum_success '+
                         'from gw_data_portal.well_registry'
			];
     
     function useSample(qn) {
         var qelem = document.getElementById('q');
         
         var sourceElem = document.getElementById(qn);
         if (sourceElem && sourceElem.value) {
        	 qelem.value = sourceElem.value;
         } else {
	         qelem.value = sampleQueries[qn];
         }
     }
     
      function drawMarkersMap() {
      var data = google.visualization.arrayToDataTable([
                                                        ['lat',          'long'],
                                                        [39.28175, -98.59500]
                                                        ]);


      chart = new google.visualization.GeoChart(document.getElementById('chart_div'));
      chart.draw(data, options);
      
      // and now launch the real query
      
      // let user launch it when they want... 
      // refresh();
      }
      
      var query;
      
      function refresh() {
	      var queryOpts = {};
	      
	      var sqlQuery = document.getElementById('q').value;
	      var url = '/ngwmn/stats/data?query=' + encodeURIComponent(sqlQuery);
	      
	      query && query.abort();
	      
	      query = new google.visualization.Query(url, queryOpts);
	
	      //optionally set google visualization query here using query.setQuery
	      
	      query.send(function(response) {
	
			if (response.isError()) {
				google.visualization.errors.addErrorFromQueryResponse(document.getElementById('chart_div'),response);
				return;
			}
	
			var data = response.getDataTable();
			chart.draw(data, options);
		   });

		};
</script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
    
    
    <form>
    Query:<br />
    <textarea id="q"   rows="20" cols="80">
select dec_lat_va, dec_long_va from gw_data_portal.well_registry
    </textarea>
    <br />
    <button onclick="refresh();return false;">refresh</button>
    <button onclick="useSample(0); return false;">sample 1</button>
    <button onclick="useSample(1); return false;">sample 2</button>
    <button onclick="useSample('sample-query-3'); return false;">sample 3 (gnarly)</button>
    
    <textarea id="sample-query-3" hidden="true">
    select 
	max(wr.dec_lat_va) latitude, 
    max(wr.dec_long_va) longitude,
	max(xq.DEPTH) maxDepth, 
    avg(xq.DEPTH) avgDepth
    -- , count(distinct qc.waterlevel_cache_id) cache_ct
    
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
		"UNITS" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'
		) xq
where qc.agency_cd = wr.agency_cd and qc.site_no = wr.site_no
and qc.published = 'Y'
group by wr.agency_cd, wr.site_no
    </textarea>
    </form>
  </body>
</html>
