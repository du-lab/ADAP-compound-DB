<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 2/20/23
  Time: 3:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="container">
  <div class="row row-content">
    <div class="col">
      <div class="card">
        <div class="card-header card-header-single">Reset Password</div>
        <div class="card-body">
          <form  action ="/resetPassword" method="post" style="max-width: 420px; margin: 0 auto;">
            <sec:csrfInput />
            <div class="row form-group">
              <label for="password" cssClass="col-md-3 offset-md-3 col-form-label">New Password:</label>
              <input type="password" id="password" name="password" cssClass="col-md-3 form-control"/>
            </div>
            <div class="row form-group">
              <label for="confirmPassword" cssClass="col-md-3 offset-md-3 col-form-label">Confirm Password:</label>
              <input type="password" id="confirmPassword" name="confirmPassword" cssClass="col-md-3 form-control"/>
            </div>
            <div>
              <input type="submit" value="Reset Password" class="btn btn-primary"/>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
