<%@page import="gov.usgs.ngwmn.dm.cache.Loader"%>
<%@page import="gov.usgs.ngwmn.dm.cache.Cache"%>
<%@page import="gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>NGWMN Cache</title>
	<script type="text/javascript" src="js/snoop.js">
	</script>
</head>
<body>

	<h1>National Ground Water Monitoring Network</h1>
	<h2>Data Cache</h2>
	<ul>
		<li><a href="wip/fetchlog/chart">Fetch Statistics</a></li>
		<li><a href="wip/fetchlog/timechart">Data Publication Timeline</a></li>
		<li><a href="wip/fetchlog/fetchdates">Most Recent Fetch Date</a></li>
		<li><a href="wells">Well List</a></li>
		<li> Well Prefetch by Agency
			<ul>
				<%
					org.springframework.context.ApplicationContext
					ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
					
					CacheMetaDataDAO
					dao = ctx.getBean("CacheMetaDataDAO", CacheMetaDataDAO.class);

				%>
				
				<c:forEach items="<%= dao.listAgencySummary() %>" var="agency">
					<li><a href="wells?servlet=prefetch&agencyID=${agency.agencyCd}&type=LOG&type=QUALITY&type=WATERLEVEL">Prefetch list for ${agency.agencyCd}</a></li>
				</c:forEach>			
			</ul>
		</li>
		<li> Well Prefetch by State(Not a complete list)
			<ul>
				<li><a href="wells?servlet=prefetch&state=27&type=LOG&type=QUALITY&type=WATERLEVEL">Prefetch list for Minnesota</a></li>
				<li><a href="wells?servlet=prefetch&state=30&type=LOG&type=QUALITY&type=WATERLEVEL">Prefetch list for Montana</a></li>
			</ul>
		</li>

		<li><a href="admin/cache.jsp">Cache Statistics</a></li>
		<li>
			<form id="siteDetail" method="get" action="admin/siteDetail.jsp">
				<input type="submit" value="site detail" name="siteDetail"/>
				<input type="text" name="agencyCd" value="agency code"/>
				<input type="text" name="siteNo" value="site #"/>
			</form>
		</li>
		<li>
			<form id="cacheSnoop" method="get" action="cache/">
				<input type="button" onclick="window.location.href = snoop(this.form, this.form.cacheType.value, this.form.cacheId.value);" value="snoop" name="snooper"></input>
				<select name="cacheType">
					<option value="LOG">LOG</option>
					<option value="LITHOLOGY">LITHOLOGY</option>
					<option value="CONSTRUCTION">CONSTRUCTION</option>
					<option value="WATERLEVEL">WATERLEVEL</option>
					<option value="QUALITY" selected="selected">QUALITY</option>
				</select>
				<input type="text" name="cacheId" value="cacheId" size="7"></input>
				
			</form>
	</ul>

</body>
</html>
