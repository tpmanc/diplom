<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="layout_top.jsp" />

<h1>${code} Error</h1>
<p>${exception.message}</p>

<jsp:include page="layout_bottom.jsp" />
