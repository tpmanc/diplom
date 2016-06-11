<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="trigger" />
</jsp:include>

<h2>Добавить свойство файла</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/file-triggers" />">Триггеры</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<jsp:include page="_form.jsp" >
    <jsp:param name="trigger" value="${trigger}" />
</jsp:include>

<jsp:include page="../layouts/layout_bottom.jsp" />