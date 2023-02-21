<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 2/20/23
  Time: 3:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="container">
  <div class="row row-content">
    <div class="col">
      <div class="card">
        <div class="card-header card-header-single">Reset Password</div>
        <div class="card-body">
          <div class="container-fluid">
            <div class="row row-content">
              <div class="col-md-6 offset-md-3">
                <p>Please fill out the information below to reset your current password.</p>
                <p>Password should match the pattern:</p>
                <ul>
                  <li>at least one digit: 0-9</li>
                  <li>at least one lower case letter: a-z</li>
                  <li>at least one upper case letter: A-Z</li>
                  <li>at least one special character: @, #, $, %, ^, &, +, =</li>
                  <li>no whitespaces</li>
                  <li>at least eight characters long</li>
                </ul>
              </div>
            </div>
            <form:form method="POST" modelAttribute="resetPassForm" action="/resetPassword">
              <sec:csrfInput />
              <c:if test="${errorMsg != null}">
                <div class="row">
                  <div class="col-md-6 offset-md-6">
                    <p class="text-danger">${errorMsg}</p>
                  </div>
                </div>
              </c:if>
              <%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>--%>
              <c:if test="${validationErrors != null}">
                <div class="row">
                  <div class="col-md-6 offset-md-6">
                    <c:forEach items="${validationErrors}" var="e">
                      <p class="text-danger"><c:out value="${e.message}"/></p>
                    </c:forEach>
                  </div>
                </div>
              </c:if>
              <div class="row form-group">
                <form:label path="newPass" cssClass="col-md-3 offset-md-3 col-form-label">New Password:</form:label>
                <form:password path="newPass" cssClass="col-md-3 form-control"/>
              </div>
              <div class="row form-group">
                <form:label path="confirmedNewPass" cssClass="col-md-3 offset-md-3 col-form-label">ReEnter Password:</form:label>
                <form:password path="confirmedNewPass" cssClass="col-md-3 form-control"/>
              </div>
              <div class="row form-group">
                <div class="col-md-6 offset-md-3">
                  <form:errors path="*" element="div" cssClass="text-danger"/>
                </div>
              </div>
              <div class="row form-group">
                <div class="col-md-6 offset-md-6">
                  <input type="submit" class="btn btn-primary" value="Change"/>
                </div>
              </div>
            </form:form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
