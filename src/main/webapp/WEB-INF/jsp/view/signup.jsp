<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>--%>
<%--@elvariable id="signUpForm" type="org.dulab.site.controllers.SignUpForm"--%>
<%--@elvariable id="integTest" type="java.lang.Boolean"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script src="https://www.google.com/recaptcha/api.js"></script>
<script>
    function recaptchaCallback() {
        $('#submit').removeAttr('disabled');
    }
</script>

<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Sign-Up</div>
                <div class="card-body">
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-md-6 offset-md-3">
                                <p>All fields are required.</p>
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
                        <form:form method="POST" modelAttribute="signUpForm">
                            <c:if test="${errorMsg != null}">
                                <div class="row">
                                    <div class="col-md-8 offset-md-2">
                                        <p class="text-danger">${errorMsg}</p>
                                    </div>
                                </div>
                            </c:if>
                            <%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>"--%>
                            <c:if test="${validationErrors != null}">
                                <div class="row">
                                    <div class="col-md-8 offset-md-2">
                                        <c:forEach items="${validationErrors}" var="e">
                                            <p class="text-danger"><c:out value="${e.message}"/></p>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                            <div class="row form-group">
                                <form:label path="username"
                                            cssClass="col-md-3 offset-md-3 col-form-label">Username:</form:label>
                                <form:input path="username" cssClass="col-md-3 form-control" autofocus="autofocus"/>
                            </div>
                            <div class="row form-group">
                                <form:label path="email"
                                            cssClass="col-md-3 offset-md-3 col-form-label">E-mail address</form:label>
                                <form:input path="email" cssClass="col-md-3 form-control"/>
                            </div>
                            <div class="row form-group">
                                <form:label path="confirmedEmail"
                                            cssClass="col-md-3 offset-md-3 col-form-label">Confirm E-mail address</form:label>
                                <form:input path="confirmedEmail" cssClass="col-md-3 form-control"/>
                            </div>
                            <div class="row form-group">
                                <form:label path="password"
                                            cssClass="col-md-3 offset-md-3 col-form-label">Password:</form:label>
                                <form:password path="password" cssClass="col-md-3 form-control"/>
                            </div>
                            <div class="row form-group">
                                <form:label path="confirmedPassword"
                                            cssClass="col-md-3 offset-md-3 col-form-label">Confirm password:</form:label>
                                <form:password path="confirmedPassword" cssClass="col-md-3 form-control"/>
                            </div>
                            <div class="row form-group">
                                <form:label path="organization"
                                            cssClass="col-md-3 offset-md-3 col-form-label">Organization Account:</form:label>
                                <form:checkbox path="organization" cssClass="col-1 form-control"/>
                            </div>
                            <div class="row form-group">
                                <div class="col-md-6 offset-md-3">
                                    <form:errors path="*" element="div" cssClass="text-danger"/>
                                </div>
                            </div>
                            <c:if test="${!integTest}">
                                sdaf
                                <div class="g-recaptcha col-md-2 offset-md-6"
                                     data-sitekey="6LdY3V8hAAAAACkWkUd5G9xYtgnM9vwPvIPsQrWy" data-callback="recaptchaCallback"></div>
                                <br/>
                            </c:if>
                            <div class="row form-group">
                                <div class="col-md-2 offset-md-6">
                                    <input id="submit" name="submit" type="submit" class="btn btn-primary" value="Sign up"
                                            <c:if test="${!integTest}">
                                                <c:out value="disabled='disabled'"/>
                                            </c:if>
                                    />
                                </div>
                            </div>
                        </form:form>
                        <sec:csrfInput />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>