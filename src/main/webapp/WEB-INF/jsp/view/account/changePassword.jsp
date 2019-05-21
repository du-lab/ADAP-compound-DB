<%--@elvariable id="changePassForm" type="org.dulab.site.controllers.ChangePassForm"--%>
<%--@elvariable id="currentUser" type="org.springframework.security.core.userdetails.User"--%>
<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Changing Password</h1>
    <div align="center">
        <div align="left" class="subsection">
        <p>Please fill out the information below to change your current password.</p>
        <p>Password should match the pattern:</p>
        <ul>
            <li>at least one digit: 0-9</li>
            <li>at least one lower case letter: a-z</li>
            <li>at least one upper case letter: A-Z</li>
            <li>at least one special character: @, #, $, %, ^, &, +, =</li>
            <li>no whitespaces</li>
            <li>at least eight characters long</li>
        </ul>

        <form:form method="POST" modelAttribute="changePassForm">
            <c:if test="${errorMsg != null}">
                <p class="errors">${errorMsg}</p>
            </c:if>
            <c:if test="${validationErrors != null}">
                <c:forEach items="${validationErrors}" var="e">
                    <p class="errors"><c:out value="${e.message}"/></p>
                </c:forEach>
            </c:if>
            <form:errors path="*" element="div" cssClass="errors"/>
            <table>
                <tr>
                    <td>
                        <form:label path="username">Username:</form:label><br/>
                    </td>
                    <td>
                     ${currentUser.username}
                    </td>
                </tr>
                <tr>
                    <td>
                        <form:label path="oldpass">Old Password:</form:label><br/>
                    </td>
                    <td>
                        <form:password path="oldpass"/><br/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <form:label path="newpass">New Password:</form:label><br/>
                    </td>
                    <td>
                        <form:password path="newpass"/><br/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <form:label path="renewpass">ReEnter Password:</form:label><br/>
                    </td>
                    <td>
                        <form:password path="renewpass"/><br/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="RESET"/></td>
                </tr>
            </table>
        </form:form>
        </div>
    </div>


</section>