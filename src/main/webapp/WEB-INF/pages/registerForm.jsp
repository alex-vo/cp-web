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

<form:form modelAttribute="registerForm" method="post" action="register">
    <form:label path="login">Login</form:label> : <form:input type="text" path="login" />
    <form:errors path="login" cssClass="red" /><br/>
    <form:label path="password">Password</form:label> : <form:input type="password" path="password" />
    <form:errors path="password" cssClass="red" /><br/>
    <form:label path="repeatPassword">Password</form:label> : <form:input type="password" path="repeatPassword" />
    <form:errors path="repeatPassword" cssClass="red" /><br/>
    <button>Register</button><br/>
</form:form>

</body>
</html>