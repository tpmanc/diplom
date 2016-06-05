<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/files" />">Файлы</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <form role="search" action="<spring:url value="/file-search" />" method="get" class="navbar-form-custom">
        <div class="input-group">
            <input type="text" name="text" class="form-control" value="${text}" placeholder="Поиск">
            <span class="input-group-btn">
                <button type="submit" class="btn btn-primary"><i class="fa fa-search"></i></button>
            </span>
        </div>
    </form>
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
    <c:forEach items="${files}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.id}</td>
            <td>${item.title}</td>
            <td>
                <a href="<spring:url value="/file-view?id=" />${item.id}" class="icon">
                    <i class="fa fa-eye"></i>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />