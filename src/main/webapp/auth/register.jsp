<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Register</title>
    <link rel="stylesheet" type="text/css" href=<c:url value="/css/common.css"/>>
    <script type="text/javascript" src="<c:url value="/js/jquery-2.1.3.min.js"/>"></script>
    <script type="text/javascript">
        function showErrorBox(error) {
            document.getElementById("errorBox").innerHTML = error;
            $(".errBox").addClass("errBox-change");
            return false;
        }
        function hideErrorBox() {
            $(".errBox").removeClass("errBox-change");
        }
        function nullifyPassword() {
            document.forms["registerForm"].elements.namedItem("password1").value = "";
            document.forms["registerForm"].elements.namedItem("password2").value = "";
        }
        function submitRegister() {
            var inputs = document.forms["registerForm"].elements;
            var login = inputs.namedItem("login").value;
            var pass1 = inputs.namedItem("password1").value;
            var pass2 = inputs.namedItem("password2").value;
            if (login.length > 0) {
                if (login.indexOf(" ") == -1) {
                    if (pass1 === pass2) {
                        if (pass1.length > 2) {
                            $.post("<c:url value="/chess/register"/>", {login: login, password: pass1, action: "REGISTER"}, function (data) {
                                nullifyPassword();
                                if (data == "OK") {
                                    window.location.replace("<c:url value="/chess/start"/>");
                                } else if (data == "FAIL_REQ") {
                                    showErrorBox("Your data do not fits to the requirements");
                                } else if (data == "FAIL_REG") {
                                    showErrorBox("User with that nickname already exists, sorry.");
                                } else {
                                    showErrorBox(data);
                                }
                                return false;
                            });
                            return false;
                        } else showErrorBox("Password is too short");
                    } else showErrorBox("Passwords are not equal");
                } else showErrorBox("Login can not contain space character");
            } else showErrorBox("Login can not be empty");
            return false;
        }
    </script>
</head>

<body>
<div class="message warning">
    <div class="inset">
        <div class="message-head"><h1>Registration Form</h1></div>

        <form id="registerForm" onsubmit="return submitRegister()">
            <li><input type="text" name="login" placeholder="Username" onfocus="hideErrorBox()" autofocus/><a class="icon user"></a></li>
            <li><input type="password" name="password1" placeholder="Password" onfocus="hideErrorBox()"><a class="icon lock"></a></li>
            <li><input type="password" name="password2" placeholder="Repeat password" onfocus="hideErrorBox()"><a class="icon lock"></a></li>
            <div class="submit">
                <input type="submit" value="Sing Up">
                <h4><a href=<c:url value="/chess/login"/>>Or login now</a></h4>
            </div>
            <div class="clear"></div>

            <div id="errorBox" class="errBox"></div>
        </form>
    </div>
</div>
<c:if test="${not empty fail and not fail}">
    You've been successfully registered! Now you may go <a href="<c:url value="/chess/start"/>">further</a>
</c:if>
</body>

</html>