<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Repository - Login</title>
    <spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss" />
    <spring:url value="/resources/js/jquery-2.1.4.min.js" var="jquery" />
    <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapJs" />
    <link href="${bootstrapCss}" rel="stylesheet" />
    <script src="${jquery}"></script>
    <script src="${bootstrapJs}"></script>
</head>
<body>
    <div class="container">
        <h1>Sing in</h1>

        <form action="<c:url value='j_spring_security_check' />" method="POST">

            <div class="form-group">
                <label for="j_username">Login:</label>
                <input type="text" class="form-control" name="username" id="j_username" placeholder="Login">
            </div>

            <div class="form-group">
                <label for="j_password">Password:</label>
                <input type="password" class="form-control" name="password" id="j_password" placeholder="Password">
            </div>

            <div class="checkbox">
                <label>
                    <input type="checkbox" name="j_spring_security_check">
                    Remember Me
                </label>
            </div>

            <button type="submit" class="btn btn-success">Sing in</button>

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
    </div>
</body>
</html>
