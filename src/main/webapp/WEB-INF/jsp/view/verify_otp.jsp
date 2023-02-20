<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 2/20/23
  Time: 3:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="container">
  <div class="row row-content">
    <div class="col">
      <div class="card">
        <div class="card-header card-header-single">Verify otp</div>
        <div class="card-body">
          <form  action ="/send_otp" method="post" style="max-width: 420px; margin: 0 auto;">
            <div class="border border-secondary rounded p-3">
              <div>
                <p>We have sent OTP to your email.</p>
              </div>
              <div>
                <p>
                  <input type="email" name="email" class="form-control" placeholder="Enter OTP here" required autofocus/>
                </p>
                <p class="text-center">
                  <input type="submit" value="Verify" class="btn btn-primary" />
                </p>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
