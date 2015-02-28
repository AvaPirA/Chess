<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Load game</title>
    <%--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">--%>
</head>

<body>
    <c:if test="${not empty error}">
        <p>${error}</p>
    </c:if>
    <c:if test="${is_empty}">
        You have no saved games.
    </c:if>
    <table>
        <tr>
            <td>Turn</td>
            <td>Opponent Id</td>
            <td>Action</td>
            <td>Field</td>
        </tr>
        <c:forEach var="game" items="${sessionScope.games}">
            <tr>
                <td>${game.turnCount}</td>
                <c:choose><c:when test="${game.whiteId == sessionScope.user.id}">
                    <td>${game.blackId}</td> </c:when><c:otherwise>
                    <td>${game.whiteId}</td> </c:otherwise>
                </c:choose>
                <td>
                    <form action="<c:url value="/chess/load"/>" method="post">
                    <input type="submit"value="Load">
                    <input type="hidden"name="id"value=${game.id}>
                    <input type="hidden"name="action"value="load"></form>
                    <form action="<c:url value="/chess/load"/>" method="post">
                    <input type="submit"value="Delete">
                    <input type="hidden"name="id"value=${game.id}>
                    <input type="hidden"name="action"value="delete"></form>
                </td>
                <td>${game.fieldData}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>