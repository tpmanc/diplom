<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="btn-group">
    <c:if test="${page > 1}">
        <a href="<spring:url value="/${pageUrl}?page=" />${page - 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-left"></i></a>
    </c:if>

    <c:forEach begin="1" end="${pageCount}" var="number">
        <a href="<spring:url value="/${pageUrl}?page=" />${number}"
           class="btn btn-white <c:if test="${page == number}">active</c:if>">${number}</a>
    </c:forEach>

    <c:if test="${page < pageCount}">
        <a href="<spring:url value="/${pageUrl}?page=" />${page + 1}" type="button" class="btn btn-white"><i class="fa fa-chevron-right"></i></a>
    </c:if>
</div>