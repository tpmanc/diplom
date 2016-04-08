<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
    <script src="<spring:url value="/resources/js/pages/file/file-view.js" />"></script>
    <script>
        var filePropertyDeleteUrl = "<spring:url value="/file-property-delete" />";
        var fileVersionPropertyDeleteUrl = "<spring:url value="/file-version-property-delete" />";
        var fileVersionDeleteUrl = "<spring:url value="/file-version-delete" />";
        var fileVersionRecoverUrl = "<spring:url value="/file-version-recover" />";
        var fileVersionDeletePermanentUrl = "<spring:url value="/file-version-delete-permanent" />";
    </script>
</sec:authorize>
<sec:authorize access="!hasRole('ROLE_FR-ADMIN') && !hasRole('ROLE_FR-MODERATOR')">
    <script src="<spring:url value="/resources/js/pages/catalog/file-view.js" />"></script>
</sec:authorize>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
        <li><a href="<spring:url value="/files" />">Файлы</a></li>
    </sec:authorize>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
        <a href="<spring:url value="/file-categories?fileId=${file.id}" />" class="btn btn-info">Редактировать категории</a>
        <a href="<spring:url value="/file-filling?versionId=${currentVersion.id}" />" class="btn btn-warning">Изменить</a>
    </sec:authorize>
    <a href="<spring:url value="/file-download?id=${currentVersion.id}" />" class="btn btn-primary">Скачать файл</a>
</p>

<sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
<p>
    <a href="<spring:url value="/file-property-add?id=${file.id}" />" class="btn btn-success">Добавить свойство файла</a>
    <a href="<spring:url value="/file-version-property-add?id=${currentVersion.id}" />" class="btn btn-success">Добавить свойство версии</a>
    <sec:authorize access="hasRole('ROLE_FR-ADMIN')">
        <a href="<spring:url value="/file-export?versionId=${currentVersion.id}" />" class="btn btn-warning">Экспорт</a>
    </sec:authorize>
</p>
<p>
    <a href="#" id="deleteVersionBtn" data-versionid="${currentVersion.id}" class="btn btn-danger">Удалить версию</a>
    <sec:authorize access="hasRole('ROLE_FR-ADMIN')">
        <c:if test="${currentVersion.isDisabled}">
            <a href="#" id="deleteVersionPermanentBtn" data-versionid="${currentVersion.id}" class="btn btn-danger">Удалить окончательно</a>
        </c:if>
    </sec:authorize>
</p>
</sec:authorize>

<c:if test="${fileCategories.size() == 0}">
    <div class="alert alert-danger">
        Файл не привязан ни к одной категории! <a class="alert-link" href="<spring:url value="/file-categories?fileId=${file.id}" />">Исправить</a>
    </div>
</c:if>

<c:if test="${currentVersion.isDisabled}">
    <div class="alert alert-danger">Файл удален. <a class="alert-link" data-versionid="${currentVersion.id}" id="recoverVersionBtn" href="#">Восстановить</a></div>
</c:if>

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
        <sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
            <tr>
                <td>Загрузил</td>
                <td><a href="<spring:url value="/user-view?id=${user.id}" />">${user.displayName}</a></td>
            </tr>
        </sec:authorize>
        <tr>
            <td>Версия</td>
            <td>
                <form action="<spring:url value="/file-view" />" method="get">
                    <input type="hidden" name="id" value="${file.id}">
                    <select id="versionSelect" name="versionId">
                        <c:forEach items="${versionList}" var="item" varStatus="itemStat">
                            <option <c:if test="${currentVersion.id == item.id}"> selected="selected" </c:if> value="${item.id}">${item.version} <c:if test="${item.isDisabled}">(удален)</c:if></option>
                        </c:forEach>
                    </select>
                </form>
            </td>
        </tr>
        <tr>
            <td>Категории</td>
            <td>
                <c:forEach items="${fileCategories}" var="category" varStatus="itemStat">
                    <span class="label">${category.title}</span>
                </c:forEach>
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
            <sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
                <td>
                    <a href="<spring:url value="/file-property-edit?id=" />${item.id}" class="icon">
                        <i class="fa fa-edit"></i>
                    </a>
                    <a data-link="${item.id}" class="icon remove-file-property">
                        <i class="fa fa-trash"></i>
                    </a>
                </td>
            </sec:authorize>
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
                <sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
                    <td>
                        <a href="<spring:url value="/file-version-property-edit?id=" />${item.id}" class="icon">
                            <i class="fa fa-edit"></i>
                        </a>
                        <a data-link="${item.id}" class="icon remove-file-version-property">
                            <i class="fa fa-trash"></i>
                        </a>
                    </td>
                </sec:authorize>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />