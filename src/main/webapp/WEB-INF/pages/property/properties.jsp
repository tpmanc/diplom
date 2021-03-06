<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/property/properties.js" />"></script>
<script>
    var propertyDeleteUrl = "<spring:url value="/property-delete" />";
</script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <a href="<spring:url value="/property-add" />" class="btn btn-success">Добавить свойство</a>
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
                <a href="<spring:url value="/property-view?id=" />${item.id}" class="icon">
                    <i class="fa fa-eye"></i>
                </a>
                <c:if test="${item.custom == true}">
                    <a href="<spring:url value="/property-edit?id=" />${item.id}" class="icon">
                        <i class="fa fa-edit"></i>
                    </a>
                </c:if>
                <c:if test="${item.custom == true}">
                    <a data-id="${item.id}" class="icon remove-property">
                        <i class="fa fa-trash"></i>
                    </a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${pageCount > 1}">
    <c:set var="pageUrl" value="properties" scope="request" />
    <jsp:include page="../widgets/paginator.jsp" />
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />