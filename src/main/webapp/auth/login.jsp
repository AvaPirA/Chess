<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">
</head>

<body>
    <div>
        <c:if test="${fail}">
            <div class="error">
                Wrong login or password. Check your input.
            </div>
        </c:if>
        <form action="/login" method="post">
            <input name="login" value="Login" autofocus><br/>
            <input type="password" name="password" value="Password"><br/>
            <input type="hidden" name="action" value="signin">
            <input type="submit" value="Sing In">
        </form>
    </div>
    <c:if test="${not empty fail and not fail}">
        <div>
            Nice! You have been logged in now!
        </div>
    </c:if>
    <div>
        Not registered?
        <form action="/login" method="post">
            <input type="submit" value="Sign Up">
            <input type="hidden" name="action" value="signup">
        </form>
    </div>
</body>
</html>