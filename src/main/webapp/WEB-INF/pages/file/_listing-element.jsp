<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<div class="file-box">
    <div class="file">
        <a href="<spring:url value="/file-view?id=${file.id}&versionId=${file.versionId}" />">
            <span class="corner"></span>
            <div class="file-name">${file.title}
                <br>
                <small>Дата: ${file.date}</small>
                <br>
                <small>Загрузил: ${file.userDN}</small>
            </div>
        </a>
    </div>
</div>