<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="user" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<table class="table table-striped models-view">
    <thead>
    <tr>
        <th>employeeId</th>
        <th>displayName</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${users}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.id}</td>
            <td>${item.displayName}</td>
            <td>
                <a href="<spring:url value="/user-view?id=" />${item.id}" class="icon">
                    <i class="fa fa-eye"></i>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${pageCount > 1}">
    <c:set var="pageUrl" value="users" scope="request" />
    <jsp:include page="../widgets/paginator.jsp" />
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />