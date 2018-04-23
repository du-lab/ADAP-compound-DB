<%--@elvariable id="submission" type="org.dulab.models.Submission"--%>
<%--@elvariable id="form" type="org.dulab.site.controllers.SubmissionController.Form"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>File content</h1>
    <div align="right" style="float: right">
        <p><a href="filedownload/" class="button" target="_blank">
            Download file</a></p>
        <p><a href="fileview/" class="button" target="_blank">
            View file</a></p>
    </div>
    <p>Filename: <span class="highlighted">${submission.filename}</span></p>
    <p>File Type: <span class="highlighted">${submission.fileType.label}</span></p>
    <p>Chromatography Type: <span class="highlighted">${submission.chromatographyType.label}</span></p>
    <p>Number of spectra: <span class="highlighted">${fn:length(submission.spectra)}</span></p>
</section>

<section>
    <h1>Mass spectra</h1>
    <p>
        Click on name to view the mass spectrum
    </p>
    <div align="center" style="overflow: auto; max-height: 400px">
        <table>
            <tr>
                <th>No</th>
                <th>Name</th>
                <th>Properties</th>
                <th></th>
            </tr>
            <c:if test="${submission.spectra.size() > 0}">
                <c:forEach var="i" begin="0" end="${submission.spectra.size() - 1}">
                    <tr>
                        <td>${i + 1}</td>
                        <td><a href="${i}/">${submission.spectra[i]}</a></td>
                        <td>${dulab:abbreviate(submission.spectra[i].properties, 80)}</td>
                        <td>
                            <!-- forward -->
                            <a href="${i}/"><i class="material-icons">&#xE154;</i></a>
                            <!-- search -->
                            <a href="${i}/match/"><i class="material-icons">&#xE8B6;</i></a>
                            <!-- delete -->
                            <a href="${i}/delete/"><i class="material-icons">&#xE872;</i></a>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </table>
    </div>
</section>

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
            <form:form method="POST" modelAttribute="form">
                <form:errors path="" cssClass="errors"/><br/>

                <form:label path="name">Name:</form:label><br/>
                <form:input path="name"/><br/>
                <form:errors path="name" cssClass="errors"/><br/>

                <form:label path="description">Description:</form:label><br/>
                <form:textarea path="description" rows="12" cols="80"/><br/>
                <form:errors path="description" cssClass="errors"/><br/>

                <form:label path="submissionCategoryId">Submission Category</form:label><br/>
                <span style="vertical-align: bottom;">
                    <form:select path="submissionCategoryId">
                        <form:option value="0" label="--- Select ---"/>
                        <form:options items="${submissionCategories}" itemValue="id"/>
                    </form:select>
                </span>
                <a href="<c:url value="/submissioncategory/"/>">
                    <!--list-->
                    <i class="material-icons" title="Manage categories">&#xE896;</i>
                </a>
                <a href="<c:url value="/submissioncategory/add/"/>">
                    <!--add circle-->
                    <i class="material-icons" title="Add new category">&#xE147;</i>
                </a><br/>
                <form:errors path="submissionCategoryId" cssClass="errors"/>



                <div align="center">
                    <input type="submit" value="<c:choose>
                        <c:when test="${submission.id > 0}">Save</c:when>
                        <c:otherwise>Submit</c:otherwise>
                    </c:choose>"/>
                </div>
            </form:form>
        </div>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />