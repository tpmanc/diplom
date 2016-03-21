<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="catalog" />
</jsp:include>


<script src="<spring:url value="/resources/js/pages/catalog/catalog.js" />"></script>
<script>
    var loadFilesByCategory = '<spring:url value="/catalog/ajax-load-files" />';
    var categoryId = ${categoryId};
    var trees = [
        <c:forEach items="${trees}" var="item" varStatus="itemStat">
            {
                id: "jst_${item.get("id")}",
                parent: "<c:choose><c:when test="${item.get(\"parent\") == \"0\"}">#</c:when><c:otherwise>jst_${item.get("parent")}</c:otherwise></c:choose>",
                text: "${item.get("title")}"
                <c:if test="${item.get(\"id\") == categoryId}">,
                    state: {
                        opened: true,
                        selected: true
                    }
                </c:if>
            }<c:if test="${!itemStat.last}">,</c:if>
        </c:forEach>
    ];
</script>

<h2>${pageTitle}</h2>

<br>

<div class="row">
    <div class="col-md-4" id="treesHolder"></div>
    <div class="col-md-8" id="filesHolder">
        <c:forEach items="${categoryFiles}" var="file" varStatus="itemStat">
            <div class="file-box">
                <div class="file">
                    <a href="<spring:url value="/file-view?id=${file.id}" />">
                        <span class="corner"></span>
                        <div class="file-name">${file.title}
                            <br>
                            <small>Дата: ${file.date}</small>
                            <br>
                            <small>Загрузил: ${file.userDN}</small>
                        </div>
                    </a>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="../layout_bottom.jsp" />