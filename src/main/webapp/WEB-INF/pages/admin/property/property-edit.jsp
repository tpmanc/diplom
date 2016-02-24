<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<h1>${pageTitle}</h1>

<jsp:include page="_form.jsp" />

<jsp:include page="../layout_bottom.jsp" />