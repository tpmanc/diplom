<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="categoryTree" />
</jsp:include>

<spring:url value="/resources/js/pages/admin/category/categories.js" var="categoryTreeJs" />
<spring:url value="/admin/category/ajax-delete" var="categoryTreeDeleteUrl" />
<spring:url value="/admin/category/ajax-add-category" var="addCategoryToTreeUrl" />
<spring:url value="/admin/category/ajax-update-position" var="updatePosition" />
<spring:url value="/admin/category/ajax-rename" var="renameCategoryUrl" />
<script src="${categoryTreeJs}"></script>
<script>
    var deleteTreesUrl = '${categoryTreeDeleteUrl}';
    var addCategoryToTreeUrl = '${addCategoryToTreeUrl}';
    var updatePosition = '${updatePosition}';
    var renameCategoryUrl = '${renameCategoryUrl}';
    var trees = [
        <c:forEach items="${trees}" var="item" varStatus="itemStat">
            {
                id: "jst_${item.get("id")}",
                parent: "<c:choose><c:when test="${item.get(\"parent\") == \"0\"}">#</c:when><c:otherwise>jst_${item.get("parent")}</c:otherwise></c:choose>",
                text: "${item.get("title")}"
            }<c:if test="${!itemStat.last}">,</c:if>
        </c:forEach>
    ];
</script>

<h2>${pageTitle}</h2>

<ol class="breadcrumb">
    <li><a href="<spring:url value="/admin" />">Главная</a></li>
    <li class="active">
        <strong>${pageTitle}</strong>
    </li>
</ol>

<br>

<p>
    <button class="btn btn-success" id="addNewTree">Добавить дерево</button>
</p>

<div id="treesHolder">
</div>

<div class="modal inmodal" id="addCategoryModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content animated fadeIn">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">Добавить категорию</h4>
            </div>
            <div class="modal-body">
                <div class="form">
                    <div class="form-group">
                        <label for="categoryTitle">Название</label>
                        <input type="text" class="form-control" id="categoryTitle" placeholder="Название">
                        <span class="help-block"></span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <input type="button" id="submitNewCategory" class="btn btn-success" value="Добавить">
            </div>
        </div>
    </div>
</div>

<div class="modal inmodal" id="renameCategoryModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content animated fadeIn">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">Переименовать категорию</h4>
            </div>
            <div class="modal-body">
                <div class="form">
                    <div class="form-group">
                        <label for="categoryTitle">Название</label>
                        <input type="text" class="form-control" id="newCategoryTitle" placeholder="Название">
                    </div>
                    <input type="hidden" class="form-control" id="renameCategoryId">
                </div>
            </div>
            <div class="modal-footer">
                <input type="button" id="submitNewCategoryTitle" class="btn btn-success" value="Добавить">
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout_bottom.jsp" />