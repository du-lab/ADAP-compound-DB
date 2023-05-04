<%--
  Created by IntelliJ IDEA.
  User: gorle
  Date: 3/24/23
  Time: 1:34 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="organization_container" ${param.SHOW_ORGANIZATION ? '' : 'style="display: none;"' }>
  <%--@elvariable id="errorMessage" type="java.lang.String"--%>
  <%--@elvariable id="successMessage" type="java.lang.String"--%>
  <div>
    <div id="organization-failed" class="alert-danger" style="margin-bottom: 5px;">${errorMessage}</div>
    <div id="organization-success" class="alert-success" style="margin-bottom: 5px;">${successMessage}</div>
    <c:if test="${empty requestScope.members}">
      <p style="font-size:14px;">No users added.</p>
    </c:if>
  </div>
  <c:if test="${not empty requestScope.members}">
    <table id="organization_table" class="display dataTable" style="width: 100%;">
      <thead>
      <tr>
        <th>S.No</th>
        <th>Name</th>
        <th>Email</th>
        <th>Remove User</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${requestScope.members}" var="member" varStatus="loop">
        <tr>
          <td>${loop.index+1}</td>
          <td>${member.username}</td>
          <td>${member.email}</td>
          <td>
            <a id="organization_dialog" onclick="confirmDeleteDialog.show(
                    'Are you sure you want to remove user \'${member.username}\' from the organization?',
                    '${pageContext.request.contextPath}/account/organization/${member.username}/delete/');">
              <i class="material-icons" title="Delete">&#xE872;</i>
            </a>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </c:if>
</div>
<%--@elvariable id="organizationForm" type="org.dulab.adapcompounddb.site.controllers.forms.OrganizationForm"--%>
<form:form modelAttribute="organizationForm"
           action="${pageContext.request.contextPath}/account/fetchUserNamesForOrganization"
           method="POST">
  <div class="form-group row"
       style="justify-content: right;margin-right: unset; margin-top: 1rem;">
    <div class="col-md-4">
      <form:input type="text" class="form-control" placeholder="Enter Username or Email"
                  id="username"
                  path="username"/>
    </div>
    <button id="organizationSubmitButton"
            class="btn btn-primary"
            type="submit">
      Search Users
    </button>
  </div>
</form:form>

<c:if test="${requestScope.searchMembersList ne null and not empty requestScope.searchMembersList}">
  <div style="font-size:14px;">Search Results:</div>
  <form:form method="post" action="${pageContext.request.contextPath}/account/addUserToOrganization">
    <table id="organization_table_select" class="display dataTable" style="width: 100%;">
      <thead>
      <tr>
        <th>S.No</th>
        <th>Name</th>
        <th>Email</th>
        <th>Add User</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${requestScope.searchMembersList}" var="member" varStatus="loop">
        <tr>
          <td>${loop.index+1}</td>
          <td>${member.username}</td>
          <td>${member.email}</td>
          <td>
            <c:if test="${member.organizationId ne null}">
              <i class="material-icons" style="color:#03c703d4;" title="Already added">&#xe834;</i>
            </c:if>
            <c:if test="${member.organizationId eq null}">
              <input style="width: 24px;height: 20px;" type="checkbox" title="Click to add" value="${member.id}" name="selectedUsers"/>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <div class="form-group row"
         style="justify-content: right;margin-right: unset; margin-top: 1rem;">
      <button id="organizationAdd"
              class="btn btn-primary"
              type="submit">
        Send Invite
      </button>
    </div>
  </form:form>
</c:if>
