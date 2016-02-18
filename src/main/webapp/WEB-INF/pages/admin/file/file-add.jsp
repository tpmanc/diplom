<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<spring:url value="/resources/js/pages/admin/file/file-form.js" var="fileFormJs" />
<script src="${fileFormJs}"></script>

<h1>Добавить файл</h1>

<jsp:include page="_file-form.jsp" />

<jsp:include page="../layout_bottom.jsp" />