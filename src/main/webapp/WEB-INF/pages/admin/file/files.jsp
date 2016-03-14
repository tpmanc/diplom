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

<c:if test="${pageCount > 1}">
    <div class="btn-group">
        <c:if test="${page > 1}">
            <a href="<spring:url value="/admin/files?page=" />${page - 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-left"></i></a>
        </c:if>

        <c:forEach begin="1" end="${pageCount}" var="number">
            <a href="<spring:url value="/admin/unfilled-files?page=" />${number}"
               class="btn btn-white <c:if test="${page == number}">active</c:if>">${number}</a>
        </c:forEach>

        <c:if test="${page < pageCount}">
            <a href="<spring:url value="/admin/files?page=" />${page + 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-right"></i></a>
        </c:if>
    </div>
</c:if>

<jsp:include page="../layout_bottom.jsp" />