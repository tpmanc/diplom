<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="category" />
</jsp:include>

<spring:url value="/resources/js/pages/category/category-add.js" var="categoryAdd" />
<script src="${categoryAdd}"></script>

<h1>${pageTitle}</h1>

<form method="POST" action="<spring:url value="/category-add-handler" />">
    <div class="form-group <c:if test="${errors.get(\"title\") != null}">has-error</c:if>">
        <label for="title">Название</label>
        <input type="text" class="form-control" name="title" id="title" placeholder="Название">
        <c:if test="${errors.get(\"title\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"title\")}" var="item" varStatus="itemStat">
                    ${item}
                </c:forEach>
            </span>
        </c:if>
    </div>

    <div class="checkbox">
        <label>
            <input type="checkbox" name="isEnabled"> Включена
        </label>
    </div>

    <button type="submit" class="btn btn-success">Добавить</button>
</form>

<jsp:include page="../layout_bottom.jsp" />