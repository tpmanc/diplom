<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<h1>${pageTitle}</h1>

<p>
    <a href="<spring:url value="/property-add" />" class="btn btn-success">Добавить файлы</a>
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
    <c:forEach items="${properties}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.get("id")}</td>
            <td>${item.get("title")}</td>
            <td>
                <a href="<spring:url value="/property-view?id=" />${item.get("id")}" class="icon">
                    <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                </a>
                <a href="<spring:url value="/property-edit?id=" />${item.get("id")}" class="icon">
                    <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />