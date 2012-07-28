<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Site selector</title>
</head>
<body>

<!-- 
For Ajax/JSON, see http://blog.springsource.org/2010/01/25/ajax-simplifications-in-spring-3-0/
 -->
Agency codes = ${agencyCodes}<br />

And, in jstl, site is ${siteSelector}.
<form:form commandName="siteSelector">
<table>
<tr>
<th>agency: </th><td><form:select path="agency" items="${agencyCodes}"/></td></tr>
<tr><th>site:</th><td> <form:input path="siteId" /></td></tr>
<tr><th>type:</th> <td>
<form:select path="dataType">
              <form:option value="-" label="--Please Select"/>
              <form:options items="${dataTypes}"/>
</form:select>
</td></tr>

<tr><td colspan="2"><input type="submit" value="Save Changes" /></td></tr>
</table>
</form:form>

</body>
</html>