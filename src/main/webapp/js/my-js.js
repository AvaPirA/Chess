/**
 * Created by Alpen Ditrix on 28.02.2015.
 */

function showErrorBox(error) {
    document.getElementById("errorBox").innerHTML = error;
    $(".errBox").addClass("errBox-change");
}
function hideErrorBox() {
    $(".errBox").removeClass("errBox-change");
}
function nullifyPasswordL() {
    document.forms["loginForm"].elements.namedItem("password").value = "";
}function nullifyPasswordR() {
    document.forms["registerForm"].elements.namedItem("password1").value = "";
    document.forms["registerForm"].elements.namedItem("password2").value = "";
}
function submitLogin() {
    var inputs = document.forms["loginForm"].elements;
    var login = inputs.namedItem("login").value;
    var password = inputs.namedItem("password").value;
    if (login.length > 0) {
        if (password.length > 2) {
            $.post("/chess/login", {login: login, password: password, action: "SIGN_IN"},
                function (data) {
                    if (data == "OK") {
                        window.location.replace("/chess/start");
                        return true;
                    } else if (data == "FAIL") {
                        nullifyPasswordL();
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
function submitRegister() {
    var inputs = document.forms["registerForm"].elements;
    var login = inputs.namedItem("login").value;
    var pass1 = inputs.namedItem("password1").value;
    var pass2 = inputs.namedItem("password2").value;
    if (login.length > 0) {
        if (login.indexOf(" ") == -1) {
            if (pass1 === pass2) {
                if (pass1.length > 2) {
                    $.post("/chess/register", {login: login, password: pass1, action: "REGISTER"}, function (data) {
                        nullifyPasswordR();
                        if (data == "OK") {
                            window.location.replace("/chess/start");
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