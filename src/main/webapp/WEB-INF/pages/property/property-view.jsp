<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/admin/properties" />">Свойства файлов</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<table class="table table-striped models-view">
    <tbody>
        <tr>
            <td>Id</td>
            <td>${property.id}</td>
        </tr>
        <tr>
            <td>Название</td>
            <td>${property.title}</td>
        </tr>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />