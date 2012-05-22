<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="gov.usgs.ngwmn.dm.dao.FetchLogDAO"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
th { text-align: left; }
</style>
<title>Ground Water Data Cache Statistics</title>
</head>
<body>

<h1><a href="/ngwmn">National Ground Water Monitoring Network</a></h1>
<h2>Data Cache</h2>

<%
org.springframework.context.ApplicationContext
ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());

FetchLogDAO
dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

String day_s = request.getParameter("day");
Date day;
if (day_s == null) {
	day = new Date();
} else {
	day = sdf.parse(day_s);
}
day_s = sdf.format(day);
%>

<h2>Cache Statistics</h2>

<form method="get">
<input type="text" name="day" value="<%= day_s %>"/>
<input type="submit"/>

<table>
<tr>
	<th>Agency Code</th>
	<th>Data Type</th>
	<th>Status</th>
	<th>Count</th>
	<th>Average fetch time</th>
</tr>
<c:forEach  var="r" items="<%= dao.statisticsByDay(day) %>">
<tr>
	<td><c:out value="${r.AGENCY_CD}"/></td>
	<td><c:out value="${r.DATA_STREAM}"/></td>
	<td><c:out value="${r.STATUS}"/></td>
	<td><c:out value="${r.CT}"/></td>
	<td><c:out value="${r.AVG}"/></td>
</tr>
</c:forEach>
</table>

</form>

</body>
</html>