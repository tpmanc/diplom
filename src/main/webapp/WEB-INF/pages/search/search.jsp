<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="catalog" />
</jsp:include>


<h2>${pageTitle}</h2>

<br>

<div class="row">
    <div class="col-md-12" id="filesHolder">
        <c:forEach items="${files}" var="file" varStatus="itemStat">
            <c:set var="file" value="${file}" scope="request" />
            <jsp:include page="../file/_listing-element.jsp" />
        </c:forEach>
    </div>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />