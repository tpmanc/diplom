<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp" />

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/users" />">Пользователи</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<table class="table table-striped models-view">
    <tbody>
        <tr>
            <td>employeeId</td>
            <td>${user.id}</td>
        </tr>
        <tr>
            <td>displayName</td>
            <td>${user.displayName}</td>
        </tr>
        <tr>
            <td>email</td>
            <td>${user.email}</td>
        </tr>
        <tr>
            <td>phone</td>
            <td>${user.phone}</td>
        </tr>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />