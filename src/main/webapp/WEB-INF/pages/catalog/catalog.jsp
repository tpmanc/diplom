<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="catalog" />
</jsp:include>


<script src="<spring:url value="/resources/js/pages/catalog/catalog.js" />"></script>
<script>
    var trees = [
        <c:forEach items="${trees}" var="item" varStatus="itemStat">
            {
                id: "jst_${item.get("id")}",
                parent: "<c:choose><c:when test="${item.get(\"parent\") == \"0\"}">#</c:when><c:otherwise>jst_${item.get("parent")}</c:otherwise></c:choose>",
                text: "${item.get("title")}"
            }<c:if test="${!itemStat.last}">,</c:if>
        </c:forEach>
    ];
</script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<div id="treesHolder"></div>

<jsp:include page="../layout_bottom.jsp" />