<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="gov.usgs.ngwmn.dm.prefetch.PrefetchController"%>
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
<title>Ground Water Data Prefetch</title>
</head>
<body>

<h1><a href="/ngwmn">National Ground Water Monitoring Network</a></h1>
<h2>Data Cache Prefetch Controller</h2>

<%
org.springframework.context.ApplicationContext
ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());

PrefetchController controller = ctx.getBean(PrefetchController.class);
%>

<%
String action = request.getParameter("action");
if (action != null) {
	if ("start".equals(action)) {
		log("start");
		controller.start();
	} 
	else if ("stop".equals(action)) {
		log("stop");
		controller.stop();
	} 
	else if ("enable".equals(action)) {
		log("enable");
		controller.enable();
	} 
	else if ("startInParallel".equals(action)) {
		log("startInParallel");
		controller.startInParallel();
	} 
	else {
		log("unknown action: " + action);
	}
}

%>

Enabled: <%= controller.isEnabled() %>

<form action="" method="post">
<button type="submit" name="action" value="start">Start immediately</button>
<button type="submit" name="action" value="stop">Stop and disable</button>
<button type="submit" name="action" value="enable">Enable schedule</button>
<button type="submit" name="action" value="startInParallel">Start in parallel, immediately</button>

</form>

</body>
</html>