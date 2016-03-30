<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<form method="POST" action="<spring:url value="/property-handler" />">
    <c:if test="${property.id != null}">
        <input type="hidden" name="id" value="${property.id}">
    </c:if>
    <div class="form-group <c:if test="${errors.get(\"title\") != null}">has-error</c:if>">
        <label for="title">Название</label>
        <input type="text" class="form-control" name="title" id="title" placeholder="Название" value="<c:if test="${property.title != null}">${property.title}</c:if>">
        <c:if test="${errors.get(\"title\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"title\")}" var="item" varStatus="itemStat">
                    <div>${item}</div>
                </c:forEach>
            </span>
        </c:if>
    </div>

    <button type="submit" class="btn btn-success">Сохранить</button>
</form>
