<%--@elvariable id="logInForm" type="org.dulab.site.controllers.LogInForm"--%>
<%--@elvariable id="signUpForm" type="org.dulab.site.controllers.SignUpForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Log-in / Sign-up</h1>

    <div class="subsection">
        <h2>Log-in</h2>
        <c:if test="${loginFailed}">
            <b class="errors">The username and password you entered are not correct. Please try again.</b><br/>
        </c:if><c:if test="${validationErrors != null}"><div class="errors">
            <ul>
                <c:forEach items="${validationErrors}" var="error">
                    <li><c:out value="${error.message}"/></li>
                </c:forEach>
            </ul>
        </div></c:if>

        <form:form method="POST" modelAttribute="logInForm" action="/login">
            <p>You must log in to submit (pseudo-) spectra.</p>
            <form:label path="username">Username:</form:label>
            <form:input path="username"/>
            <form:errors path="username" cssClass="errors"/>
            <br/>
            <form:label path="password">Password:</form:label>
            <form:password path="password"/>
            <form:errors path="password" cssClass="errors"/>
            <br/>
            <div class="submit">
                <input type="submit" value="Log in"/>
            </div>
        </form:form>
    </div>

    <div class="subsection">
        <h2>Sign-up</h2>
        <form:form method="POST" modelAttribute="signUpForm" action="/signup">
            <p>If you have not registered before, please do it now</p>
            <form:label path="username">Username:</form:label>
            <form:input path="username"/>
            <br/>
            <form:label path="password">Password:</form:label>
            <form:password path="password"/>
            <br/>
            <form:label path="repeatPassword">Repeat Password:</form:label>
            <form:password path="repeatPassword"/>
            <div class="submit">
                <input type="submit" value="Sign up"/>
            </div>
        </form:form>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />