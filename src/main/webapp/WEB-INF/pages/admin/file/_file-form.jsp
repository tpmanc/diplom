<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<div class="row">
    <div class="col-lg-12">
        <div class="inqbox float-e-margins">
            <div class="inqbox-title border-top-success">
                <h5>Dropzone Area</h5>
            </div>
            <div class="inqbox-content">
                <form id="my-awesome-dropzone" class="dropzone" action="<spring:url value="/admin/file-add-handler" />">
                    <div class="dropzone-previews"></div>
                    <button type="submit" class="btn btn-primary pull-right">Загрузить файлы</button>
                </form>
            </div>
        </div>
    </div>
</div>