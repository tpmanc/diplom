<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li><a href="<spring:url value="/admin/files" />">Файлы</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<div class="kform">
    <form action="<spring:url value="/file-export-handler2" />" method="post" id="parameters" class="form-horizontal" name="params">
        <table class="table export-table">
            <thead>
                <tr>
                    <th>Название параметра</th>
                    <th>Значение</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${parameters}" var="item" varStatus="itemStat">
                    <tr>
                        <td>${item.name}</td>
                        <td>
                            <input type="hidden" name="name[]" value="${item.name}">
                            <c:choose>
                            <c:when test="${item.type == 2}">
                                <select required name="value[]" id="">
                                    <c:forEach items="${item.variants}" var="value" varStatus="valueStat">
                                        <option value="${value}">${value}</option>
                                    </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <input type="text" disabled name="value[]" value="${item.value}">
                            </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="form-group">
            <div class="col-sm-4">
                <button class="btn btn-success" type="submit">Далее</button>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />