<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<script>
    var fileTitleUrl = "<spring:url value="/admin/file-title-autocomplete" />";
</script>

<div class="row">
    <div class="col-lg-12">
        <div class="inqbox float-e-margins">
            <div class="inqbox-content">
                <div id="steps">
                    <h2>Загзурзка файлов</h2>
                    <div class="step-content">
                        <div class="text-center m-t-md">
                            <form id="my-awesome-dropzone" class="dropzone" action="<spring:url value="/admin/file-add-handler" />">
                                <div class="dropzone-previews"></div>
                                <button type="submit" class="btn btn-primary pull-right">Загрузить файлы</button>
                            </form>
                        </div>
                    </div>
                    <h2>Заполнение информации</h2>
                    <div class="step-content">
                        <div class="text-center m-t-md">
                            <form action="#" id="secondStep"></form>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>