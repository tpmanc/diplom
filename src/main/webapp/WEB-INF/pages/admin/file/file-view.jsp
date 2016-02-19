<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/admin/files" />">Файлы</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<h3>${file.title}</h3>

<table class="table table-striped models-view">
    <tbody>
        <tr>
            <td>Id</td>
            <td>${file.id}</td>
        </tr>
        <tr>
            <td>Название</td>
            <td>${file.title}</td>
        </tr>
    </tbody>
</table>

<h3>Свойства</h3>
<table class="table table-striped models-view">
    <tbody>
        <c:forEach items="${fileProperties}" var="item" varStatus="itemStat">
            <tr>
                <td>${item.title}</td>
                <td>${item.value}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />