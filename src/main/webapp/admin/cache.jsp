<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Ground Water Data Cache</title>
</head>
<body>

<h1>National Ground Water Monitoring Network</h1>
<h2>Data Cache</h2>

<%
org.springframework.context.ApplicationContext
ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());

CacheMetaDataDAO
dao = ctx.getBean("CacheMetaDataDAO", CacheMetaDataDAO.class);
%>

<p>
Storage: FileCache<br />
Base directory: <%= ctx.getBean("FSCache.basedir") %><br />
</p>

<table>
<tr>
<th>Agency Code</th>
<th>Site #</th>
<th>Data Type</th>
<th>Success Count</th>
<th>Fail Count</th>
<th>Last Success</th>
</tr>

<%
dao.updateStatistics();
%>
<c:forEach items="<%= dao.listAll() %>" var="cmd">
<tr>
<td>${cmd.agencyCd}</td>
<td>${cmd.siteNo}</td>
<td>${cmd.dataType }</td>
<td>${cmd.successCt}</td>
<td>${cmd.failCt}</td>
<td>${cmd.mostRecentFetchDt}</td>
</tr>

</c:forEach>
</table>
</body>
</html>