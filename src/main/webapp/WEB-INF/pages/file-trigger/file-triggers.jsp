<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="trigger" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/file-trigger/file-triggers.js" />"></script>
<script>
    var fileTriggerDeleteUrl = "<spring:url value="/file-trigger-delete" />";
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
    <a href="<spring:url value="/file-trigger-add" />" class="btn btn-success">Создать триггер</a>
</p>

<table class="table table-striped models-view">
    <thead>
    <tr>
        <th>Расширение</th>
        <th>Команда</th>
        <th>Регулярка</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${triggers}" var="item" varStatus="itemStat">
        <tr>
            <td>${item.extension}</td>
            <td>${item.command}</td>
            <td><c:out value="${item.regexp}"/></td>
            <td>
                <a href="<spring:url value="/file-trigger-edit?id=" />${item.id}" class="icon">
                    <i class="fa fa-edit"></i>
                </a>
                <a data-id="${item.id}" class="icon remove-file-trigger">
                    <i class="fa fa-trash"></i>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />