<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Repository - ${pageTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss" />
        <spring:url value="/resources/css/main.css" var="mainCss" />
        <spring:url value="/resources/css/nprogress.css" var="nprogressCss" />
        <spring:url value="/resources/css/js-tree/style.min.css" var="jsTreeCss" />
        <spring:url value="/resources/css/select2/select2.min.css" var="select2Css" />
        <spring:url value="/resources/css/arctic-modal/jquery.arcticmodal-0.3.css" var="arcticCss" />
        <spring:url value="/resources/css/arctic-modal/theme.css" var="arcticThemeCss" />
        <spring:url value="/resources/css/dropzone/dropzone.css" var="dropzoneCss" />

        <spring:url value="/resources/js/jquery-2.1.4.min.js" var="jquery" />
        <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapJs" />
        <spring:url value="/resources/js/nprogress.js" var="nprogressJs" />
        <spring:url value="/resources/js/jstree.min.js" var="jsTreeJs" />
        <spring:url value="/resources/js/select2.min.js" var="select2Js" />
        <spring:url value="/resources/js/jquery.arcticmodal-0.3.min.js" var="arcticJs" />
        <spring:url value="/resources/js/dropzone.js" var="dropzoneJs" />

        <link href="${bootstrapCss}" rel="stylesheet" />
        <link href="${mainCss}" rel="stylesheet" />
        <link href="${nprogressCss}" rel="stylesheet" />
        <link href="${jsTreeCss}" rel="stylesheet" />
        <link href="${select2Css}" rel="stylesheet" />
        <link href="${arcticCss}" rel="stylesheet" />
        <link href="${arcticThemeCss}" rel="stylesheet" />
        <link href="${dropzoneCss}" rel="stylesheet" />

        <script src="${jquery}"></script>
        <script src="${bootstrapJs}"></script>
        <script src="${nprogressJs}"></script>
        <script src="${jsTreeJs}"></script>
        <script src="${select2Js}"></script>
        <script src="${arcticJs}"></script>
        <script src="${dropzoneJs}"></script>
    </head>
    <body>
        <jsp:include page="header.jsp">
            <jsp:param name="firstname" value="${param.activePage}" />
        </jsp:include>

        <div class="container">
