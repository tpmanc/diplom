<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="layouts/init-settings_top.jsp" />

<div class="middle-box text-center">
    <h1>404</h1>
    <h3 class="font-bold">Страница не найдена</h3>

    <div class="row" style="margin-top: 40px;">
        <a href="<spring:url value="/" />" class="btn btn-primary">
            <i class="fa fa-home"></i>
            Вернуться на главную
        </a>
    </div>
</div>

<jsp:include page="layouts/init-settings_bottom.jsp" />
