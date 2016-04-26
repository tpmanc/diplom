<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp" />

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/file-view?id=${version.fileId}&versionId=${version.id}" />">Просмотр файла</a></li>
    <li><a href="<spring:url value="/file-export-template?versionId=${version.id}" />">Выбор шаблона</a></li>
    <li><a href="<spring:url value="/file-export?versionId=${version.id}" />">Шаг 1</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<div class="kform">
    <form action="<spring:url value="/file-export-handler-2" />" method="post" id="parameters" class="form-horizontal" name="params">
        <input type="hidden" name="versionId" value="${version.id}">
        <table class="table export-table">
            <thead>
                <tr>
                    <th>Название параметра</th>
                    <th>Значение</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${parameters.params}" var="item" varStatus="itemStat">
                    <tr>
                        <td>${item.name}</td>
                        <td>
                            <input type="hidden" name="names[]" value="${item.name}">
                            <c:choose>
                            <c:when test="${item.type == 2}">
                                <div class="select form-group <c:if test="${errors.get(item.name) != null}">has-error</c:if>">
                                    <select class="form-control" required name="values[]">\
                                        <c:forEach items="${item.variants}" var="value" varStatus="valueStat">
                                            <option value="${value}">${value}</option>
                                        </c:forEach>
                                    </select>
                                    <span class="help-block">
                                        <div>${errors.get(item.name)}</div>
                                    </span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="form-group <c:if test="${errors.get(item.name) != null}">has-error</c:if>">
                                    <input type="text" class="form-control disabled" name="values[]" value="${item.value}">
                                    <span class="help-block">
                                        <div>${errors.get(item.name)}</div>
                                    </span>
                                </div>
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