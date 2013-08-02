<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="gov.usgs.ngwmn.dm.cache.Loader"%>
<%@page import="gov.usgs.ngwmn.dm.cache.Cache"%>
<%@page import="gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<style type="text/css">
		th { text-align: left; }
	</style>
	<title>Ground Water Data Cache</title>
</head>
<body>

	<h1><a href="/ngwmn_cache">National Ground Water Monitoring Network</a></h1>
	<h2>Data Cache</h2>
	<p>
		(<a href="cachestats.jsp">Daily data fetch statistics</a>)
	</p>
	
	<h2>Prefetch</h2>
	<a href="prefetch.jsp">control</a>
	
	<%
		org.springframework.context.ApplicationContext
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		
		CacheMetaDataDAO
		dao = ctx.getBean("CacheMetaDataDAO", CacheMetaDataDAO.class);
		
		Loader loader = ctx.getBean("Loader", Loader.class);
	%>
	
	<h2>Caches:</h2>

	<table>
		<tr><th>Data type</th><th>Cache</th></tr>
		<c:forEach  var="cmi" items="<%= loader.getCacheMap().entrySet() %>">
			<tr><td><c:out value="${cmi.key}"/></td><td><c:out value="${cmi.value}"/></td></tr>
		</c:forEach>
	</table>

	<h2>Fetch record:</h2>
	
	<table>
		<tr>
			<th>Agency<br/>Code</th>
			<th>Site #</th>
			<th>Data Type</th>
			<th>Success<br/>Count</th>
			<th>Fail<br/>Count</th>
			<th>Last Attempt</th>
		</tr>
		
		<%
			dao.updateStatistics();
		%>
		<c:forEach items="<%= dao.listAllByFetchDate() %>" var="cmd">
			<tr>
				<td>${cmd.agencyCd}</td>
				<td>${cmd.siteNo}</td>
				<td>${cmd.dataType }</td>
				<td>${cmd.successCt}</td>
				<td>${cmd.failCt}</td>
				<td>${cmd.mostRecentAttemptDt}</td>
			</tr>
		
		</c:forEach>
	</table>
</body>
</html>