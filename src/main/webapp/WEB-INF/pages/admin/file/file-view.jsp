<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/admin/file/file-view.js" />"></script>
<script>
    var filePropertyDeleteUrl = "<spring:url value="/admin/file-property-delete" />";
</script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/admin/files" />">Файлы</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <a href="<spring:url value="/admin/file-add-property?id=${file.id}" />" class="btn btn-success">Добавить свойство файла</a>
    <a href="<spring:url value="/admin/version-add-property?id=${lastVersion.id}" />" class="btn btn-success">Добавить свойство версии</a>
    <a href="<spring:url value="/admin/file-download?id=${lastVersion.id}" />" class="btn btn-primary">Скачать файл</a>
</p>

<h3>${file.title}</h3>

<table class="table table-striped models-view">
    <tbody>
        <tr>
            <td>Id</td>
            <td>${file.id}</td>
        </tr>
        <tr>
            <td>Название</td>
            <td>${file.title}</td>
        </tr>
        <tr>
            <td>Последняя версия</td>
            <td>${lastVersion.version}</td>
        </tr>
    </tbody>
</table>

<div class="hr-line-dashed"></div>

<h3>Свойства файла</h3>
<table class="table table-striped models-view">
    <tbody>
    <c:forEach items="${fileProperties}" var="item" varStatus="itemStat">
        <tr class="file-property-holder">
            <td>${item.title}</td>
            <td>${item.value}</td>
            <td>
                <a href="<spring:url value="/admin/file-edit-property?id=" />${item.get("id")}" class="icon">
                    <i class="fa fa-edit"></i>
                </a>
                <a data-link="${item.get("id")}" class="icon remove-file-property">
                    <i class="fa fa-trash"></i>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="hr-line-dashed"></div>

<h3>Свойства версии</h3>
<table class="table table-striped models-view">
    <tbody>
        <tr>
            <td>Дата загрузки</td>
            <td>${downloadDate}</td>
        </tr>
        <c:forEach items="${fileVersionProperties}" var="item" varStatus="itemStat">
            <tr>
                <td>${item.title}</td>
                <td>${item.value}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />