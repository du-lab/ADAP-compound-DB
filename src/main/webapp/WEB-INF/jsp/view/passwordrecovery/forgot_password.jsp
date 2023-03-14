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
<script src="https://www.google.com/recaptcha/api.js"></script>
<script>
  function recaptchaCallback() {
    $('#submitBtn').removeAttr('disabled');
  }
</script>
<div class="container">
  <div class="row row-content">
    <div class="col">
      <div class="card">
        <div class="card-header card-header-single">Forgot password</div>
        <div class="card-body">
          <form action="/passwordRecovery/forgotPassword"  method="POST" style="max-width: 420px; margin: 0 auto;">
            <sec:csrfInput />

              <div>
                <p>Please enter the username or the email associated with your account. We will be sending a reset password link to your email.</p>
              </div>
              <div>
                <p>
                  <input type="text" name="email_username_input" class="form-control" placeholder="Enter your username or e-mail" required autofocus/>
                </p>
                  <div id="submit" class="g-recaptcha col-md-8 offset-md-1" data-callback="recaptchaCallback"
                       data-sitekey="6LdY3V8hAAAAACkWkUd5G9xYtgnM9vwPvIPsQrWy"></div>
                <p class="text-center">
                  <input type="submit" id="submitBtn" value="Send" class="btn btn-primary" disabled="disabled"/>
                </p>
              </div>

          </form>
        </div>
      </div>
    </div>
  </div>
</div>

