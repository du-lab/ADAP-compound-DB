<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="availableCategories" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionCategories>>"--%>
<%--@elvariable id="availableTags" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionTag>"--%>
<%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%--@elvariable id="submissionForm" type="org.dulab.adapcompounddb.site.controllers.SubmissionController.SubmissionForm"--%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Files</h1>
    <table id="file_table" class="display" style="width: 100%; clear:none;">
        <thead>
        <tr>
            <th>File</th>
            <th>Type</th>
            <th>Size</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${submission.files}" var="file" varStatus="loop">
            <tr>
                <td><a href="${loop.index}/view/" target="_blank">${file.name}</a></td>
                <td>${file.fileType.label}</td>
                <td>${file.spectra.size()} spectra</td>
                <td>
                    <a href="${loop.index}/view/" target="_blank">
                        <i class="material-icons" title="View">attach_file</i>
                    </a>
                    <a href="${loop.index}/download/" target="_blank">
                        <i class="material-icons" title="Download">save_alt</i>
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>

<section>
    <h1>Mass spectra</h1>
    <div align="center">
        <table id="spectrum_table" class="display" style="width: 100%; clear:none;">
            <thead>
            <tr>
                <th></th>
                <th>Name</th>
                <th>Ret Time (min)</th>
                <th>Precursor mass</th>
                <th>Significance</th>
                <th>Type</th>
                <th>File</th>
                <th></th>
            </tr>

            </thead>
            <tbody>
            <c:if test="${submission.files.size() > 0}">
                <c:forEach items="${submission.files}" var="file" varStatus="fileLoop">
                    <c:forEach items="${file.spectra}" var="spectrum" varStatus="spectrumLoop">
                        <tr>
                            <td></td>
                                <%--<td>${loop.index + 1}</td>--%>
                            <td><a href="${fileLoop.index}/${spectrumLoop.index}/">${spectrum}</a><br/>
                                    <%--<small>${dulab:abbreviate(spectrum.properties, 80)}</small>--%>
                            </td>
                            <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.retentionTime}"/></td>
                            <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.precursor}"/></td>
                            <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.significance}"/></td>
                            <td><img src="${spectrum.chromatographyType.iconPath}" alt="${spectrum.chromatographyType.label}" title="${spectrum.chromatographyType.label}" class="icon"/></td>
                            <td>${file.name}</td>
                            <td>
                                <!-- more horiz -->
                                <a href="${fileLoop.index}/${spectrumLoop.index}/">
                                    <i class="material-icons" title="View spectrum">&#xE5D3;</i>
                                </a>
                                <!-- search -->
                                <a href="${fileLoop.index}/${spectrumLoop.index}/search/">
                                    <i class="material-icons" title="Search spectrum">&#xE8B6;</i>
                                </a>
                                <!-- delete -->
                                <c:if test="${authorized && edit}">
                                    <a href="${fileLoop.index}/${spectrumLoop.index}/delete/">
                                        <i class="material-icons" title="Delete spectrum">&#xE872;</i>
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
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

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/tag-it-6ccd2de/js/tag-it.min.js"/>"></script>
<script>
    $(document).ready(function () {

        // Table with a list of spectra
        var table = $('#spectrum_table').DataTable({
            processing: true,
            'columnDefs': [{
                'searchable': false,
                'orderable': false,
                'targets': 0
            }]
        });

        table.on('order.dt search.dt', function () {
            table.column(0, {search: 'applied', order: 'applied'})
                .nodes()
                .each(function (cell, i) {
                    cell.innerHTML = i + 1;
                })
        }).draw();

        // Table with a list of files
        $('#file_table').DataTable({
            bLengthChange: false,
            info: false,
            ordering: false,
            paging: false,
            searching: false
        });

        // Selector with autocomplete
        $('#tags').tagit({
            autocomplete: {
                source: ${dulab:stringsToJson(availableTags)}
            }
        });
    })
</script>