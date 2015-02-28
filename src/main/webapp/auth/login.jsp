<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href=<c:url value="/css/common.css"/>>
</head>

<body>
<div class="message warning">
    <div class="inset">
        <div class="message-head"><h1>Login Form</h1></div>

        <form id="loginForm" onsubmit="return submitLogin()">
            <p>
                <input type="text" name="login" placeholder="Username" onfocus="hideErrorBox()" autofocus/>
            </p>
            <p>
                <input type="password" name="password" placeholder="Password" onfocus="hideErrorBox()"/>
            </p>
            <div class="submit">
                <input type="submit" value="Sing In">
                <h4><a href=<c:url value="/chess/register"/>>Or register now</a></h4>
            </div>
            <div class="clear"></div>

            <div id="errorBox" class="errBox"></div>
        </form>
    </div>
</div>
</body>
<script type="text/javascript" src="<c:url value="/js/jquery-2.1.3.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/my-js.js"/>"></script>
</html>