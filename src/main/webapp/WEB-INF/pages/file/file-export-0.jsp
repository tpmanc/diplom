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
    <form action="<spring:url value="/file-export-template-handler" />" method="post" class="form-horizontal" name="params">
        <input type="hidden" name="versionId" value="${version.id}">
        <div class="select">
            Использовать шаблон

            <select name="template">
                <c:forEach items="${templates}" var="item" varStatus="itemStat">
                    <option value="${item.id}">${item.title}</option>
                </c:forEach>
            </select>
        </div>
        <br>
        <div>
            <label>Добавить шаблон</label>
            <input type="text" name="templateTitle" placeholder="Название шаблона" class="form-control" value="">
        </div>

        <div class="form-group">
            <div class="col-sm-4">
                <button class="btn btn-success" type="submit">Далее</button>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />