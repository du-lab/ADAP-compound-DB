<%--@elvariable id="categoryForm" type="org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.CategoryForm"--%>
<%--@elvariable id="validationErrors" type="javax.validation.ConstraintViolation"--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section>
    <h1>Edit Category</h1>
    <div align="center">
        <div align="left" class="subsection">
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
            <form:form method="post" modelAttribute="categoryForm">
                <form:errors path="" cssClass="errors"/>

                <form:label path="name">Name:</form:label><br/>
                <form:input path="name"/><br/>
                <form:errors path="name" cssClass="errors"/><br/>

                <form:label path="description">Description:</form:label><br/>
                <form:textarea path="description" rows="12" cols="80"/><br/>
                <form:errors path="description" cssClass="errors"/>

                <div align="center">
                    <input type="submit" value="Save"/>
                </div>
            </form:form>
        </div>
    </div>
</section>