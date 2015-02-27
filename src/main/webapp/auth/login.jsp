<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/common.css"/>">
</head>

<body>
<c:if test="${fail}">
    <div class="error">Wrong login or password. Check your input.</div>
</c:if>

<div class="error">Wrong login or password. Check your input.</div>
<form action="<c:url value="/chess/login"/>" method="post">
    <input name="login" value="Login" autofocus><br>
    <input type="password" name="password" value="Password"><br>
    <input type="hidden" name="action" value="signin">
    <input type="submit" value="Sing In">
</form>
<c:if test="${not empty fail and not fail}">Nice! You have been logged in now!</c:if>
<form action="<c:url value="/chess/login"/>" method="post">Not registered?<input type="submit" value="Sign Up"><input
        type="hidden" name="action" value="signup"></form>
</body>
</html>