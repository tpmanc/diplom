<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<spring:url value="/resources/js/pages/admin/file/file-add-property.js" var="fileFormJs" />
<script src="${fileFormJs}"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/admin/files" />">Файлы</a></li>
    <li><a href="<spring:url value="/admin/file-view?id=${fileProperty.fileId}" />">Просмотр файла</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<form action="<spring:url value="/admin/file-property-handler" />" method="post" class="form-horizontal">
    <input type="hidden" name="id" value="${fileProperty.id}">
    <input type="hidden" name="fileId" value="${fileProperty.fileId}">
    <input type="hidden" name="propertyId" value="${fileProperty.propertyId}">
    <div class="form-group">
        <label class="col-sm-2 control-label">Свойство</label>
        <div class="col-sm-10">
            ${fileProperty.title}
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-2 control-label">Значение</label>
        <div class="col-sm-10">
            <input type="text" name="value" class="form-control" value="${fileProperty.value}">
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-4 col-sm-offset-2">
            <button class="btn btn-success" type="submit">Добавить</button>
        </div>
    </div>
</form>

<jsp:include page="../layout_bottom.jsp" />