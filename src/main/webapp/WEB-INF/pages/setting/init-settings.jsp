<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/init-settings_top.jsp" />

<h2>${pageTitle}</h2>

<c:if test="${isNeedRestart != null && isNeedRestart == true}">
    <div class="alert alert-success">
        Настройки сохранены. Необходимо перезагрузить приложение!
    </div>
</c:if>

<div class="row">
    <div class="col-lg-12">
        <div class="inqbox float-e-margins">
            <form method="post" action="<spring:url value="/settings-save" />" class="form-horizontal">
                <div class="inqbox-title">
                    <h5>Пути для загрузки файлов</h5>
                </div>
                <div class="inqbox-content">
                    <div class="form-group <c:if test="${errors.get(\"catalogPath\") != null}"> has-error</c:if>">
                        <label class="col-sm-4 control-label">Путь для загрузки файлов каталога</label>
                        <div class="col-sm-8"><input type="text" name="catalogPath" required value="${model1.value}" class="form-control"></div>
                        <c:if test="${errors.get(\"catalogPath\") != null}">
                            <span class="help-block">
                                <div>${errors.get("catalogPath")}</div>
                            </span>
                        </c:if>
                    </div>
                    <div class="form-group <c:if test="${errors.get(\"requestPath\") != null}"> has-error</c:if>">
                        <label class="col-sm-4 control-label">Путь для загрузки файлов заявок</label>
                        <div class="col-sm-8"><input type="text" name="requestPath" required value="${model2.value}" class="form-control"></div>
                        <c:if test="${errors.get(\"requestPath\") != null}">
                            <span class="help-block">
                                <div>${errors.get("requestPath")}</div>
                            </span>
                        </c:if>
                    </div>
                </div>
                <div class="inqbox-title">
                    <h5>База данных</h5>
                </div>
                <div class="inqbox-content">
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Адрес</label>
                        <div class="col-sm-8"><input type="text" name="dbUrl" required value="${dbProperties.get("db.url")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Пользователь</label>
                        <div class="col-sm-8"><input type="text" name="dbUser" required value="${dbProperties.get("db.user")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Пароль</label>
                        <div class="col-sm-8"><input type="password" name="dbPass" value="${dbProperties.get("db.password")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Размер пула соединений</label>
                        <div class="col-sm-8"><input type="number" name="dbPool" required value="${dbProperties.get("db.poolSize")}" class="form-control"></div>
                    </div>
                </div>
                <div class="inqbox-title">
                    <h5>Active Directory</h5>
                </div>
                <div class="inqbox-content">
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Адрес сервера</label>
                        <div class="col-sm-8"><input type="text" name="ldapUrl" required value="${adProperties.get("ldap.url")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">DN менеджера</label>
                        <div class="col-sm-8"><input type="text" name="ldapManager" required value="${adProperties.get("ldap.manager-dn")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Пароль менеджера</label>
                        <div class="col-sm-8"><input type="password" name="ldapPassword" required value="${adProperties.get("ldap.manager-password")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Фильтр поиска пользователей</label>
                        <div class="col-sm-8"><input type="text" name="ldapUserFilter" required value="${adProperties.get("ldap.user-search-filter")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Путь, где искать группы</label>
                        <div class="col-sm-8"><input type="text" name="ldapGroupSearch" required value="${adProperties.get("ldap.group-search-base")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Фильтр поиска группы</label>
                        <div class="col-sm-8"><input type="text" name="ldapGroupFilter" required value="${adProperties.get("ldap.group-search-filter")}" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Аттрибут роли</label>
                        <div class="col-sm-8"><input type="text" name="ldapRole" required value="${adProperties.get("ldap.role-attribute")}" class="form-control"></div>
                    </div>
                </div>
                <div class="inqbox-content">
                    <button class="btn btn-primary" type="submit">Сохранить</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../layouts/init-settings_bottom.jsp" />