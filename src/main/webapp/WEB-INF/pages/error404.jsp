<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="layouts/init-settings_top.jsp" />

<div class="middle-box text-center">
    <h1>404</h1>
    <h3 class="font-bold">
        <c:choose>
        <c:when test="${message != null}">
            ${message}
        </c:when>
        <c:otherwise>
            Страница не найдена
        </c:otherwise>
        </c:choose>
    </h3>

    <div class="row" style="margin-top: 40px;">
        <a href="#" onclick="history.go(-1);" class="btn btn-success">
            Назад
        </a>
        <a href="<spring:url value="/" />" class="btn btn-primary">
            Вернуться на главную
        </a>
    </div>
</div>

<jsp:include page="layouts/init-settings_bottom.jsp" />
