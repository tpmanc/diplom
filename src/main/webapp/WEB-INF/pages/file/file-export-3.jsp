<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/file-view?id=${version.fileId}&versionId=${version.id}" />">Просмотр файла</a></li>
    <li><a href="<spring:url value="/file-export-template?versionId=${version.id}" />">Выбор шаблона</a></li>
    <li><a href="<spring:url value="/file-export?versionId=${version.id}" />">Шаг 1</a></li>
    <li><a href="<spring:url value="/file-export-2?versionId=${version.id}" />">Шаг 2</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<div class="well well-sm">
    <h3>Доступные переменные:</h3>
    <div><strong>{title}</strong> = ${file.title}</div>
    <div><strong>{version}</strong> = ${version.version}</div>
    <c:forEach items="${parameters.params}" var="item" varStatus="itemStat">
        <div><strong>{${item.name}}</strong> = ${item.value}</div>
    </c:forEach>
</div>

<div class="kform">
    <form action="<spring:url value="/file-export-handler-3" />" method="post" id="parameters" class="form-horizontal" name="params">
        <input type="hidden" name="versionId" value="${version.id}">

        <div class="select">
            <select class="interpreter-select" name="commandsInterpreter">\
                <option value="0">Linux</option>
                <option value="1" <c:if test="${parameters.finalCommandInterpreter == 1}">selected</c:if>>cmd</option>
                <option value="2" <c:if test="${parameters.finalCommandInterpreter == 2}">selected</c:if>>PowerShell</option>
                <option value="3" <c:if test="${parameters.finalCommandInterpreter == 3}">selected</c:if>>JScript</option>
                <option value="4" <c:if test="${parameters.finalCommandInterpreter == 4}">selected</c:if>>VBS</option>
            </select>
        </div>

        <br><br>

        <div class="section <c:if test="${errors.get(\"commands\") != null}">has-error</c:if>">
            <label class="field prepend-icon">
                <textarea class="gui-textarea" required name="commands" id="commands" placeholder="Комманды">${parameters.finalCommand}</textarea>
                <label for="commands" class="field-icon">
                    <i class="fa fa-terminal"></i>
                </label>
                <c:if test="${errors.get(\"commands\") != null}">
                    <span class="help-block">
                        <div>${errors.get("commands")}</div>
                    </span>
                </c:if>
            </label>
        </div>

        <div class="form-group">
            <div class="col-sm-4">
                <button class="btn btn-success" type="submit">Выполнить</button>
            </div>
        </div>
    </form>
</div>

<c:if test="${result != null}">
    <h3>Результат выполнения</h3>
    <pre>${result}</pre>
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />