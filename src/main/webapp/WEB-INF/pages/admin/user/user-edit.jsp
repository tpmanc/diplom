<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:include page="../layout_top.jsp" />

<ol class="breadcrumb">
    <li><a href="<spring:url value="/index.html" />">Home</a></li>
    <li><a href="<spring:url value="/users.html" />">Users</a></li>
    <li class="active">User ${user.id}</li>
</ol>

<h1>User ${user.id}</h1>

<form class="form-horizontal">
    <div class="form-group">
        <label for="username" class="col-sm-2 control-label">Username</label>
        <div class="col-sm-10">
            <input type="text" name="username" class="form-control" id="username" required placeholder="Username" value="${user.username}">
        </div>
    </div>

    <div class="form-group">
        <label for="role" class="col-sm-2 control-label">Role</label>
        <div class="col-sm-10">
            <input type="text" name="role" class="form-control" id="role" required placeholder="Role" value="${user.role}">
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <div class="checkbox">
                <label>
                    <input type="checkbox"> Is Enabled
                </label>
            </div>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-success">Save</button>
        </div>
    </div>
</form>

<jsp:include page="../layout_bottom.jsp" />
