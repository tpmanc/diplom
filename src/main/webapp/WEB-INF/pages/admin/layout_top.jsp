<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Repository - ${pageTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <link href="<spring:url value="/resources/css/admin/bootstrap.min.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/fonts/admin/font-awesome/css/font-awesome.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/animate.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/kforms.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/dropzone.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/toastr.min.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/jquery.arcticmodal-0.3.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/js-tree/style.min.css" />" rel="stylesheet" />
        <link href="<spring:url value="/resources/css/admin/style.css" />" rel="stylesheet" />

        <script src="<spring:url value="/resources/js/admin/jquery-2.1.4.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/bootstrap.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/jquery.metisMenu.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/jquery.slimscroll.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/jstree.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/toastr.min.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/dropzone.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/main.js"/>"></script>
        <script src="<spring:url value="/resources/js/admin/pace.min.js"/>"></script>
    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="header.jsp">
                <jsp:param name="firstname" value="${param.activePage}" />
            </jsp:include>