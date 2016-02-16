<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="layout_top.jsp">
    <jsp:param name="activePage" value="" />
</jsp:include>

<h1>Message : ${name}</h1>
Index Page

${names}

<jsp:include page="layout_bottom.jsp" />
