<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<script src="<spring:url value="/resources/js/pages/file/file-form.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/request-list" />">Мои заявки</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<div class="kform">
    <form method="POST" enctype="multipart/form-data" action="<spring:url value="/request-add-handler" />">
        <div class="section form-group <c:if test="${errors.get(\"text\") != null}">has-error</c:if>">
            <label class="field">
                <textarea class="gui-textarea" required name="text" placeholder="Значение">${text}</textarea>
            </label>
            <c:if test="${errors.get(\"text\") != null}">
                <span class="help-block">
                        <div>${errors.get("text")}</div>
                </span>
            </c:if>
        </div>

        <div>
            <div class="section form-group <c:if test="${errors.get(\"file\") != null}">has-error</c:if>">
                <label class="field prepend-icon append-button file">
                    <span class="button">Выберите файлы</span>
                    <input type="file" multiple class="gui-file" name="file[]" id="file1" onchange="document.getElementById('uploader1').value = this.value;">
                    <input type="text" class="gui-input" id="uploader1" placeholder="Выберите файлы">
                    <label class="field-icon">
                        <i class="fa fa-upload"></i>
                    </label>
                </label>
                <c:if test="${errors.get(\"file\") != null}">
                <span class="help-block">
                        <div>${errors.get("file")}</div>
                </span>
                </c:if>
            </div>
        </div>

        <button type="submit" class="btn btn-success">Отправить</button>
    </form>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />