<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="category" />
</jsp:include>

<script>
    var categoryDeleteUrl = "<spring:url value="/category-delete" />";
</script>
<spring:url value="/resources/js/pages/category/categories.js" var="categoriesJs" />
<script src="${categoriesJs}"></script>

<h1>${pageTitle}</h1>

<p>
    <a href="<spring:url value="/category-add" />" class="btn btn-success">Добавить категорию</a>
</p>

<table class="table table-striped">
    <thead>
    <tr>
        <td>id</td>
        <td>Название</td>
        <td>Включена</td>
        <td></td>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${categories}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.get("id")}</td>
            <td>${item.get("title")}</td>
            <td>${item.get("isEnabled")}</td>
            <td>
                <a href="<spring:url value="/category-edit?id=" />${item.get("id")}">
                    <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                </a>
                <span class="glyphicon glyphicon-trash category-delete" data-id="${item.id}" aria-hidden="true"></span>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />