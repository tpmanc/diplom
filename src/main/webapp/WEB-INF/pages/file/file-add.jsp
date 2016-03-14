<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/admin/file/file-form.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/admin/files" />">Файлы</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<jsp:include page="_file-form.jsp" />

<jsp:include page="../layout_bottom.jsp" />