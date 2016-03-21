<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<nav class="navbar-default navbar-static-side fixed-menu" role="navigation">
    <div class="sidebar-collapse">
        <div id="hover-menu"></div>
        <ul class="nav metismenu" id="side-menu">
            <li>
                <div class="logopanel">
                    <div class="profile-element">
                        <h2><a href="<spring:url value="/admin/" />">Repository</a></h2>
                    </div>
                    <div class="logo-element">Rep</div>
                </div>
            </li>
            <li>
                <div class="leftpanel-profile">
                    <div class="media-body profile-name">
                        <sec:authorize access="isAuthenticated()">
                            <h4 class="media-heading"><sec:authentication property="principal.username" /></h4>
                            dn: <sec:authentication property="principal.fullname" />
                            <span><sec:authentication property="principal.authorities"/></span>
                            <div><a href="<spring:url value="/login?logout" />" class="logout-btn"><i class="fa fa-sign-out"></i> Выйти</a></div>
                        </sec:authorize>
                    </div>
                </div>
            </li>
            <li>
                <div class="nano left-sidebar">
                    <div class="nano-content">
                        <ul class="nav nav-pills nav-stacked nav-inq">
                            <li <c:if test="${param.activePage == ''}"> class="active" </c:if>>
                                <a href="<spring:url value="/admin/" />"><i class="fa fa-home"></i> <span class="nav-label">Главная</span></a>
                            </li>
                            <li <c:if test="${param.activePage == 'catalog'}"> class="active" </c:if>>
                                <a href="<spring:url value="/catalog" />"><i class="fa fa-folder-open"></i> <span class="nav-label">Каталог</span></a>
                            </li>
                            <li <c:if test="${param.activePage == 'categoryTree'}"> class="active" </c:if>>
                                <a href="<spring:url value="/admin/categories" />"><i class="fa fa-sitemap"></i> <span class="nav-label">Категории</span></a>
                            </li>
                            <sec:authorize access="hasRole('ROLE_FR-ADMIN')">
                                <li <c:if test="${param.activePage == 'file'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/files" />"><i class="fa fa-file-text-o"></i> <span class="nav-label">Файлы</span></a>
                                </li>
                            </sec:authorize>
                            <sec:authorize access="!hasRole('ROLE_FR-ADMIN')">
                                <li <c:if test="${param.activePage == 'file'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/files" />"><i class="fa fa-file-text-o"></i> <span class="nav-label">Мои файлы</span></a>
                                </li>
                            </sec:authorize>
                            <li <c:if test="${param.activePage == 'unfilled-files'}"> class="active" </c:if>>
                                <a href="<spring:url value="/unfilled-files" />"><i class="fa fa-file"></i> <span class="nav-label">Незаполненные Файлы</span></a>
                            </li>
                            <li <c:if test="${param.activePage == 'fileLink'}"> class="active" </c:if>>
                                <a href="#"><i class="fa fa-link"></i> <span class="nav-label">Привязка файлов</span></a>
                            </li>
                            <sec:authorize access="hasRole('ROLE_FR-ADMIN')">
                                <li <c:if test="${param.activePage == 'property'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/admin/properties" />"><i class="fa fa-list-ul"></i> <span class="nav-label">Свойства файлов</span></a>
                                </li>
                                <li <c:if test="${param.activePage == 'user'}"> class="active" </c:if>>
                                    <a href="<spring:url value="/users" />"><i class="fa fa-user"></i> <span class="nav-label">Пользователи</span></a>
                                </li>
                                <li>
                                    <a href="#"><i class="fa fa-warning"></i> <span class="nav-label">Логи</span></a>
                                </li>
                            </sec:authorize>
                        </ul>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</nav>

<div id="page-wrapper" class="gray-bg">
    <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-lg-12">
                <div class="inqbox float-e-margins">
                    <div class="inqbox-content">
