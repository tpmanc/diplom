<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="property" />
</jsp:include>

<h1>${pageTitle}</h1>

<h3>${property.title}</h3>

<table class="table table-striped models-view">
    <tbody>
        <tr>
            <td>Id</td>
            <td>${property.id}</td>
        </tr>
        <tr>
            <td>Название</td>
            <td>${property.title}</td>
        </tr>
    </tbody>
</table>

<jsp:include page="../layout_bottom.jsp" />