<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Start</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">
</head>

<body>
    <c:set var="ID" value="${sessionScope.id}"/>
    Hello, <b>${sessionScope.user.login}</b>!

    <form action="/start" method="POST">
        <input type="submit" value="Find!">
        <input type="hidden" name="action" value="find">
    </form>
    <form action="/login" method="POST">
            <input type="submit" value="Log out">
            <input type="hidden" name="action" value="logout">
    </form>

    <c:if test="${sessionScope.user.stateInt==1}">

        <c:if test="${not empty friend}">
            <p>
                You offered to play with ${friend}
            </p>
        </c:if>

        <c:if test="${sessionScope.user.offersAmount>0}">
            <p>
                You have some play offers!
                <table>
                    <tr>
                        <td>Id</td>
                        <td>Login</td>
                        <td>State</td>
                    </tr>
                    <c:forEach var="friend" items="${sessionScope.user.offers}">
                        <tr>
                            <td>${friend.id}</td>
                            <td>${friend.login}</td>
                            <td>${friend.stateInt}</td>
                            <td>
                                <form action="/start" method="post"><input type="submit"value="Play">
                                <input type="hidden"name="id"value=${friend.id}>
                                <input type="hidden"name="action"value="play"></form>
                        </tr>
                    </c:forEach>
                </table>
         </c:if>
        <p/>
        <p>
        Another user searching for opponents
            <table>
                    <tr>
                        <td>Id</td>
                        <td>Login</td>
                        <td>State</td>
                    </tr>

                    <c:forEach var="user" items="${applicationScope.waiters}">
                        <c:if test="${user.id != sessionScope.user.id}">
                            <tr>
                                <td>${user.id}</td>
                                <td>${user.login}</td>
                                <td>${user.stateInt}</td>
                                <td>
                                    <form action="/start" method="post"><input type="submit"value="Play">
                                    <input type="hidden"name="id"value=${user.id}>
                                    <input type="hidden"name="action"value="play"></form>
                            </tr>
                        </c:if>
                    </c:forEach>
            </table>
        </p>
    </c:if>
    <form action="/start" method="post">
    <input type="submit"value="Load">
    <input type="hidden"name="action"value="load"></form>
</body>
</html>