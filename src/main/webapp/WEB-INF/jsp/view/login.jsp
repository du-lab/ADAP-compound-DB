<%--@elvariable id="logInForm" type="org.dulab.site.controllers.LogInForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Log-in</div>
                <div class="card-body">
                    <div class="row row-content">
                        <div class="col-md-8 offset-md-2 text-center">
                            You must log in to submit new mass spectra to the Knowledgebase.
                        </div>
                    </div>
                    <c:if test="${param['loginFailed']}">
                        <div class="row">
                            <div class="col-md-8 offset-md-2 text-danger">
                                The username and password you entered are not correct. Please try again.
                            </div>
                        </div>
                    </c:if>
                    <%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>"--%>
                    <c:if test="${validationErrors != null}">
                        <div class="row">
                            <div class="col-md-8 offset-md-2 text-danger">
                                <c:forEach items="${validationErrors}" var="error">
                                    <li><c:out value="${error.message}"/></li>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>

                    <div class="row row-content">
                        <div class="container">
                            <form:form method="POST" action="${pageContext.request.contextPath}/j_spring_security_check"
                                       modelAttribute="logInForm">
                                <div class="row form-group">
                                    <form:label path="username"
                                                cssClass="col-md-3 offset-md-3 col-form-label">Username:</form:label>
                                    <form:input path="username" autofocus="autofocus" cssClass="col-md-3 form-control"/>
                                    <form:errors path="username" cssClass="text-danger"/>
                                </div>
                                <div class="row form-group">
                                    <form:label path="password"
                                                cssClass="col-md-3 offset-md-3 col-form-label">Password:</form:label>
                                    <form:password path="password" cssClass="col-md-3 form-control"/>
                                    <form:errors path="password" cssClass="text-danger"/>
                                </div>
                                <div class="row">
                                    <div class="col-md-2 offset-md-6">
                                        <input class="btn btn-primary" name="submit" type="submit" value="Log in"/>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/forgotForm" >Forgot Password</a>
                                </div>

                            </form:form>
                            <sec:csrfInput />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Sign-Up</div>
                <div class="card-body">
                    <div class="row row-content justify-content-center">
                        <div class="col-auto">If you are not registered yet, please do it now:</div>
                    </div>
                    <div class="row row-content justify-content-center">
                        <div class="col-auto">
                            <a id="registerButton" href="<c:url value="/signup"/>" class="btn btn-secondary">Register</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<%--    <div class="row row-content">--%>
<%--        <div class="col">--%>
<%--            <div class="card">--%>
<%--                <div class="card-header card-header-single">Forgot Password</div>--%>
<%--                <div class="card-body">--%>
<%--                    <div class="row row-content justify-content-center">--%>
<%--                        <div class="col-auto">If you are not registered yet, please do it now:</div>--%>
<%--                    </div>--%>
<%--                    <div class="row row-content justify-content-center">--%>
<%--                        <div class="col-auto">--%>
<%--                            <a id="registerButton" href="<c:url value="/signup"/>" class="btn btn-secondary">Register</a>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
</div>