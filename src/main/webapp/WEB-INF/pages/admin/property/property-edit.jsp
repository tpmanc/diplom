<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<h1>${pageTitle}</h1>

<form method="POST" action="<spring:url value="/property-edit-handler" />">
    <input type="hidden" value="${property.id}" name="id" />
    <div class="form-group <c:if test="${errors.get(\"title\") != null}">has-error</c:if>">
        <label for="title">Название</label>
        <input type="text" class="form-control" name="title" id="title" placeholder="Название" value="${property.title}">
        <c:if test="${errors.get(\"title\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"title\")}" var="item" varStatus="itemStat">
                    ${item}
                </c:forEach>
            </span>
        </c:if>
    </div>

    <button type="submit" class="btn btn-success">Сохранить</button>
</form>

<jsp:include page="../layout_bottom.jsp" />