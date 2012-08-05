<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Latest Fetch Dates</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7; IE=EmulateIE9"></meta> 
	<script type="text/javascript"
		  src="http://cida.usgs.gov/js/dygraphs/2012_07_21_bc2d2/dygraph-dev.js"></script>
    
    <script type="text/javascript">
    
      // Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['table']});

      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(doQuery);

      function doQuery() {
    	  var query = new google.visualization.Query(
    			  "age"
    			  );
    	  
    	  query.send(handleQueryResponse);
    	  
      }
      
      function handleQueryResponse(response) {
    	    if (response.isError()) {
    	      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    	      return;
    	    }

    	    var data = response.getDataTable();
    	    
    	    var table = new google.visualization.Table(document.getElementById('stats_table'));
            table.draw(data, {showRowNumber: false});
            
    	  }
            
      </script>
      
<title>Fetch Dates</title>
</head>
<body>

<h1>Most recent successful fetch of water level data</h1>

<div id="stats_table"></div>
</body>
</html>