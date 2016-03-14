<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../../layout_top.jsp" />

<h1>Users</h1>

<table class="table table-striped">
    <thead>
        <tr>
            <td>id</td>
            <td>username</td>
            <td>password</td>
            <td>is enabled</td>
            <td></td>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${users}" var="item" varStatus="itemStat">
            <tr>
                <td>${item.get("id")}</td>
                <td>${item.get("username")}</td>
                <td>${item.get("password")}</td>
                <td>${item.get("isEnabled")}</td>
                <td>
                    <a href="<spring:url value="/user-edit.html?id=" />${item.get("id")}">
                        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="../../layout_bottom.jsp" />
