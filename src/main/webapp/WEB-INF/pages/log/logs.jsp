<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="log" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/log/logs.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<c:if test="${status == true}">
    <div class="alert alert-success">Логи очищены</div>
</c:if>

<form action="<spring:url value="/logs-clear" />" method="post">
    <button class="btn btn-danger" type="submit" id="clearLogsBtn">Очистить логи</button>
</form>

<hr>

<form action="<spring:url value="/logs" />" method="get" class="form-horizontal">
    <div class="form-group">
        <label class="col-sm-2 control-label" for="levelSelect">Выберите уровень</label>
        <div class="col-sm-3">
            <select id="levelSelect" class="form-control" name="level">
                <option value=""> - </option>
                <option <c:if test="${currentLevel.equals(\"info\")}"> selected="selected" </c:if> value="info">info</option>
                <option <c:if test="${currentLevel.equals(\"warning\")}"> selected="selected" </c:if> value="warning">warning</option>
                <option <c:if test="${currentLevel.equals(\"error\")}"> selected="selected" </c:if> value="error">error</option>
            </select>
        </div>
    </div>
</form>

<table class="table table-striped models-view">
    <thead>
    <tr>
        <th>Id</th>
        <th>Дата</th>
        <th>Пользователь</th>
        <th>Действие</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${logs}" var="log" varStatus="itemStat">
        <tr class="
                <c:choose>
                    <c:when test="${log.level.equals(\"info\")}">info</c:when>
                    <c:when test="${log.level.equals(\"warning\")}">warning</c:when>
                    <c:when test="${log.level.equals(\"error\")}">danger</c:when>
                </c:choose>">
            <td>${log.id}</td>
            <td>${log.stringDate}</td>
            <td>${log.username}</td>
            <td>${log.message}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${pageCount > 1}">
    <div class="btn-group">
        <c:if test="${page > 1}">
            <a href="<spring:url value="/logs?page=" />${page - 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-left"></i></a>
        </c:if>

        <c:forEach begin="1" end="${pageCount}" var="number">
            <a href="<spring:url value="/logs?page=" />${number}"
               class="btn btn-white <c:if test="${page == number}">active</c:if>">${number}</a>
        </c:forEach>

        <c:if test="${page < pageCount}">
            <a href="<spring:url value="/logs?page=" />${page + 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-right"></i></a>
        </c:if>
    </div>
</c:if>

<jsp:include page="../layouts/layout_bottom.jsp" />