<%--@elvariable id="sampleSourceTypeList" type="org.dulab.adapcompounddb.models.SampleSourceType[]"--%>
<%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%--@elvariable id="submissionForm" type="org.dulab.adapcompounddb.site.controllers.SubmissionController.SubmissionForm"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

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
    <p>Number of spectra: <span class="highlighted">${fn:length(submission.spectra)}</span></p>
</section>

<section>
    <h1>Mass spectra</h1>
    <p>
        Click on name to view the mass spectrum
    </p>
    <div align="center">
        <table id="spectrum_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>No</th>
                <th>Name</th>
                <th>Ret Time (min)</th>
                <th>Precursor mass</th>
                <%--<th>Properties</th>--%>
                <th>View / Search / Delete</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${submission.spectra.size() > 0}">
                <c:forEach items="${submission.spectra}" var="spectrum" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td><a href="${loop.index}/">${spectrum}</a><br/>
                            <small>${dulab:abbreviate(spectrum.properties, 80)}</small>
                        </td>
                        <td>${spectrum.retentionTime}</td>
                        <td>${spectrum.precursor}</td>
                            <%--<td>${dulab:abbreviate(submission.spectra[i].properties, 80)}</td>--%>
                        <td>
                            <!-- more horiz -->
                            <a href="${loop.index}/"><i class="material-icons" title="View spectrum">&#xE5D3;</i></a>
                            <!-- search -->
                            <a href="${loop.index}/search/"><i class="material-icons"
                                                               title="Search spectrum">&#xE8B6;</i></a>
                            <!-- delete -->
                            <a href="${loop.index}/delete/"><i class="material-icons"
                                                               title="Delete spectrum">&#xE872;</i></a>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>
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
            <form:form method="POST" modelAttribute="submissionForm">
                <form:errors path="" cssClass="errors"/><br/>

                <form:label path="name">Name:</form:label><br/>
                <form:input path="name"/><br/>
                <form:errors path="name" cssClass="errors"/><br/>

                <form:label path="description">Description:</form:label><br/>
                <form:textarea path="description" rows="12" cols="80"/><br/>
                <form:errors path="description" cssClass="errors"/><br/>

                <form:label path="submissionSourceId">Source:</form:label><br/>
                <span style="vertical-align: bottom;">
                    <form:select path="submissionSourceId">
                        <form:option value="0" label="Please select..."/>
                        <form:options items="${submissionForm.sources}" itemLabel="name" itemValue="id"/>
                    </form:select><br/>
                </span>
                <a href="<c:url value="/sources/"/>">
                    <!--list-->
                    <i class="material-icons" title="Manage sources">&#xE896;</i>
                </a>
                <a href="<c:url value="/sources/add/"/>">
                    <!--add circle-->
                    <i class="material-icons" title="Add new source">&#xE147;</i>
                </a><br/>
                <form:errors path="submissionSourceId" cssClass="errors"/><br/>

                <form:label path="submissionSpecimenId">Specimen:</form:label><br/>
                <span style="vertical-align: bottom;">
                    <form:select path="submissionSpecimenId">
                        <form:option value="0" label="Please select..."/>
                        <form:options items="${submissionForm.species}" itemLabel="name" itemValue="id"/>
                    </form:select><br/>
                </span>
                <a href="<c:url value="/species/"/>">
                    <!--list-->
                    <i class="material-icons" title="Manage species">&#xE896;</i>
                </a>
                <a href="<c:url value="/species/add/"/>">
                    <!--add circle-->
                    <i class="material-icons" title="Add new specimen">&#xE147;</i>
                </a><br/>
                <form:errors path="submissionSpecimenId" cssClass="errors"/><br/>

                <form:label path="submissionDiseaseId">Disease:</form:label><br/>
                <span style="vertical-align: bottom;">
                <form:select path="submissionDiseaseId">
                    <form:option value="0" label="Please select..."/>
                    <form:options items="${submissionForm.diseases}" itemLabel="name" itemValue="id"/>
                </form:select><br/>
                </span>
                <a href="<c:url value="/diseases/"/>">
                    <!--list-->
                    <i class="material-icons" title="Manage diseases">&#xE896;</i>
                </a>
                <a href="<c:url value="/diseases/add/"/>">
                    <!--add circle-->
                    <i class="material-icons" title="Add new disease">&#xE147;</i>
                </a><br/>
                <form:errors path="submissionDiseaseId" cssClass="errors"/><br/>


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

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(document).ready(function() {
        $('#spectrum_table').DataTable();
    })
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>