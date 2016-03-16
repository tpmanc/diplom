<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/admin/file/file-view.js" />"></script>
<script>
    var filePropertyDeleteUrl = "<spring:url value="/admin/file-property-delete" />";
    var fileVersionPropertyDeleteUrl = "<spring:url value="/admin/file-version-property-delete" />";
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
    <c:if test="${isFileOwner == true}">
        <a href="<spring:url value="/file-filling?versionId=${currentVersion.id}" />" class="btn btn-warning">Изменить</a>
    </c:if>
    <a href="<spring:url value="/admin/file-download?id=${currentVersion.id}" />" class="btn btn-primary">Скачать файл</a>
</p>
<p>
    <a href="<spring:url value="/admin/file-property-add?id=${file.id}" />" class="btn btn-success">Добавить свойство файла</a>
    <a href="<spring:url value="/admin/file-version-property-add?id=${currentVersion.id}" />" class="btn btn-success">Добавить свойство версии</a>
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
            <td>Загрузил</td>
            <td><a href="<spring:url value="/user-view?id=${user.id}" />">${user.displayName}</a></td>
        </tr>
        <tr>
            <td>Версия</td>
            <td>
                <form action="<spring:url value="/file-view" />" method="get">
                    <input type="hidden" name="id" value="${file.id}">
                    <select id="versionSelect" name="versionId">
                        <c:forEach items="${versionList}" var="item" varStatus="itemStat">
                            <option <c:if test="${currentVersion.id == item.get(\"id\")}"> selected="selected" </c:if> value="${item.get("id")}">${item.get("version")}</option>
                        </c:forEach>
                    </select>
                </form>
            </td>
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
                <a href="<spring:url value="/admin/file-property-edit?id=" />${item.get("id")}" class="icon">
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
            <td></td>
        </tr>
        <c:forEach items="${fileVersionProperties}" var="item" varStatus="itemStat">
            <tr class="file-version-property">
                <td>${item.title}</td>
                <td>${item.value}</td>
                <td>
                    <a href="<spring:url value="/admin/file-version-property-edit?id=" />${item.get("id")}" class="icon">
                        <i class="fa fa-edit"></i>
                    </a>
                    <a data-link="${item.get("id")}" class="icon remove-file-version-property">
                        <i class="fa fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />