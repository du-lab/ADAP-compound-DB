<%--@elvariable id="signUpForm" type="org.dulab.site.controllers.SignUpForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>
<head>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
    <script src="/resources/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>

</head>
<!-- Start the middle column -->

<section>
    <h1>Sign-Up</h1>
    <div align="center">
        <div align="left" class="subsection">
            <p>All fields are required.</p>
            <p>Password should match the pattern:</p>
            <ul>
                <li>at least one digit: 0-9</li>
                <li>at least one lower case letter: a-z</li>
                <li>at least one upper case letter: A-Z</li>
                <li>at least one spectral character: @, #, $, %, ^, &, +, =</li>
                <li>no whitespaces</li>
                <li>at least eight characters long</li>
            </ul>

            <c:if test="${validationErrors != null}">
                <div class="errors">
                    <p>Errors:</p>
                    <ul>
                        <c:forEach items="${validationErrors}" var="error">
                            <li><c:out value="${error.message}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <form:form method="POST" modelAttribute="signUpForm">
                <form:errors path="" cssClass="errors"/><br/>
                <form:label path="username">Username:</form:label><br/>
                <form:input path="username"/><br/>
                <form:errors path="username" cssClass="errors"/><br/>
                <div>
                    <span>
                        <form:label path="email">E-mail address</form:label><br/>
                        <form:input path="email"/><br/>
                        <form:errors path="email" cssClass="errors"/><br/>
                    </span>
                    <span>
                        <form:label path="confirmedEmail">Confirm E-mail address</form:label><br/>
                        <form:input path="confirmedEmail"/><br/>
                        <form:errors path="confirmedEmail" cssClass="errors"/><br/>
                    </span>
                </div>
                <div>
                    <span>
                        <form:label path="password">Password:</form:label><br/>
                        <form:password path="password"/><br/>
                        <form:errors path="password" cssClass="errors"/><br/>
                    </span>
                    <span>
                        <form:label path="confirmedPassword">Confirm password:</form:label><br/>
                        <form:password path="confirmedPassword"/><br/>
                        <form:errors path="confirmedPassword" cssClass="errors"/><br/>
                    </span>
                </div>
                <div align="center">
                    <input type="submit" value="Sign up"/>
                </div>
            </form:form>
        </div>
    </div>

    <form action="http://lab.alexcican.com/minimal_signup_form/about.html" method="get">
        <h1>Sign up</h1><br/>

        <span class="input"></span>
        <input type="text" name="name" placeholder="Full name" title="Format: Xx[space]Xx (e.g. Alex Cican)" autofocus autocomplete="off" required pattern="^\w+\s\w+$" />
        <span class="input"></span>
        <input type="email" name="email" placeholder="Email address" required />
        <span id="passwordMeter"></span>
        <input type="password" name="password" id="password" placeholder="Password" title="Password min 8 characters. At least one UPPERCASE and one lowercase letter" required pattern="(?=^.{8,}$)(?=.*[a-z])(?=.*[A-Z])(?!.*\s).*$"/>

        <button type="submit" value="Sign Up" title="Submit form" class="icon-arrow-right"><span>Sign up</span></button>
    </form>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>