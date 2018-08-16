<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<section>
    <h1>Submit</h1>
    <div align="center">
        <div align="left" style="width: 600px">
            <p>
                Please provide name and detailed description of the data when you submit mass spectra to the library.
                This information will be used for finding unknown compounds.
            </p>
        </div>

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
            <form:form method="POST" modelAttribute="submission">
                <form:errors path="" cssClass="errors"/><br/>

                <form:label path="name">Name:</form:label><br/>
                <form:input path="name"/><br/>
                <form:errors path="name" cssClass="errors"/><br/>

                <form:label path="description">Description:</form:label><br/>
                <form:textarea path="description" rows="12" cols="80"/><br/>
                <form:errors path="description" cssClass="errors"/><br/>

                <form:label path="reference">Reference:</form:label><br/>
                <form:input path="reference"/><br/>
                <form:errors path="reference" cssClass="errors"/><br/>

                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <label>${type.label}:</label><br/>
                    <span style="vertical-align: bottom;">
                        <form:select path="submissionCategoryIds" multiple="false">
                            <form:option value="0" label="Please select..."/>
                            <form:options items="${availableCategories[type]}" itemLabel="name" itemValue="id"/>
                        </form:select><br/>
                    </span>
                    <a href="<c:url value="/categories/${type}/"/>">
                        <i class="material-icons" title="View categories for ${type.label}">&#xE896;</i>
                    </a>
                    <a href="<c:url value="/categories/${type}/add/"/>">
                        <i class="material-icons" title="Add category for ${type.label}">&#xE147;</i>
                    </a><br/>
                </c:forEach>
                <form:errors path="submissionCategoryIds" cssClass="errors"/><br/>

                <form:label path="tags">Equipment:</form:label><br/>
                <form:input path="tags"/><br/>
                <form:errors path="tags" cssClass="errors"/><br/>

                <div align="center">
                    <c:choose>
                        <c:when test="${submission.id > 0}">
                            <input type="submit" value="Save"/>
                        </c:when>
                        <c:otherwise>
                            <input type="submit" value="Submit" formaction="submit"/>
                            <a href="clear/" class="button">Clear</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </form:form>
        </div>
    </div>
</section>