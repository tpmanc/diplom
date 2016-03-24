<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

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
            <td>Телефон</td>
            <td>${user.phone}</td>
        </tr>
        <tr>
            <td>Факс</td>
            <td>${user.fax}</td>
        </tr>
        <tr>
            <td>Отдел</td>
            <td>${user.department}</td>
        </tr>
        <tr>
            <td>Номер отдела</td>
            <td>${user.departmentNumber}</td>
        </tr>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />