<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
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
    <a href="<spring:url value="/admin/file-add" />" class="btn btn-success">Добавить файлы</a>
</p>

<table class="table table-striped models-view">
    <thead>
    <tr>
        <td>id</td>
        <td>title</td>
        <td></td>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${files}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.get("id")}</td>
            <td>${item.get("title")}</td>
            <td>
                <a href="<spring:url value="/admin/file-view?id=" />${item.get("id")}" class="icon">
                    <i class="fa fa-eye"></i>
                </a>
                <a href="<spring:url value="/admin/file-edit?id=" />${item.get("id")}" class="icon">
                    <i class="fa fa-edit"></i>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />