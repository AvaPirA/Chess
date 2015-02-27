<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Register</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">
</head>

<body>
    <div>
        <form action="<c:url value="/chess/register"/>" method="post">
            <input name="login" value="Login" autofocus><br>
            <c:if test="${duplicateLogin}"><div class="error">Login already in use. Choose another.</div></c:if>
            <c:if test="${emptyLogin}"><div class="error">Login can not be empty.</div></c:if>
            <c:if test="${spacesInLogin}"><div class="error">Login can not contain space characters.</div></c:if>
            <input type="password" name="password" value="Password"><br>
            <c:if test="${shortPassword}"><div class="error">Password must have at least 3 symbols.</div></c:if>
            <input type="submit" value="Sing Up">
            <input type="hidden" name="action" value="register">
        </form>
    </div>
    <c:if test="${not empty fail and not fail}">
        You've been successfully registered! Now you may go <a href="<c:url value="/chess/start"/>">further</a>
    </c:if>
</body>

</html>