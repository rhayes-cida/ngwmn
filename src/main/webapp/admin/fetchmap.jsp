<!DOCTYPE html>
<html>
  <head>
  
<style type="text/css">
.loading {
	opacity: 0.5;
}
</style>

    <script type='text/javascript' src='https://www.google.com/jsapi'></script>
    <script type='text/javascript'>
     google.load('visualization', '1', {'packages': ['geochart']});
     google.setOnLoadCallback(drawMarkersMap);

     var errorDisplayID;
     var chart;
     var options = {
    	        region: 'US',
    	        displayMode: 'markers',
    	        colorAxis: {colors: ['red', 'green']},
    	        // sizeAxis: { minValue:1, maxValue: 1}
    	      };
     
     function useSample(qn) {
         var qelem = document.getElementById('q');
         
         var sourceElem = document.getElementById(qn);
         if (sourceElem && sourceElem.value) {
        	 qelem.value = sourceElem.value;
         }
     }
     
      function drawMarkersMap() {
      var data = google.visualization.arrayToDataTable([
                                                        ['lat',          'long'],
                                                        ]);


      chart = new google.visualization.GeoChart(document.getElementById('chart_div'));
      chart.draw(data, options);
      
      // and now launch the real query
      
      // let user launch it when they want... 
      // refresh();
      }
      
      var query;
      
      function refresh() {
    	  var chart_div = document.getElementById('chart_div')
    	  
    	  chart_div.className = 'loading';
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
	    	  chart_div.className = '';

		   });

		};
</script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;">
    
    </div>
    
        <form>
    <textarea id="q" hidden='true'  rows="20" cols="80">
    a geoquery goes here
    </textarea>
    
    <button onclick="useSample('rank-stats-query'); refresh(); return false;">Rank statistics</button>
    <button onclick="useSample('data-age-query'); refresh(); return false;">Data age</button>
    <button onclick="useSample('monthly-stats-query'); refresh(); return false;">Per-month rank statistics (for most recent sample date)</button>
    
    
    <textarea id="rank-stats-query" hidden="true">

select dec_lat_va, dec_long_va, 
cumulative_distribution,
count sample_count

from 
gw_data_portal.well_registry wr, 
GW_DATA_PORTAL.waterlevel_cache c,
GW_DATA_PORTAL.waterlevel_data_stats cs

where wr.agency_cd = c.agency_cd and wr.site_no = c.site_no
and c.waterlevel_cache_id = cs.waterlevel_cache_id
and published='Y'
    </textarea>
    
    <textarea id="data-age-query" hidden="true">
select dec_lat_va, dec_long_va, 
sysdate - cast(fetch_date as date) days_ago,
1 published


from 
gw_data_portal.well_registry wr, 
GW_DATA_PORTAL.waterlevel_cache c,
GW_DATA_PORTAL.waterlevel_data_stats cs

where wr.agency_cd = c.agency_cd and wr.site_no = c.site_no
and c.waterlevel_cache_id = cs.waterlevel_cache_id
and published='Y'
    </textarea>
    
    <textarea id="monthly-stats-query" hidden="true">
select dec_lat_va, dec_long_va, 
monthly_cum_distribution,
month


from 
gw_data_portal.well_registry wr, 
GW_DATA_PORTAL.waterlevel_cache c,
GW_DATA_PORTAL.waterlevel_data_stats cs

where wr.agency_cd = c.agency_cd and wr.site_no = c.site_no
and c.waterlevel_cache_id = cs.waterlevel_cache_id
and published='Y'
    </textarea>

    </form>
  </body>
</html>
