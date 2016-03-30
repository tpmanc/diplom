<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="hasAnyRole('ROLE_FR-ADMIN', 'ROLE_FR-MODERATOR')">
    <nav class="navbar-default navbar-static-side fixed-menu" role="navigation">
        <div class="sidebar-collapse">
            <div id="hover-menu"></div>
            <ul class="nav metismenu" id="side-menu">
                <li>
                    <div class="logopanel">
                        <div class="profile-element">
                            <h2><a href="<spring:url value="/" />">Репозиторий Инсталляционных Пакетов</a></h2>
                        </div>
                        <div class="logo-element">РИП</div>
                    </div>
                </li>
                <li>
                    <div class="leftpanel-profile">
                        <div class="media-body profile-name">
                            <h4 class="media-heading"><sec:authentication property="principal.fullname" /></h4>
                            Служебный номер: <sec:authentication property="principal.employeeId" /> <br>
                            <span><sec:authentication property="principal.authorities"/></span>
                            <div><a href="<c:url value="/j_spring_security_logout" />" class="logout-btn"><i class="fa fa-sign-out"></i> Выйти</a></div>
                        </div>
                    </div>
                </li>
                <li>
                    <div class="nano left-sidebar">
                        <div class="nano-content">
                            <ul class="nav nav-pills nav-stacked nav-inq">
                                <li <c:if test="${param.activePage == 'catalog'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/catalog" />"><i class="fa fa-folder-open"></i> <span class="nav-label">Каталог</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'categoryTree'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/categories" />"><i class="fa fa-sitemap"></i> <span class="nav-label">Категории</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'file'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/files" />"><i class="fa fa-file-text-o"></i> <span class="nav-label">Файлы</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'unfilled-files'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/unfilled-files" />"><i class="fa fa-file"></i> <span class="nav-label">Незаполненные Файлы</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'property'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/properties" />"><i class="fa fa-list-ul"></i> <span class="nav-label">Свойства файлов</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'request'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/request-list" />"><i class="fa fa-clipboard"></i> <span class="nav-label">Заявки</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'user'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/users" />"><i class="fa fa-user"></i> <span class="nav-label">Пользователи</span></a>
                                </li>
                                <sec:authorize access="hasRole('ROLE_FR-ADMIN')">
                                    <li <c:if test="${param.activePage == 'log'}"> class="active" </c:if>>
                                        <a href="<spring:url value="/logs" />"><i class="fa fa-warning"></i> <span class="nav-label">Логи</span></a>
                                    </li>
                                </sec:authorize>
                            </ul>
                        </div>
                    </div>
                </li>
            </ul>
        </div>
    </nav>
</sec:authorize>

<div id="page-wrapper"
     class="gray-bg
            <sec:authorize access="!hasRole('ROLE_FR-ADMIN') && !hasRole('ROLE_FR-MODERATOR')">
                user-page
            </sec:authorize>
    ">

    <sec:authorize access="!hasRole('ROLE_FR-ADMIN') && !hasRole('ROLE_FR-MODERATOR')">
        <div>
            <nav class="navbar navbar-fixed-top white-bg show-menu-full" id="nav" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <a class="navbar-minimalize minimalize-styl-2 btn" href="<spring:url value="/" />">
                        <i class="fa fa-home" style="font-size:27px;"></i>
                    </a>
                    <form role="search" action="<spring:url value="/search" />" method="get" class="navbar-form-custom">
                        <div class="input-group top-search-holder">
                            <input type="text"
                                   name="text"
                                   class="form-control"
                                    <c:if test="${searchText != null}"> value="${searchText}" </c:if>
                                   placeholder="Поиск">
                            <span class="input-group-btn">
                                <button type="submit" class="btn btn-primary"><i class="fa fa-search"></i></button>
                            </span>
                        </div>
                    </form>
                </div>
                <ul class="nav navbar-top-links navbar-right">
                    <li class="dropdown hidden-xs">
                        <a class="dropdown-toggle count-info" href="<spring:url value="/request-list" />">
                            Мои заявки
                            <i class="fa fa-envelope"></i>  <span class="label label-danger">${requestCount}</span>
                        </a>
                    </li>
                    <li class="dropdown pull-right">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="true">
                            <span class="pl15"><sec:authentication property="principal.fullname" /></span>
                            <span class="caret caret-tp"></span>
                        </a>
                        <ul class="dropdown-menu m-t-xs">
                            <li><a href="<c:url value="/j_spring_security_logout" />"><i class="fa fa-sign-out"></i> Выйти</a></li>
                        </ul>
                    </li>
                </ul>
            </nav>
        </div>
    </sec:authorize>

    <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-lg-12">
                <div class="inqbox float-e-margins">
                    <div class="inqbox-content">
