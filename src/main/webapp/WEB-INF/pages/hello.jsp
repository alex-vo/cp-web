<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title>Spring Form Validation</title>
	<style type="text/css">
		label { width: 70px; display: inline-block; }
		input, textarea { width: 320px; }
		textarea { height: 100px; }
		.red { color: #f00; }
	</style>
</head>
<body>

<div id="successMessage"><c:out value="${successMessage}"/></div>
<form:form modelAttribute="loginForm" method="post" action="login">
    <form:label path="login">Login</form:label> : <form:input type="text" path="login" />
    <form:errors path="login" cssClass="red" /><br/>
    <form:label path="password">Password</form:label> : <form:input type="text" path="password" />
    <form:errors path="password" cssClass="red" /><br/>
    <button>Login</button><br/>
</form:form>

<a href="registerForm">Register</a>

</body>
</html>