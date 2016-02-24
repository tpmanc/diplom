<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="navbar-default navbar-static-side fixed-menu" role="navigation">
    <div class="sidebar-collapse">
        <div id="hover-menu"></div>
        <ul class="nav metismenu" id="side-menu">
            <li>
                <div class="logopanel" style="margin-left: 0px; z-index: 99999">
                    <div class="profile-element">
                        <h2><a href="index.html">Repository</a></h2>
                    </div>
                    <div class="logo-element">Rep</div>
                </div>
            </li>
            <li>
                <div class="leftpanel-profile">
                    <div class="media-body profile-name" style="white-space: nowrap;">
                        <h4 class="media-heading">test@test.info</h4>
                        <span>test test</span>
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
                            <li <c:if test="${param.activePage == 'categoryTree'}"> class="active" </c:if>>
                                <a href="<spring:url value="/admin/categories" />"><i class="fa fa-sitemap"></i> <span class="nav-label">Категории</span></a>
                            </li>
                            <li <c:if test="${param.activePage == 'file'}"> class="active" </c:if>>
                                <a href="<spring:url value="/admin/files" />"><i class="fa fa-file"></i> <span class="nav-label">Файлы</span></a>
                            </li>
                            <li <c:if test="${param.activePage == 'fileLink'}"> class="active" </c:if>>
                                <a href="#"><i class="fa fa-link"></i> <span class="nav-label">Привязка файлов</span></a>
                            </li>
                            <li <c:if test="${param.activePage == 'property'}"> class="active" </c:if>>
                                <a href="<spring:url value="/admin/properties" />"><i class="fa fa-list-ul"></i> <span class="nav-label">Свойства файлов</span></a>
                            </li>
                            <li>
                                <a href="<spring:url value="/admin/users" />"><i class="fa fa-user"></i> <span class="nav-label">Пользователи</span></a>
                            </li>
                            <li>
                                <a href="#"><i class="fa fa-warning"></i> <span class="nav-label">Логи</span></a>
                            </li>
                        </ul>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</nav>

<div id="page-wrapper" class="gray-bg">
    <div id="header">
        <nav class="navbar navbar-fixed-top white-bg show-menu-full" id="nav" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <a class="navbar-minimalize minimalize-styl-2 btn" href="javascript:void(0)"><i class="fa fa-bars" style="font-size:27px;"></i> </a>
            </div>
            <ul class="nav navbar-top-links navbar-right">
                <li class="dropdown pull-right">
                    <a href="#">
                        <span class="pl15"> Перейти на сайт</span>
                        <i class="fa fa-external-link"></i>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
    <div style="clear: both; height: 61px;"></div>
    <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-lg-12">
                <div class="inqbox float-e-margins">
                    <div class="inqbox-content">
