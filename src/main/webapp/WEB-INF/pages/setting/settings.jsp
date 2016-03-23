<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../init-settings_top.jsp" />

<h2>${pageTitle}</h2>

<c:if test="${isSaved != null}">
    <div class="panel panel-success">
        <div class="panel-heading">
            Настройки сохранены
        </div>
        <div class="panel-body">
            <p>Не забудьте перегазрузить сервер!</p>
        </div>
    </div>
</c:if>

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
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Адрес</label>
                        <div class="col-sm-8"><input type="text" name="ldapUrl" value="${activeDirectory.url}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">DN менеджера</label>
                        <div class="col-sm-8"><input type="text" name="ldapManagerDn" value="${activeDirectory.manager}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Пароль</label>
                        <div class="col-sm-8"><input type="text" name="ldapManagerPass" value="${activeDirectory.password}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Фильтр пользователей</label>
                        <div class="col-sm-8"><input type="text" name="ldapUserSearchFilter" value="${activeDirectory.userFilter}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Поиск групп</label>
                        <div class="col-sm-8"><input type="text" name="ldapGroupSearch" value="${activeDirectory.groupSearch}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Фильтр групп</label>
                        <div class="col-sm-8"><input type="text" name="ldapGroupSearchFilter" value="${activeDirectory.groupFilter}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Аттрибут роли</label>
                        <div class="col-sm-8"><input type="text" name="ldapRoleAttribute" value="${activeDirectory.roleAttribute}" class="form-control"></div>
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