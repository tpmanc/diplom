<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="cl" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Панель администратора - Войти</title>
    <link rel="shortcut icon" href="<spring:url value="/resources/favicon.png" />" type="image/png">
    <link href="<spring:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
    <link href="<spring:url value="/resources/css/style.css" />" rel="stylesheet">
</head>
<body class="gray-bg">
<div class="loginColumns">
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <h2 class="font-bold">Репозиторий инсталляционных пакетов</h2>
            <p>
                Для входа введите служебный номер и пароль
            </p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div class="inqbox-content">
                <cl:if test="${error}">
                    <div class="alert alert-danger">Неправильный служебный номер или пароль</div>
                </cl:if>
                <form class="m-t" role="form" method="post" action="<c:url value='j_spring_security_check' />">
                    <div class="form-group">
                        <input type="text" class="form-control" placeholder="Логин" required name="username">
                    </div>
                    <div class="form-group">
                        <input type="password" class="form-control" placeholder="Пароль" required name="password">
                    </div>
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="j_spring_security_check">
                            Запомнить
                        </label>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="btn btn-primary block full-width m-b">Войти</button>
                </form>
            </div>
        </div>
    </div>
    <hr/>
    <div class="row">
        <div class="col-md-6 col-md-offset-3">АО "ВПК "НПО машиностроения"</div>
    </div>
</div>
</body>
</html>
