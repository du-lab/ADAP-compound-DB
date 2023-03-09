<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 3/2/23
  Time: 4:25 PM
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
        <div class="card-header card-header-single">Forgot username</div>
        <div class="card-body">
          <form action="/passwordRecovery/forgotUsername"  method="POST" style="max-width: 420px; margin: 0 auto;">
            <sec:csrfInput />

            <div>
              <p>Please enter the email associated with your account. We will be sending the username to your email.</p>
            </div>
            <div>
              <p>
                <input type="email" name="email" class="form-control" placeholder="Enter your e-mail" required autofocus/>
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
