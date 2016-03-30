<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<script src="<spring:url value="/resources/js/pages/unfilled-file/file-filling.js" />"></script>
<script>
    var fileTitleUrl = "<spring:url value="/file-title-autocomplete" />";
</script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/unfilled-files" />">Незаполненные файлы</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<form action="<spring:url value="/file-filling-handler" />" method="post" class="form-horizontal">
    <input type="hidden" name="versionId" value="${fileVersion.id}">
    <div class="form-group">
        <label class="col-sm-2 control-label">Название продукта</label>
        <div class="col-sm-10">
            <input type="text" id="titleAutocomplete" name="title" class="form-control" value="${file.title}">
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-2 control-label">Версия файла</label>
        <div class="col-sm-10">
            <input type="text" name="version" class="form-control" value="${fileVersion.version}">
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-4 col-sm-offset-2">
            <button class="btn btn-success" type="submit">Сохранить</button>
        </div>
    </div>
</form>

<jsp:include page="../layouts/layout_bottom.jsp" />