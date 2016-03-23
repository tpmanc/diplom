<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<h1>${pageTitle}</h1>

<select name="" id="">
    <c:forEach items="${categories}" var="category" varStatus="itemStat">
        <option value="${category.get("id")}">${category.get("title")}</option>
    </c:forEach>
</select>

<jsp:include page="../layout_bottom.jsp" />