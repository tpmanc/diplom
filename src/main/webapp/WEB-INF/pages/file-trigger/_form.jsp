<%@ page import="helpers.TriggerHelper" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<div class="well well-sm">
    <h3>Доступные переменные:</h3>
    <div><strong>{filePath}</strong> - Путь до файла</div>
    <h3>Ключи для доступа к свойствам:</h3>
    <div><strong><%= TriggerHelper.titleKey %></strong> - Название</div>
    <div><strong><%= TriggerHelper.versionKey %></strong> - Версия</div>
    <div><strong><%= TriggerHelper.descriptionKey %></strong> - Описание файла</div>
    <div><strong><%= TriggerHelper.copyrightKey %></strong> - Авторские права</div>
    <div><strong><%= TriggerHelper.authorKey %></strong> - Автор</div>
</div>

<form method="POST" action="<spring:url value="/file-trigger-handler" />">
    <c:if test="${trigger.id != null}">
        <input type="hidden" name="id" value="${trigger.id}">
    </c:if>
    <div class="form-group <c:if test="${errors.get(\"extension\") != null}">has-error</c:if>">
        <label for="extension">Название</label>
        <input type="text" class="form-control" name="extension" required id="extension" placeholder="Расширение" value="<c:if test="${trigger.extension != null}">${trigger.extension}</c:if>">
        <c:if test="${errors.get(\"extension\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"extension\")}" var="item" varStatus="itemStat">
                    <div>${item}</div>
                </c:forEach>
            </span>
        </c:if>
    </div>

    <div class="form-group <c:if test="${errors.get(\"command\") != null}">has-error</c:if>">
        <label for="command">Название</label>
        <input type="text" class="form-control" name="command" required id="command" placeholder="Команда" value="<c:if test="${trigger.command != null}">${trigger.command}</c:if>">
        <c:if test="${errors.get(\"command\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"command\")}" var="item" varStatus="itemStat">
                    <div>${item}</div>
                </c:forEach>
            </span>
        </c:if>
    </div>

    <div class="form-group <c:if test="${errors.get(\"regexp\") != null}">has-error</c:if>">
        <label for="regexp">Название</label>
        <input type="text" class="form-control" name="regexp" required id="regexp" placeholder="Регулярка" value="<c:if test="${trigger.regexp != null}">${trigger.regexp}</c:if>">
        <c:if test="${errors.get(\"regexp\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"regexp\")}" var="item" varStatus="itemStat">
                    <div>${item}</div>
                </c:forEach>
            </span>
        </c:if>
    </div>

    <button type="submit" class="btn btn-success">Сохранить</button>
</form>
