<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="categoryTree" />
</jsp:include>

<spring:url value="/resources/js/pages/admin/category-tree/category-trees.js" var="categoryTreeJs" />
<spring:url value="/category-tree/ajax-save" var="categoryTreeSaveUrl" />
<spring:url value="/category-tree/ajax-delete" var="categoryTreeDeleteUrl" />
<spring:url value="/category-tree/ajax-add-category" var="addCategoryToTreeUrl" />
<script src="${categoryTreeJs}"></script>
<script>
    var saveTreesUrl = '${categoryTreeSaveUrl}';
    var deleteTreesUrl = '${categoryTreeDeleteUrl}';
    var addCategoryToTreeUrl = '${addCategoryToTreeUrl}';
    var trees = [
        <c:forEach items="${trees}" var="item" varStatus="itemStat">
            {id: "${item.get("treeId")}", parent: "${item.get("parent")}" , text: "${item.get("title")}"}<c:if test="${!itemStat.last}">,</c:if>
        </c:forEach>
    ];
</script>

<h1>${pageTitle}</h1>

<p>
    <button class="btn btn-success" id="addNewTree">Добавить дерево</button>
</p>

<div id="treesHolder">
</div>

<div style="display: none;">
    <div class="box-modal" id="addCategoryModal">
        <div class="box-modal_close arcticmodal-close">закрыть</div>
        <form action="#">
            <div class="form-group">
                <label for="categoryTitle">Название</label>
                <input type="text" class="form-control" id="categoryTitle" placeholder="Название">
            </div>
            <input type="button" id="submitNewCategory" class="btn btn-success" value="Добавить">
        </form>
    </div>
</div>

<jsp:include page="../layout_bottom.jsp" />