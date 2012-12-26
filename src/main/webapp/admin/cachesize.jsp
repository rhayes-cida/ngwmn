<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Cache Size</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <!--Load the AJAX API-->
	<script type="text/javascript"
		  src="http://cida.usgs.gov/js/dygraphs/2012_07_21_bc2d2/dygraph-dev.js"></script>
    
      
<title>Cache Size</title>
</head>
<body onload="draw_graph">

<h1>Cache Size</h1>

<div id="chart_div" style="width: 80%;">
Chart size graph
</div>
    <script type="text/javascript">
		var g;
		
		function draw_graph() {
            var div = document.getElementById("chart_div");
        	g = new Dygraph(
              div,
              "/ngwmn/stats/cache_size", 
              {
                connectSeparatedPoints: true
              }
            );
		}

        draw_graph();
        
      </script>

</body>
</html>