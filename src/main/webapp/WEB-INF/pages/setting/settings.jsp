<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="settings" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/setting/init-settings.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<c:if test="${isNeedRestart != null && isNeedRestart == true}">
    <div class="alert alert-success">
        Настройки сохранены. Необходимо перезагрузить приложение!
    </div>
</c:if>

<form method="POST" action="<spring:url value="/settings-save" />">
    <input type="hidden" name="isFromPanel" value="1">
    <h2>Пути загрузки файлов</h2>
    <div class="form-group <c:if test="${errors.get(\"catalogPath\") != null}">has-error</c:if>">
        <label for="catalogPath">Путь для загрузки файлов каталога</label>
        <input type="text" class="form-control" name="catalogPath" required id="catalogPath" placeholder="" value="${model1.value}">
        <c:if test="${errors.get(\"catalogPath\") != null}">
            <span class="help-block">
                <div>${errors.get("catalogPath")}</div>
            </span>
        </c:if>
    </div>
    <div class="form-group <c:if test="${errors.get(\"requestPath\") != null}">has-error</c:if>">
        <label for="requestPath">Путь для загрузки файлов заявок</label>
        <input type="text" class="form-control" name="requestPath" required id="requestPath" placeholder="" value="${model2.value}">
        <c:if test="${errors.get(\"requestPath\") != null}">
            <span class="help-block">
                <div>${errors.get("requestPath")}</div>
            </span>
        </c:if>
    </div>

    <br>

    <h2>База данных</h2>
    <code>Файл: ${dbFilePath}</code>
    <br><br>

    <div class="form-group">
        <label for="dbUrl">Адрес</label>
        <input type="text" class="form-control" name="dbUrl" required id="dbUrl" value="${dbProperties.get("db.url")}">
    </div>
    <div class="form-group">
        <label for="dbUser">Пользователь</label>
        <input type="text" class="form-control" name="dbUser" required id="dbUser" value="${dbProperties.get("db.user")}">
    </div>
    <div class="form-group">
        <label for="dbPass">Пароль</label>
        <input type="password" class="form-control" name="dbPass" id="dbPass" value="${dbProperties.get("db.password")}">
    </div>
    <div class="form-group">
        <label for="dbPool">Размер пула соединений</label>
        <input type="text" class="form-control" name="dbPool" required id="dbPool" value="${dbProperties.get("db.poolSize")}">
    </div>

    <br>

    <h2>Active Directory</h2>
    <code>Файл: ${adFilePath}</code>
    <br><br>

    <div class="form-group">
        <label for="ldapUrl">Адрес сервера</label>
        <input type="text" class="form-control" name="ldapUrl" required id="ldapUrl" value="${adProperties.get("ldap.url")}">
    </div>
    <div class="form-group">
        <label for="ldapManager">DN менеджера</label>
        <input type="text" class="form-control" name="ldapManager" required id="ldapManager" value="${adProperties.get("ldap.manager-dn")}">
    </div>
    <div class="form-group">
        <label for="ldapPassword">Пароль менеджера</label>
        <input type="password" class="form-control" name="ldapPassword" required id="ldapPassword" value="${adProperties.get("ldap.manager-password")}">
    </div>
    <div class="form-group">
        <label for="ldapUserFilter">Фильтр поиска пользователей</label>
        <input type="text" class="form-control" name="ldapUserFilter" required id="ldapUserFilter" value="${adProperties.get("ldap.user-search-filter")}">
    </div>
    <div class="form-group">
        <label for="ldapGroupSearch">Путь, где искать группы</label>
        <input type="text" class="form-control" name="ldapGroupSearch" required id="ldapGroupSearch" value="${adProperties.get("ldap.group-search-base")}">
    </div>
    <div class="form-group">
        <label for="ldapGroupFilter">Фильтр поиска группы</label>
        <input type="text" class="form-control" name="ldapGroupFilter" required id="ldapGroupFilter" value="${adProperties.get("ldap.group-search-filter")}">
    </div>
    <div class="form-group">
        <label for="ldapRole">Аттрибут роли</label>
        <input type="text" class="form-control" name="ldapRole" required id="ldapRole" value="${adProperties.get("ldap.role-attribute")}">
    </div>
    <div class="form-group">
        <label for="ldapAdminGroup">Название группы администраторов</label>
        <input type="text" class="form-control" name="ldapAdminGroup" required id="ldapAdminGroup" value="${adProperties.get("ldap.admin")}">
    </div>
    <div class="form-group">
        <label for="ldapModeratorGroup">Название группы модераторов</label>
        <input type="text" class="form-control" name="ldapModeratorGroup" required id="ldapModeratorGroup" value="${adProperties.get("ldap.moderator")}">
    </div>

    <h2>Логи</h2>
    <code>Файл: ${logFilePath}</code>
    <br><br>

    <div class="form-group">
        <div class="i-checks">
            <label>
                <input type="checkbox" id="logFileEnable" <c:if test="${isLogFileEnabled}">checked="checked"</c:if> name="logFileEnable">
                <i></i> Включить логи в файл
            </label>
        </div>
    </div>
    <div id="logFileGroup">
        <div class="form-group <c:if test="${errors.get(\"logFilePath\") != null}"> has-error</c:if>">
            <label for="logFilePath">Путь для сохранения логов</label>
            <input type="text" <c:if test="${!isLogFileEnabled}">disabled="disabled"</c:if> name="logFilePath" id="logFilePath" required value="${logProperties.get("log4j.appender.fileRoll.File")}" class="form-control">
            <c:if test="${errors.get(\"logFilePath\") != null}">
                <span class="help-block">
                    <div>${errors.get("logFilePath")}</div>
                </span>
            </c:if>
        </div>
        <div class="form-group <c:if test="${errors.get(\"logFileCount\") != null}"> has-error</c:if>">
            <label for="logFileCount">Максимальное количество файлов</label>
            <input type="number" <c:if test="${!isLogFileEnabled}">disabled="disabled"</c:if> name="logFileCount" id="logFileCount" required value="${logProperties.get("log4j.appender.fileRoll.MaxFileSize")}" class="form-control">
            <c:if test="${errors.get(\"logFileCount\") != null}">
                <span class="help-block">
                    <div>${errors.get("logFileCount")}</div>
                </span>
            </c:if>
        </div>
        <div class="form-group <c:if test="${errors.get(\"logFileMaxSize\") != null}"> has-error</c:if>">
            <label for="logFileMaxSize">Максимальный размер файла (Мб)</label>
            <input type="number" <c:if test="${!isLogFileEnabled}">disabled="disabled"</c:if> name="logFileMaxSize" id="logFileMaxSize" required value="${logProperties.get("log4j.appender.fileRoll.MaxBackupIndex")}" class="form-control">
            <c:if test="${errors.get(\"logFileMaxSize\") != null}">
                <span class="help-block">
                    <div>${errors.get("logFileMaxSize")}</div>
                </span>
            </c:if>
        </div>
    </div>

    <div class="form-group">
        <div class="i-checks">
            <label>
                <input type="checkbox" id="logSyslogEnable" <c:if test="${isLogSyslogEnabled}">checked="checked"</c:if> name="logSyslogEnable">
                <i></i> Включить логи в Syslog
            </label>
        </div>
    </div>
    <div id="logSyslogGroup">
        <div class="form-group <c:if test="${errors.get(\"logSyslogHost\") != null}"> has-error</c:if>">
            <label for="logSyslogHost">Адрес Syslog сервера</label>
            <input type="text" <c:if test="${!isLogSyslogEnabled}">disabled="disabled"</c:if> name="logSyslogHost" id="logSyslogHost" required value="${logProperties.get("log4j.appender.SYSLOG.syslogHost")}" class="form-control">
            <c:if test="${errors.get(\"logSyslogHost\") != null}">
                <span class="help-block">
                    <div>${errors.get("logSyslogHost")}</div>
                </span>
            </c:if>
        </div>
        <div class="form-group <c:if test="${errors.get(\"logSyslogFacility\") != null}"> has-error</c:if>">
            <label for="logSyslogFacility">Категория записей в Syslog</label>
            <input type="text" <c:if test="${!isLogSyslogEnabled}">disabled="disabled"</c:if> name="logSyslogFacility" id="logSyslogFacility" required value="${logProperties.get("log4j.appender.SYSLOG.facility")}" class="form-control">
            <c:if test="${errors.get(\"logSyslogFacility\") != null}">
                <span class="help-block">
                    <div>${errors.get("logSyslogFacility")}</div>
                </span>
            </c:if>
        </div>
    </div>

    <button type="submit" class="btn btn-success">Сохранить</button>
</form>

<jsp:include page="../layouts/layout_bottom.jsp" />