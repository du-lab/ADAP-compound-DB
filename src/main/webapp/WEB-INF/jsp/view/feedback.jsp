<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>--%>
<%--@elvariable id="signUpForm" type="org.dulab.site.controllers.SignUpForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Feedback</h1>
    <div align="center">
        <p>
            Please take a moment to review this application.<br/>
            You can mention your experience, any issues faced or any areas of this website you would like us to improve.<br/>
            We will address them asap.
        </p>
    </div>
    <c:if test="${errors != null}">
        <div class="errors" align="center">
            <ul class="no-bullet-list">
                <c:forEach items="${errors}" var="error">
                    <li><c:out value="${error.defaultMessage}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
    <div align="center">
        <div align="left" class="subsection">
            <form:form method="POST" modelAttribute="feedbackForm">
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
                    <form:label path="name">Name:</form:label><br/>
                    <form:input path="name"/>
                </p>
                <p>
                    <span>
                        <form:label path="affiliation">Affiliation</form:label><br/>
                        <form:input path="affiliation"/>
                    </span>
                </p>
                <p>
                    <span>
                        <form:label path="email">E-mail address</form:label><br/>
                        <form:input path="email"/>
                    </span>
                </p>
                <p>
                    <span>
                        <form:label path="message">Feedback Message:</form:label><br/>
                        <form:textarea path="message" />
                    </span>
                </p>
                <div align="left">
                    <input type="submit" value="Submit Feedback"/>
                </div>
            </form:form>
        </div>
        <p class="success-message">${status}</p>
    </div>
</section>