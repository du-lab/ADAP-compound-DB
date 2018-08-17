<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>--%>
<%--@elvariable id="signUpForm" type="org.dulab.site.controllers.SignUpForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

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
                <li>at least one special character: @, #, $, %, ^, &, +, =</li>
                <li>no whitespaces</li>
                <li>at least eight characters long</li>
            </ul>

            <form:form method="POST" modelAttribute="signUpForm">
                <c:if test="${errorMsg != null}">
                    <p class="errors">${errorMsg}</p>
                </c:if>
                <c:if test="${validationErrors != null}">
                    <c:forEach items="${validationErrors}" var="e">
                        <p class="errors"><c:out value="${e.message}"/></p>
                    </c:forEach>
                </c:if>
                <form:errors path="*" element="div" cssClass="errors"/>
                <p>
                    <form:label path="username">Username:</form:label><br/>
                    <form:input path="username"/>
                </p>
                <p>
                    <span>
                        <form:label path="email">E-mail address</form:label><br/>
                        <form:input path="email"/>
                    </span>
                    <span>
                        <form:label path="confirmedEmail">Confirm E-mail address</form:label><br/>
                        <form:input path="confirmedEmail"/>
                    </span>
                </p>
                <p>
                    <span>
                        <form:label path="password">Password:</form:label><br/>
                        <form:password path="password"/>
                    </span>
                    <span>
                        <form:label path="confirmedPassword">Confirm password:</form:label><br/>
                        <form:password path="confirmedPassword"/>
                    </span>
                </p>
                <div align="center">
                    <input type="submit" value="Sign up"/>
                </div>
            </form:form>
        </div>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>