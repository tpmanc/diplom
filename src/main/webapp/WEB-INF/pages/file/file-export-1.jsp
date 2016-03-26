<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layouts/layout_top.jsp">
    <jsp:param name="activePage" value="file" />
</jsp:include>

<script src="<spring:url value="/resources/js/pages/file/file-export-1.js" />"></script>
<script>
    var lineTemplate = '<tr>\
                <td class="param-name">\
                    <input type="text" name="names[]" placeholder="Название" required class="form-control" value="">\
                </td>\
                <td class="param-type">\
                    <div class="select">\
                        <select name="types[]">\
                            <option value="1">Значение</option>\
                            <option value="2">Команды</option>\
                        </select>\
                    </div>\
                </td>\
                <td class="param-commands">\
                    <div class="section">\
                        <label class="field">\
                            <textarea class="gui-textarea" required name="values[]" placeholder="Значение"></textarea>\
                        </label>\
                    </div>\
                </td>\
                <td class="param-delete">\
                    <a class="btn btn-white btn-bitbucket add-parameter">\
                        <i class="fa fa-times"></i>\
                        Удалить\
                    </a>\
                </td>\
            </tr>';
</script>

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
    <form action="<spring:url value="/file-export-handler" />" method="post" id="parameters" class="form-horizontal" name="params">
        <table class="table export-table">
            <thead>
                <tr>
                    <th>Название параметра</th>
                    <th>Тип</th>
                    <th>Значение</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
        <div class="form-group">
            <div class="col-sm-4">
                <a class="btn btn-white btn-bitbucket add-parameter">
                    <i class="fa fa-plus"></i>
                    Добавить параметр
                </a>
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-4">
                <button class="btn btn-success" type="submit">Далее</button>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../layouts/layout_bottom.jsp" />