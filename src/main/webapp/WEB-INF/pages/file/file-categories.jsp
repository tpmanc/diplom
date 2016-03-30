<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/file/file-category.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/files" />">Файлы</a></li>
    <li><a href="<spring:url value="/file-view?id=${fileId}" />">Просмотр файла</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<form action="<spring:url value="/file-categories-handler" />" method="post" class="form-horizontal">
    <input type="hidden" value="${fileId}" name="fileId">
    <div class="form-group">
        <label class="col-sm-2 control-label">Категории</label>
        <div class="col-sm-10">
            <select data-placeholder="Выберите категории" name="categoriesId[]" class="chosen-select" multiple id="categories">
                <c:forEach items="${categories}" var="category" varStatus="itemStat">
                    <option
                            value="${category.id}"
                            <c:forEach items="${fileCategories}" var="selected" varStatus="itemStat">
                                <c:if test="${selected.id == category.id}">
                                    selected="selected"
                                </c:if>
                            </c:forEach>
                            >${category.title}
                    </option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-2 col-sm-offset-2">
            <button class="btn btn-success" type="submit">Сохранить</button>
        </div>
    </div>
</form>

<jsp:include page="../layouts/layout_bottom.jsp" />