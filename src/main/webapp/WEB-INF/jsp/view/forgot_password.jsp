<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 2/20/23
  Time: 2:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="container">
  <div class="row row-content">
    <div class="col">
      <div class="card">
        <div class="card-header card-header-single">Forgot password</div>
        <div class="card-body">
          <form action="/forgotPassword"  method="POST" style="max-width: 420px; margin: 0 auto;">
            <sec:csrfInput />

              <div>
                <p>Please enter the username or the email associated with your account. We will be sending a reset password link to your email.</p>
              </div>
              <div>
                <p>
                  <input type="text" name="email_username_input" class="form-control" placeholder="Enter your username or e-mail" required autofocus/>
                </p>
                <p class="text-center">
                  <input type="submit" value="Send" class="btn btn-primary" />
                </p>
              </div>

          </form>
        </div>
      </div>
    </div>
  </div>
</div>

