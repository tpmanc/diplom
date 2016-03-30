<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="unfilled-files" />
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
    <a href="<spring:url value="/file-add" />" class="btn btn-success">Добавить файлы</a>
</p>

<p>
    <sec:authorize access="hasRole('ROLE_FR-ADMIN')">
        <a href="<spring:url value="/unfilled-files" />" class="btn btn-success">Мои файлы</a>
        <a href="<spring:url value="/unfilled-files?all=true" />" class="btn btn-success">Все файлы</a>
    </sec:authorize>
</p>

<table class="table table-striped models-view">
    <thead>
    <tr>
        <th>Id</th>
        <th>Имя файла</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${files}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.get("id")}</td>
            <td>${item.get("title")}</td>
            <td>
                <c:choose>
                    <c:when test="${item.get(\"isNoCategory\") == 0}">
                        <a href="<spring:url value="/file-filling?versionId=${item.get(\"id\")}" />" class="btn btn-danger">Заполнить данные</a>
                    </c:when>
                    <c:when test="${item.get(\"isNoCategory\") == 1}">
                        <a href="<spring:url value="/file-categories?fileId=${item.get(\"id\")}" />" class="btn btn-danger">Привязать к категориям</a>
                    </c:when>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${pageCount > 1}">
    <div class="btn-group">
        <c:if test="${page > 1}">
            <a href="<spring:url value="/unfilled-files?page=${page - 1}&${allUrl}" />" type="button" class="btn btn-white"><i class="fa fa-chevron-left"></i></a>
        </c:if>

        <c:forEach begin="1" end="${pageCount}" var="number">
            <a href="<spring:url value="/unfilled-files?page=${number}&${allUrl}" />"
               class="btn btn-white <c:if test="${page == number}">active</c:if>">${number}</a>
        </c:forEach>

        <c:if test="${page < pageCount}">
            <a href="<spring:url value="/unfilled-files?page=${page + 1}&${allUrl}" />" type="button" class="btn btn-white"><i class="fa fa-chevron-right"></i></a>
        </c:if>
    </div>
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />