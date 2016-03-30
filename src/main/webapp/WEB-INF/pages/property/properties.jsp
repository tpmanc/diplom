<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <a href="<spring:url value="/admin/property-add" />" class="btn btn-success">Добавить свойство</a>
</p>

<table class="table table-striped models-view">
    <thead>
    <tr>
        <th>id</th>
        <th>title</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${properties}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.id}</td>
            <td>${item.title}</td>
            <td>
                <a href="<spring:url value="/admin/property-view?id=" />${item.id}" class="icon">
                    <i class="fa fa-eye"></i>
                </a>
                <c:if test="${item.custom == true}">
                    <a href="<spring:url value="/admin/property-edit?id=" />${item.id}" class="icon">
                        <i class="fa fa-edit"></i>
                    </a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />