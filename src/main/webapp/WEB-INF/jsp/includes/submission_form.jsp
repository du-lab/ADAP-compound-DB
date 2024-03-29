<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>


<div align="center">
    <div>
        <p>
            Please provide name and detailed description of the data when you submit mass spectra to the
            knowledgebase.<br/>
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
            <form:hidden path="id"/><br/>

            <form:label path="name">Name:</form:label><br/>
            <form:input class="width_input" path="name"/><br/>
            <form:errors path="name" cssClass="errors"/><br/>

            <form:label path="externalId">External ID:</form:label><br/>
            <form:input class="width_input" path="externalId"/><br/>
            <form:errors path="externalId" cssClass="errors"/><br/>

            <form:label path="description">Description:</form:label><br/>
            <form:textarea class="width_textarea" path="description"/><br/>
            <form:errors path="description" cssClass="errors"/><br/>

            <form:label path="reference">URL:</form:label><br/>
            <form:input class="width_input" path="reference"/><br/>
            <form:errors path="reference" cssClass="errors"/><br/>

            <form:errors path="submissionCategoryIds" cssClass="errors"/><br/>

            <form:label path="tags">Tags:</form:label><br/>
            <form:input placeholder="Add tags here!" path="tags"/><br/>
            <form:errors path="tags" cssClass="errors"/><br/>

            <div align="center">
                <c:choose>
                    <c:when test="${submissionForm.id > 0}">
                        <input type="submit" value="Save"/>
                    </c:when>
                    <c:otherwise>
                        <input id="button-submit" type="submit" value="Submit" formaction="submit"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </div>
</div>

<div id="progress-dialog"></div>

<script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.13.2/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script src="<c:url value="/resources/tagify-master/jQuery.tagify.min.js"/>"></script>
<script>
    var progressDialog = $( '#progress-dialog' ).progressDialog();

    $( '#button-submit' ).click( function () {
        progressDialog.show( 'Submitting new spectra may take a while. Please wait...' );
    } )
</script>

<%-- using tagify library to generate tags--%>
<script>
    $( '#tags' ).tagify( {
            // pattern: /^.{0,50}$/,  // Validate typed tag(s) by Regex. Here maximum chars length is defined as "20"
            // delimiters: ", ",         // add new tags when a comma or a space character is entered
            // maxTags: 6,
            keepInvalidTags: true,         // do not remove invalid tags (but keep them marked as invalid)
            backspace: "edit",
            whitelist:${dulab:stringsToJson(availableTags)},
            dropdown: {
                classname:"color-blue",
                enabled: 2,
                maxItems:6
            }
        }
    )
        .on( 'add', function (e, tagName) {
            console.log( 'JQEURY EVENT: ', 'added', tagName )
        } )
        .on( "invalid", function (e, tagName) {
            console.log( 'JQEURY EVENT: ', "invalid", e, ' ', tagName );
        } );


    // generate a random color (in HSL format, which I like to use)
    function getRandomColor() {
        function rand(min, max) {
            return min + Math.random() * (max - min);
        };

        var h = rand( 1, 360 ) | 0,
            s = rand( 40, 70 ) | 0,
            l = rand( 65, 72 ) | 0;

        return 'hsl(' + h + ',' + s + '%,' + l + '%)';
    }
</script>


