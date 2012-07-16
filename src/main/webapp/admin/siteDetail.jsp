<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="gov.usgs.ngwmn.dm.cache.Loader"%>
<%@page import="gov.usgs.ngwmn.dm.cache.Cache"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@page import="gov.usgs.ngwmn.dm.dao.FetchLogDAO"%>
<%@page import="gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO"%>
<%@page import="gov.usgs.ngwmn.dm.dao.FetchLog"%>
<%@page import="gov.usgs.ngwmn.dm.dao.CacheMetaData"%>
<%@page import="java.util.List"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>


<html xmlns:jsp="http://java.sun.com/JSP/Page">
<%
	String agencyCd = request.getParameter("agencyCd");
	String siteNo = request.getParameter("siteNo");
	
	ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
	FetchLogDAO dao = ctx.getBean("FetchLogDAO", FetchLogDAO.class);
	
	List<FetchLog> fLogs = dao.fetchHistory(agencyCd, siteNo);

%>

<head>
	<title>Site Detail <%= agencyCd %> <%= siteNo %></title>
	<style>
		.FAIL {color: red;}
		.DONE {color: green;}
	
	</style>
	<script type="text/javascript" src="../js/snoop.js"></script>
</head>
<body>
	<h2>Site Cache Detail for <%= agencyCd %> <%= siteNo %></h2>
	<h3>Cached </h3>
		<ul>
			<li>coming....</li>
		</ul>

	<h3>Prefetch History (<%= fLogs.size() %> items)</h3>
	<form id="cacheSnoop" method="get" action="../cache/">
		<table>
			<tr>
				<th>ID</th>
				<th>Datasource</th>
				<th>Type</th>
				<th>Started</th>
				<th>Status</th>
				<th>Problem</th>
				<th>Count</th>
				<th>Elapsed time (in seconds)</th>
				<th>Specifier</th>
				<th>Fetcher</th>
			</tr>
			<c:forEach items="<%= fLogs %>" var="fl">
				<tr class="${fl.status}">
					<td>${fl.fetchlogId}</td>
					<td>${fl.source}</td>
					<td>${fn:substringBefore(fn:substringAfter(fl.specifier,'typeID='),']')}</td>
					<td>${fl.startedAt}</td>
					<td>${fl.status}</td>
					<td>${fl.problem}</td>
					<td>${fl.ct}</td>
					<td>${fl.elapsedSec}</td>
					<td>${fl.specifier}</td>
					<td>${fl.fetcher}</td>
				</tr>
			</c:forEach>
		</table>
	</form>


</body>
</html>