<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="unfilled-files" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <a href="<spring:url value="/file-add" />" class="btn btn-success">Добавить файлы</a>
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
    <c:set var="pageUrl" value="unfilled-files" scope="request" />
    <jsp:include page="../widgets/paginator.jsp" />
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />