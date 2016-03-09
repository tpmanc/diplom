<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title></title>
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
                <input type="text" class="form-control" name="j_username" id="j_username" placeholder="Login">
            </div>

            <div class="form-group">
                <label for="j_password">Password:</label>
                <input type="text" class="form-control" name="j_password" id="j_password" placeholder="Password">
            </div>

            <div class="checkbox">
                <label>
                    <input type="checkbox" name="_spring_security_remember_me">
                    Remember Me
                </label>
            </div>

            <button type="submit" class="btn btn-success">Sing in</button>

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
    </div>
</body>
</html>