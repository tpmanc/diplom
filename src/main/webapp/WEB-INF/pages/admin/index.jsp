<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../layout_top.jsp">
    <jsp:param name="activePage" value="" />
</jsp:include>

<div class="row">
    <div class="col-lg-8">
        <div class="row">
            <div class="col-lg-6">
                <div class="widget style1 navy-bg">
                    <div class="row">
                        <div class="col-xs-4">
                            <i class="fa fa-folder-open fa-5x"></i>
                        </div>
                        <div class="col-xs-8 text-right">
                            <span> Категорий </span>
                            <h2 class="font-bold">${categoryCount}</h2>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="widget style1 red-bg">
                    <div class="row">
                        <div class="col-xs-4">
                            <i class="fa fa-file fa-5x"></i>
                        </div>
                        <div class="col-xs-8 text-right">
                            <span> Файлов </span>
                            <h2 class="font-bold">${fileCount}</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout_bottom.jsp" />
