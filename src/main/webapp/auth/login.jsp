<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href=<c:url value="/css/common.css"/>>
    <script src="<c:url value="/js/jquery-2.1.3.min.js"/>"></script>
    <script type="text/javascript">
        function showErrorBox(error) {
            document.getElementById("errorBox").innerHTML = error;
            $(".errBox").addClass("errBox-change");
        }
        function hideErrorBox() {
            $(".errBox").removeClass("errBox-change");
        }
        function submitform() {
            var inputs = document.forms["loginform"].elements;
            var login = inputs.namedItem("login").value;
            var password = inputs.namedItem("password").value;
            if (login.length > 0) {
                if (password.length > 2) {
                        $.post("<c:url value="/chess/login"/>", {login: login, password: password, action: "signin"},
                                function (data) {
                                    if (data == "OK") {
                                        window.location.replace("<c:url value="/chess/start"/>");
                                        return true;
                                    } else if (data == "FAIL") {
                                        showErrorBox("Wrong login or password");
                                        return false;
                                    } else {
                                        showErrorBox(data);
                                    }
                                    return false;
                                });
                    return false;
                } else {
                    showErrorBox("Wrong password");
                }
            } else {
                showErrorBox("Type in login");
            }
            return false;
        }
    </script>
</head>
<body>

<div class="message warning">
    <div class="inset">
        <div class="message-head"><h1>Login Form</h1></div>
        <c:if test="${fail}">
            <div class="error">Wrong login or password</div>
        </c:if>
        <form id="loginform" onsubmit="return submitform()" method="post">
            <li>
                <input type="text" class="text" name="login" placeholder="Username" onfocus="hideErrorBox()">
                <a
                        class="icon user"></a>
            </li>
            <li>
                <input type="password" name="password" placeholder="Password" onfocus="hideErrorBox()">
                <a class="icon lock"></a>
            </li>
            <div class="submit">
                <input type="hidden" name="action" value="signin">
                <input type="submit" value="Sing In">
                <h4><a href=<c:url value="/chess/register"/>>Or register now</a></h4></div>
            <div class="clear"></div>
            <div class="errBox">
                <p id="errorBox"></p>
            </div>
        </form>
    </div>
</div>
</body>
</html>