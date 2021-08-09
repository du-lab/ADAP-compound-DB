<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>--%>
<%--@elvariable id="signUpForm" type="org.dulab.site.controllers.SignUpForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Feedback</h1>
    <div align="center">
        <div align="left" class="subsection">
            <form:form>
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
                    <label>Name:</label><br/>
                        <input readonly value="${feedback.name}" />
                </p>
                <p>
                    <span>
                        <label>Affiliation</label><br/>
                        <input readonly value="${feedback.affiliation}" />
                    </span>
                </p>
                <p>
                    <span>
                        <label>E-mail address</label><br/>
                        <input size="auto" readonly value="${feedback.email}"/>
                        <a href="mailto:${feedback.email}">Reply</a>
                    </span>
                </p>
                <p>
                    <span>
                        <label>Feedback Message:</label><br/>
                        <textarea readonly>${feedback.message}</textarea>
                    </span>
                </p>
            </form:form>
        </div>
    </div>
</section>