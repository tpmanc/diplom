<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/file-view?id=${version.fileId}&versionId=${version.id}" />">Просмотр файла</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<c:if test="${error}">
    <div class="alert alert-danger">
        Необходимо выбрать один из шаблонов или создать новый!
    </div>
</c:if>

<div class="kform">
    <form action="<spring:url value="/file-export-template-handler" />" method="post" id="form" class="form-horizontal" name="params">
        <input type="hidden" name="versionId" value="${version.id}">
        <div class="select">
            <h3>Использовать шаблон</h3>

            <select name="template">
                <option value="0"> </option>
                <c:forEach items="${templates}" var="item" varStatus="itemStat">
                    <option <c:if test="${savedParameters.templateId == item.id}">selected="selected"</c:if> value="${item.id}">${item.title}</option>
                </c:forEach>
            </select>
        </div>
        <br><br><br><br>
        <div <c:if test="${titleError != null}">class="has-error" </c:if>
            <h3>Добавить шаблон</h3>
            <input type="text" name="templateTitle" placeholder="Название шаблона" class="form-control" value="">
            <c:if test="${titleError != null}">
                    <span class="help-block">
                        <div>${titleError}</div>
                    </span>
            </c:if>
        </div>
        <br><br>
        <div class="form-group">
            <div class="col-sm-4">
                <button class="btn btn-success" id="exportTemplateSelectBtn" type="submit">Далее</button>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />