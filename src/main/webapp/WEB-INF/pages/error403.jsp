<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="layouts/init-settings_top.jsp" />

<div class="middle-box text-center">
    <h1>403</h1>
    <h3 class="font-bold">Доступ запрещен</h3>

    <div class="row" style="margin-top: 40px;">
        <a href="<spring:url value="/" />" class="btn btn-primary">
            <i class="fa fa-home"></i>
            Вернуться на главную
        </a>
    </div>
</div>

<jsp:include page="layouts/init-settings_bottom.jsp" />
