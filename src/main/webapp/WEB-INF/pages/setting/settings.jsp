<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../init-settings_top.jsp" />

<h2>${pageTitle}</h2>

<div class="row">
    <div class="col-lg-12">
        <div class="inqbox float-e-margins">
            <form method="post" action="<spring:url value="/settings-save" />" class="form-horizontal">
                <div class="inqbox-title">
                    <h5>База данных</h5>
                </div>
                <div class="inqbox-content">
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Драйвер</label>
                        <div class="col-sm-8"><input type="text" name="dbDriver" value="${database.driver}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Адрес</label>
                        <div class="col-sm-8"><input type="text" name="dbUrl" value="${database.url}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Пользователь</label>
                        <div class="col-sm-8"><input type="text" name="dbUser" value="${database.user}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Пароль</label>
                        <div class="col-sm-8"><input type="text" name="dbPassword" value="${database.password}" class="form-control"></div>
                    </div>
                </div>
                <div class="inqbox-title">
                    <h5>Active Directory</h5>
                </div>
                <div class="inqbox-content">
                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            Не заданы настройки Active Directory
                        </div>
                        <div class="panel-body">
                            <c:if test="${!isFilled.adUrl}">
                                <p>Добавить системное свойство "ldap.url"</p>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="inqbox-content">
                    <button class="btn btn-primary" type="submit">Сохранить</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../init-settings_bottom.jsp" />