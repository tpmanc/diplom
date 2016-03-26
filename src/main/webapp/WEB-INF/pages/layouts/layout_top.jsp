<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
    <head>
        <title>Repository - ${pageTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta charset="UTF-8">

        <link href="<spring:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/fonts/font-awesome/css/font-awesome.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/animate.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/kforms.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/dropzone.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/toastr.min.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/jquery.steps.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/js-tree/style.min.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/choosen/chosen.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/style.css" />" rel="stylesheet" />

        <script src="<spring:url value="/resources/js/libs/jquery-2.1.4.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/bootstrap.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/chosen.jquery.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/jstree.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/toastr.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/dropzone.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/jquery.steps.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/jquery.autocomplete.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/main.js"/>"></script>
        <script src="<spring:url value="/resources/js/libs/pace.min.js"/>"></script>
    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../header.jsp">
                <jsp:param name="firstname" value="${param.activePage}" />
            </jsp:include>