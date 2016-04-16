<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<script src="<spring:url value="/resources/js/pages/file/file-form.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/request-list" />">Заявки</a></li>
    <li><a href="<spring:url value="/request-view?requestId=${request.id}" />">Заявка #${request.id}</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<c:if test="${error == true}">
    <div class="alert alert-danger">
        Ошибка при сохранении
    </div>
</c:if>

<div class="kform">
    <form method="POST" action="<spring:url value="/request-accept-handler" />">
        <input type="hidden" name="requestId" value="${request.id}">
        <div class="section form-group">
            <label class="field">
                <textarea class="gui-textarea" required name="comment" placeholder="Комментарий"></textarea>
            </label>
        </div>

        <button type="submit" class="btn btn-success">Принять</button>
    </form>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />