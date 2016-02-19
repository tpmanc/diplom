<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="category" />
</jsp:include>

<script>
    var categoryDeleteUrl = "<spring:url value="/admin/category-delete" />";
</script>
<spring:url value="/resources/js/pages/admin/category/categories.js" var="categoriesJs" />
<script src="${categoriesJs}"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <a href="<spring:url value="/admin/category-add" />" class="btn btn-success">Добавить категорию</a>
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
                <a href="<spring:url value="/admin/category-edit?id=" />${item.get("id")}">
                    <i class="fa fa-eye"></i>
                </a>
                <i class="fa fa-trash category-delete" data-id="${item.id}"></i>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />