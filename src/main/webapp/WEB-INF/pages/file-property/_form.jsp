<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script src="<spring:url value="/resources/js/pages/file/file-property-form.js" />"></script>

<form action="<spring:url value="/admin/file-property-handler" />" method="post" class="form-horizontal">
    <c:choose>
        <c:when test="${fileProperty != null}">
            <input type="hidden" name="id" value="${fileProperty.id}">
            <input type="hidden" name="propertyId" value="${fileProperty.propertyId}">
            <input type="hidden" name="fileId" value="${fileProperty.fileId}">
        </c:when>
        <c:otherwise>
            <input type="hidden" name="fileId" value="${file.id}">
        </c:otherwise>
    </c:choose>

    <div class="form-group <c:if test="${errors.get(\"propertyId\") != null}">has-error</c:if>">
        <label class="col-sm-2 control-label">Свойство</label>
        <div class="col-sm-10">
            <c:choose>
                <c:when test="${fileProperty != null}">
                    <input type="text" class="form-control" required value="${fileProperty.title}" disabled="disabled">
                </c:when>
                <c:otherwise>
                    <select id="propertySelector" name="propertyId" required data-placeholder="Выберите свойство">
                        <c:forEach items="${properties}" var="item" varStatus="itemStat">
                            <option value="${item.id}" <c:if test="${item.id == selectedProperty}"> selected="selected" </c:if>>${item.title}</option>
                        </c:forEach>
                    </select>
                </c:otherwise>
            </c:choose>

            <c:if test="${errors.get(\"propertyId\") != null}">
                <span class="help-block">
                    <c:forEach items="${errors.get(\"propertyId\")}" var="item" varStatus="itemStat">
                        <div>${item}</div>
                    </c:forEach>
                </span>
            </c:if>
        </div>
    </div>

    <div class="form-group <c:if test="${errors.get(\"value\") != null}">has-error</c:if>">
        <label class="col-sm-2 control-label">Значение</label>
        <div class="col-sm-10">
            <input type="text" name="value" required class="form-control" value="${fileProperty.value}">
            <c:if test="${errors.get(\"value\") != null}">
            <span class="help-block">
                <c:forEach items="${errors.get(\"value\")}" var="item" varStatus="itemStat">
                    <div>${item}</div>
                </c:forEach>
            </span>
            </c:if>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-4 col-sm-offset-2">
            <button class="btn btn-success" type="submit">Сохранить</button>
        </div>
    </div>
</form>
