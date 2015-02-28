<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Bookmark Page</title>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">--%>
</head>

<body>
    <c:set var="user" value="${sessionScope.user}"/>

    <div>
        ${user.game.prettyJspField}
    </div>

    <c:if test="${empty user.game.lastTurnInfo}">
        Game started!
    </c:if>

    <div>
        ${user.game.lastTurnInfo.log}
    </div>

    <c:choose>
        <c:when test="${user.id == user.game.actor}">
            Your turn, <b>${user.login}</b>. Turn of ${user.game.turnColor}.
            <c:if test="${not empty error}">
                <div class="error">
                    ${error}
                </div>
            </c:if>
            <div>
                <form action="" method="POST">
                    <p><input type="text" name="from" value="" autofocus>From<br>
                    <input type="text" name="to" value="">To</p>
                    <p><input type="submit" name="go" value="Go">
                    <input type="hidden" name="action" value="go">
                </form>
            </div>
        </c:when>
        <c:otherwise>
            <div>
                Opponents turn. Turn of ${user.game.turnColor}.
                <% response.setIntHeader("Refresh",10); %>
            </div>
        </c:otherwise>
    </c:choose>


    <p>
        Turn <i>#${user.game.turnCount}</i><br>
        Your opponent: <b>${user.opponent.login}</b>.${user.opponent.id}
    </p>
    <form action="<c:url value="/chess/game"/>" method="POST">
        <p><input type="submit" name="save" value="Save">
        <input type="hidden" name="action" value="save">
    </form>
    <form action="<c:url value="/chess/game"/>" method="POST">
        <p><input type="submit" name="save" value="Exit">
        <input type="hidden" name="action" value="exit">
    </form>

    <c:if test="${not empty saved}">
        <c:choose>
            <c:when test="${saved}">
                Game successfully saved!
            </c:when>
            <c:otherwise>
                Game saving failed!
            </c:otherwise>
        </c:choose>
    </c:if>
</body>
</html>