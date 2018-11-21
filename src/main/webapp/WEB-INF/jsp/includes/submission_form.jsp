    <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
        <form:form method="POST" modelAttribute="submissionForm">
            <form:errors path="" cssClass="errors"/><br/>
            <form:hidden path="id" /><br/>

            <form:label path="name">Name:</form:label><br/>
            <form:input path="name"/><br/>
            <form:errors path="name" cssClass="errors"/><br/>

            <form:label path="description">Description:</form:label><br/>
            <form:textarea path="description" rows="12" cols="80"/><br/>
            <form:errors path="description" cssClass="errors"/><br/>

            <form:label path="reference">Reference:</form:label><br/>
            <form:input path="reference"/><br/>
            <form:errors path="reference" cssClass="errors"/><br/>

            <form:errors path="submissionCategoryIds" cssClass="errors"/><br/>

            <form:label path="tags">Equipment:</form:label><br/>
            <form:input path="tags"/><br/>
            <form:errors path="tags" cssClass="errors"/><br/>

            <div align="center">
                <c:choose>
                    <c:when test="${submissionForm.id > 0}">
                        <input type="submit" value="Save"/>
                    </c:when>
                    <c:otherwise>
                        <input id="button-submit" type="submit" value="Submit" formaction="submit"/>
                        <a href="clear/" class="button">Clear</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </div>
</div>

<div id="progress-dialog"></div>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script>
    var progressDialog = $('#progress-dialog').progressDialog();

    $('#button-submit').click(function () {
        progressDialog.show('Submitting new spectra may take a while. Please wait...');
    })
</script>
