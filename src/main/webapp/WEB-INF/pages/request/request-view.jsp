<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<script src="<spring:url value="/resources/js/pages/request/request-view.js" />"></script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/" />">Главная</a></li>
    <li><a href="<spring:url value="/request-list" />">Заявки</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<c:if test="${isModerator == true}">
    <p>
        <a class="btn btn-success" href="<spring:url value="/request-accept?requestId=${requestModel.id}" />">Принять</a>
        <a class="btn btn-danger" href="<spring:url value="/request-cancel?requestId=${requestModel.id}" />">Отклонить</a>
        <a class="btn btn-danger" id="requestDeleteBtn" href="<spring:url value="/request-delete?requestId=${requestModel.id}" />">Удалить</a>
    </p>
</c:if>

<table class="table table-striped models-view">
    <tbody>
    <c:if test="${isModerator == true}">
        <tr>
            <td>Пользователь</td>
            <td><a href="<spring:url value="/user-view?id=${user.id}" />">${user.displayName}</a></td>
        </tr>
    </c:if>
    <tr>
        <td>Дата</td>
        <td>${requestDate}</td>
    </tr>
    <tr>
        <td>Статус</td>
        <td>
            <c:choose>
                <c:when test="${requestModel.status == 2}">
                    Принята
                </c:when>
                <c:when test="${requestModel.status == 3}">
                    Отклонена
                </c:when>
                <c:otherwise>
                    Ожидает
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td>Текст</td>
        <td>${requestModel.text}</td>
    </tr>
    <tr>
        <td>Файлы</td>
        <td>
            <c:forEach items="${files}" var="file" varStatus="itemStat">
                <span class="label">${file.fileName}</span>
            </c:forEach>
        </td>
    </tr>
    <c:if test="${!requestModel.comment.equals(\"\")}">
        <tr class="<c:if test="${requestModel.status == 3}">danger</c:if> <c:if test="${requestModel.status == 2}">success</c:if>">
            <td>Комментарий модератора</td>
            <td>
                ${requestModel.comment}
            </td>
        </tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="../layouts/layout_bottom.jsp" />