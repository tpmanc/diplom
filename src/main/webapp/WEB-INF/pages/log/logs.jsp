<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="log" />
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
        <td>Id</td>
        <td>Дата</td>
        <td>Пользователь</td>
        <td>Действие</td>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${logs}" var="log" varStatus="itemStat">
        <tr>
            <td>${log.id}</td>
            <td>${log.stringDate}</td>
            <td>${log.userId}</td>
            <td>${log.message}}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${pageCount > 1}">
    <div class="btn-group">
        <c:if test="${page > 1}">
            <a href="<spring:url value="/users?page=" />${page - 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-left"></i></a>
        </c:if>

        <c:forEach begin="1" end="${pageCount}" var="number">
            <a href="<spring:url value="/users?page=" />${number}"
               class="btn btn-white <c:if test="${page == number}">active</c:if>">${number}</a>
        </c:forEach>

        <c:if test="${page < pageCount}">
            <a href="<spring:url value="/users?page=" />${page + 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-right"></i></a>
        </c:if>
    </div>
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />