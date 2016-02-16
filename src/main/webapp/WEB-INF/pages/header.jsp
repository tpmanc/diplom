<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>

    <nav class="navbar navbar-inverse">
        <div class="container-fluid">

            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="<spring:url value="/index" />">Repository</a>
            </div>

            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li <c:if test="${param.activePage == 'user'}"> class="active" </c:if>>
                        <a href="<spring:url value="/users" />">
                            Пользователи <span class="sr-only">(current)</span>
                        </a>
                    </li>
                    <li <c:if test="${param.activePage == 'category'}"> class="active" </c:if>>
                        <a href="<spring:url value="/categories" />">Категории</a>
                    </li>
                    <li <c:if test="${param.activePage == 'categoryTree'}"> class="active" </c:if>>
                        <a href="<spring:url value="/category-trees" />">Редактор меню</a>
                    </li>
                    <li <c:if test="${param.activePage == 'file'}"> class="active" </c:if>>
                        <a href="<spring:url value="/files" />">Файлы</a>
                    </li>
                    <li <c:if test="${param.activePage == 'property'}"> class="active" </c:if>>
                        <a href="<spring:url value="/properties" />">Свойства файлов</a>
                    </li>
                </ul>

                <form class="navbar-form navbar-left" action="<spring:url value='/search' />" role="search">
                    <div class="form-group">
                        <input type="text" class="form-control" placeholder="Search">
                    </div>
                    <button type="submit" class="btn btn-default">Search</button>
                </form>

                <form class="navbar-form navbar-right" method="post" action="<spring:url value="/j_spring_security_logout" />">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="btn btn-link">Logout (${pageContext.request.userPrincipal.name})</button>
                </form>

            </div>
        </div>
    </nav>
</header>
