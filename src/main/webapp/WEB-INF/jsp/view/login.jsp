<%--@elvariable id="logInForm" type="org.dulab.site.controllers.LogInForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Log-in</h1>
    <div align="center">
        <div class="subsection">
            <p>You must log in to submit new mass spectra to the library.</p>
            <c:if test="${param['loginFailed']}">
                <p class="errors">The username and password you entered are not correct. Please try again.</p><br/>
            </c:if><c:if test="${validationErrors != null}"><div class="errors">
                <ul>
                    <c:forEach items="${validationErrors}" var="error">
                        <li><c:out value="${error.message}"/></li>
                    </c:forEach>
                </ul>
            </div></c:if>

            <form:form method="POST" action="${pageContext.request.contextPath}/j_spring_security_check" modelAttribute="logInForm">
                <form:label path="username">Username:</form:label><br/>
                <form:input path="username" autofocus="autofocus" /><br/>
                <form:errors path="username" cssClass="errors"/><br/>
                <form:label path="password">Password:</form:label><br/>
                <form:password path="password"/><br/>
                <form:errors path="password" cssClass="errors"/><br/>
                <div align="center">
                    <input name="submit" type="submit" value="Log in"/>
                </div>
            </form:form>
        </div>
    </div>
</section>

<section>
    <h1>Sign-Up</h1>
    <div align="center">
        <p>If you are not registered yet, please do it now:</p>
        <a href="<c:url value="/signup"/>" class="button">Register</a>
    </div>
</section>