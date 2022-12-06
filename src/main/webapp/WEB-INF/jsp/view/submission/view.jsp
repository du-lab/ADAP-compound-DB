<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%--@elvariable id="submissionForm" type="org.dulab.adapcompounddb.site.controllers.forms.SubmissionForm"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>
<%--@elvariable id="message" type="java.lang.String"--%>



<div id="modalView" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Study Upload</h4>
                <button type="button" class="close" data-dismiss="modal">
                    &times;
                </button>
            </div>
            <div class="modal-body">
                <p>Study is being submitted....</p>
            </div>
        </div>
    </div>
</div>
<div class="container">

    <c:if test="${!submission.isSearchable()}">
        <div class="toast" role="alert" data-autohide="false" aria-live="assertive" aria-atomic="true"
             style="position: absolute; top: 5pt; right: 5pt; z-index: 101;">
            <div class="toast-header bg-warning">
                <strong class="mr-auto">Warning</strong>
                <button type="button" class="close" data-dismiss="toast" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="toast-body">
                This submission contains raw data that cannot be searched in the group search.
            </div>
        </div>
    </c:if>

    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-between" role="toolbar">
                <div>
                    <c:if test="${submissionForm.id == 0}">
                        <%--                    <div align="center">--%>
                        <%--                        <!-- <div style="text-align: right; margin-right: 40px;"> -->--%>
                        <%--                        <a href="clear/" class="button">Clear</a>--%>
                        <%--                    </div>--%>
                        <a id="clearButton" type="button" class="btn btn-secondary" href="clear/">Clear</a>
                    </c:if>
                    <%--@elvariable id="edit_submission" type="java.lang.Boolean"--%>
                    <%--@elvariable id="view_submission" type="java.lang.Boolean"--%>
                    <c:if test="${view_submission && !edit_submission}">
                        <a href="edit" type="button" class="btn btn-primary">Edit</a>
                    </c:if>
                    <a href="<c:url value="/export/submission/${submission.id}/"><c:param name="name" value="${submission.name}"/></c:url>"
                       type="button" class="btn btn-primary">Export</a>
                </div>
                <div>
                    <div class="dropdown">
                        <button id="searchMenu"
                                class="btn btn-primary dropdown-toggle <c:if test="${!submission.isSearchable()}">disabled</c:if>"
                                type="button" data-toggle="dropdown">
                            Search
                        </button>
                        <div class="dropdown-menu dropdown-menu-right">
                            <a id="searchAllSpectra" class="dropdown-item"
                               href="<c:url value="/group_search/parameters"><c:if test="${submission.id != 0}"><c:param name="submissionId" value="${submission.id}"/></c:if></c:url>"
                               title="Search based on the spectral similarity">
                                Search for similar spectra
                            </a>
                            <a id="searchWithOntologyLevels" class="dropdown-item <c:if test="${!dulab:checkOntologyLevels(submission)}">disabled</c:if>"
                               href="<c:url value="/group_search/parameters"><c:if test="${submission.id != 0}"><c:param name="submissionId" value="${submission.id}"/></c:if><c:param name="withOntologyLevels">true</c:param></c:url>"
                               title="Search based on the mass, retention time, and spectral similarity">
                                Search for similar spectra (with Ontology Levels)
                            </a>
                            <a class="dropdown-item" href="<c:url value="study_search/"/>">Search for similar
                                studies</a>
                        </div>
                    </div>
                    <%--                    <a href="<c:url value="group_search/"/>" type="button" class="btn btn-primary">Search all--%>
                    <%--                        spectra</a>--%>
                    <%--                    <a href="<c:url value="study_search/"/>" type="button" class="btn btn-primary">Search studies</a>--%>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col-12">
            <div class="card">
                <div class="card-header card-header-tabs">
                    <ul class="nav nav-tabs nav-fill nav-justified" role="tablist">
                        <c:if test="${view_submission && edit_submission}">
                            <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#submission_edit">
                                Properties</a></li>
                        </c:if>
                        <c:if test="${view_submission && !edit_submission}">
                            <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#submission_view">
                                Properties</a></li>
                        </c:if>
                        <li class="nav-item"><a id="mass_spectra_link"
                                                class="nav-link ${!view_submission ? "active" : ""}"
                                                data-toggle="tab"
                                                href="#mass_spectra">Data</a>
                        </li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#files">Files</a></li>
                    </ul>
                </div>

                <div class="card-body tab-content">

                    <c:if test="${view_submission && edit_submission}">
                        <!-- Submission Information (Edit Mode) -->
                        <div id="submission_edit" class="tab-pane active" role="tabpanel">
                            <div class="container small">
                                <div class="row">
                                    <div class="col-12 col-md-8 offset-md-2">
                                        Please provide name and detailed description of the data when you submit
                                        mass spectra to the knowledgebase.
                                    </div>
                                </div>
                                    <%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>"--%>
                                <c:if test="${validationErrors != null}">
                                    <div class="row">
                                        <div class="col-12 col-md-8 offset-md-2 text-danger">
                                            <ul>
                                                <c:forEach items="${validationErrors}" var="error">
                                                    <li><c:out value="${error.message}"/></li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </div>
                                </c:if>

                                    <%--@elvariable id="submissionForm" type="org.dulab.adapcompounddb.site.controllers.forms.SubmissionForm"--%>
                                <form:form method="POST" modelAttribute="submissionForm" cssStyle="width: 100%">
                                    <div class="form-row form-group">
                                        <form:errors path="" cssClass="col-12 col-md-8 offset-md-2 errors"/><br/>
                                    </div>
                                    <form:hidden path="id"/><br/>

                                    <div class="form-row form-group">
                                        <form:label path="name"
                                                    cssClass="col-12 col-md-2 col-form-label">Name</form:label>
                                        <form:input path="name" cssClass="col-12 col-md-10 form-control"/>
                                        <form:errors path="name" cssClass="text-danger"/>
                                    </div>

                                    <div class="form-row form-group">
                                        <form:label path="externalId"
                                                    cssClass="col-12 col-md-2 col-form-label">External ID</form:label>
                                        <form:input path="externalId" cssClass="col-12 col-md-6 form-control"/>
                                        <form:errors path="externalId" cssClass="text-danger"/>
                                    </div>

                                    <div class="form-row form-group">
                                        <form:label path="description"
                                                    cssClass="col-12 col-md-2 col-form-label">Description</form:label>
                                        <form:textarea path="description" cssClass="col-12 col-md-8 form-control"
                                                       rows="10"/>
                                        <form:errors path="description" cssClass="text-danger"/>
                                    </div>

                                    <div class="form-row form-group">
                                        <div class="col-md-3 offset-md-2">
                                                <%--                                            <form:label path="isPrivate"--%>
                                                <%--                                                        cssClass="col-md-6 col-form-label">Private</form:label>--%>
                                            <form:checkbox path="isPrivate" data-toggle="toggle" data-on="Private"
                                                           data-off="Public" data-size="sm"/>
                                            <form:errors path="isPrivate" cssClass="text-danger"/>
                                        </div>
                                        <div class="col-md-3">
                                                <%--                                            <form:label path="isLibrary"--%>
                                                <%--                                                        cssClass="col-md-6 col-form-label">Library</form:label>--%>
                                            <form:checkbox path="isLibrary" data-toggle="toggle" data-on="Library"
                                                           data-off="Study" data-size="sm"/>
                                            <form:errors path="isLibrary" cssClass="text-danger"/>
                                        </div>
                                        <div class="col-md-3">
                                            <form:checkbox path="isInHouseLibrary" data-toggle="toggle"
                                                           data-on="In-House" data-off="External" data-size="sm"/>
                                            <form:errors path="isInHouseLibrary" cssClass="text-danger"/>
                                        </div>
                                    </div>

                                    <%--                                    <div class="form-row form-group">--%>
                                    <%--                                        <form:label path="isLibrary"--%>
                                    <%--                                                    cssClass="col-md-2 col-form-label">Library</form:label>--%>
                                    <%--                                        <form:checkbox path="isLibrary" data-toggle="toggle" data-on="Yes" data-off="No"--%>
                                    <%--                                                       data-size="sm"/>--%>
                                    <%--                                        <form:errors path="isLibrary" cssClass="text-danger"/>--%>
                                    <%--                                    </div>--%>

                                    <div class="form-row form-group">
                                        <form:label path="reference"
                                                    cssClass="col-12 col-md-2 col-form-label">URL</form:label>
                                        <form:input path="reference" cssClass="col-12 col-md-10 form-control"/>
                                        <form:errors path="reference" cssClass="text-danger"/>
                                    </div>

                                    <%--                                <form:errors path="submissionCategoryIds" cssClass="errors"/><br/>--%>

                                    <div class="form-row form-group">
                                        <form:label path="tags"
                                                    cssClass="col-12 col-md-2 col-form-label">Tags</form:label>
                                        <form:input placeholder="Add tags here!" path="tags"
                                                    cssClass="col-12 col-md-10"/>
                                        <form:errors path="tags" cssClass="text-danger"/>
                                    </div>

                                    <div class="form-row form-group">
                                        <div class="col-md-2 offset-md-2">
                                            <input class="btn btn-primary w-100" type="submit"
                                                   value="${(submissionForm.id > 0) ? "Save" : "Submit"}"/>
                                        </div>
                                    </div>
                                </form:form>
                            </div>
                        </div>

                    </c:if>
                    <c:if test="${view_submission && !edit_submission}">

                        <!-- Submission Information (View Mode) -->
                        <div id="submission_view" class="tab-pane active" role="tabpanel">
                            <div class="row row-content">
                                <div class="col-12">
                                    <table id="info_table" class="display" style="width: 100%; clear: none;">
                                        <thead>
                                        <tr>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td><strong>Name:</strong></td>
                                            <td>${submission.name}</td>
                                        </tr>
                                        <tr>
                                            <td><strong>External ID:</strong></td>
                                            <td>${submission.externalId}</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Description:</strong></td>
                                            <td>
                                                <pre>${submission.description}</pre>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Private:</strong></td>
                                            <td><span
                                                    class="badge badge-info">${submission.isPrivate() ? "Yes" : "No"}</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Library:</strong></td>
                                            <td><span
                                                    class="badge badge-warning">${submission.getIsLibrary() ? "Yes" : "No"}</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>In-House:</strong></td>
                                            <td><span
                                                    class="badge badge-success">${submission.isInHouseReference() ? "Yes" : "No"}</span>
                                            </td>
                                        </tr>
                                        <c:if test="${submission.reference != null}">
                                            <tr>
                                                <td><strong>URL:</strong></td>
                                                <td><a href="${submission.reference}"
                                                       title="${submission.reference}"
                                                       target="_blank">${dulab:abbreviate(submission.reference, 80)}</a>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${submission.tagsAsString.length() > 0}">
                                            <tr>
                                                <td><strong>Tags:</strong></td>
                                                <td>
                                                        <%-- ${submission.tagsAsString}--%>
                                                    <c:forEach items="${submission.tags}" var="tag"
                                                               varStatus="status">
                                                        <span id="${submission.id}color${status.index}">${tag}&nbsp;</span>
                                                        <script>
                                                            var spanId = '${fn:length(submission.tags)}';
                                                        </script>
                                                    </c:forEach>

                                                    <script>
                                                        spanColor(${submission.id}, spanId);
                                                    </script>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <c:forEach items="${submissionCategoryTypes}" var="type">
                                            <tr>
                                                <td><strong>${type.label}:</strong></td>
                                                <td>${submission.getCategory(type)}</td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                                <%--                            <div class="row">--%>
                                <%--                                <div class="col-md-4 offset-md-4">--%>
                                <%--                                    <a href="edit" class="btn btn-primary w-100">Edit Submission</a>--%>
                                <%--                                </div>--%>
                                <%--                            </div>--%>

                                <%--                            </c:otherwise>--%>
                                <%--                        </c:choose>--%>
                        </div>
                    </c:if>

                    <!-- List of spectra -->
                    <div id="mass_spectra" class="tab-pane ${!view_submission ? "active" : ""}" role="tabpanel">
                        <div class="row row-content">
                            <div class="col-12 small">
                                <table id="spectrum_table" class="display compact" style="width: 100%">
                                    <thead>
                                    <tr>
                                        <td></td>
                                        <td>Name</td>
                                        <td>Ret Time (min)</td>
                                        <td>Precursor m/z</td>
                                        <td>Precursor type</td>
                                        <td>Significance</td>
                                        <td>Neutral mass</td>
                                        <td>Integer m/z</td>
                                        <td>Type</td>
                                        <td></td>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                        <%--                        <div class="row">--%>
                        <%--                            <div class="col-md-3 offset-md-3">--%>
                        <%--                                <a href="<c:url value="group_search/"/>" class="btn btn-primary w-100">Search all--%>
                        <%--                                    spectra</a>--%>
                        <%--                            </div>--%>
                        <%--                            <div class="col-md-3">--%>
                        <%--                                <a href="<c:url value="study_search/"/>" class="btn btn-primary w-100">Search--%>
                        <%--                                    studies</a>--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>
                    </div>

                    <!-- List of files -->
                    <div id="files" class="tab-pane" role="tabpanel">
                        <table id="file_table" class="display responsive" style="width: 100%;">
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
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%--    <c:if test="${submissionForm.id == 0}">--%>
    <%--        <div align="center">--%>
    <%--            <!-- <div style="text-align: right; margin-right: 40px;"> -->--%>
    <%--            <a href="clear/" class="button">Clear</a>--%>
    <%--        </div>--%>
    <%--    </c:if>--%>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap4-toggle/js/bootstrap4-toggle.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/tagify-master/jQuery.tagify.min.js"/>"></script>
<script>

    $(document).ready(function () {
        $('.toast').toast('show');

    });

    $('#submissionForm').submit(function() {
        $("#modalView").modal('show');
    })

    // $(document).ready(function () {
    // Table with a list of spectra
    const table = $('#spectrum_table').DataTable({
        serverSide: true,
        processing: true,
        responsive: true,
        scrollX: true,
        scroller: true,
        ajax: {
            url: "${pageContext.request.contextPath}/spectrum/findSpectrumBySubmissionId.json?submissionId=${submission.id}",
            data: function (data) {
                data.column = data.order[0].column;
                data.sortDirection = data.order[0].dir;
                data.search = data.search["value"];
            },
            dataSrc: function (d) {
                // Hide columns without data
                // table.column(2).visible(d.data.map(row => row['retentionTime']).join(''));
                // table.column(3).visible(d.data.map(row => row['precursor']).join(''));
                // table.column(4).visible(d.data.map(row => row['precursorType']).join(''));
                table.column(5).visible(d.data.map(row => row['significance']).join(''));
                // table.column(6).visible(d.data.map(row => row['molecularWeight']).join(''));
                return d.data;
            }
        },
        "columnDefs": [
            {
                "targets": 0,
                "searchable": false,
                "render": function (data, type, row, meta) {
                    return meta.settings.oAjaxData.start + meta.row + 1;
                }
            },
            {
                // "orderable": true,
                "targets": 1,
                "render": function (data, type, row, meta) {
                    let href = (row.id > 0) ? `spectrum/\${row.id}/` : `\${row.fileIndex}/\${row.spectrumIndex}/`;
                    return `<a href="\${href}">\${row.name}</a><br/><small>\${row.fileName}</small>`;
                }
            },
            {
                "targets": 2,
                "render": function (data, type, row, meta) {
                    let x = row.retentionTime;
                    return (x != null && !isNaN(x)) ? x.toFixed(3) : x;
                }
            },
            {
                "targets": 3,
                "render": function (data, type, row, meta) {
                    let x = row.precursor;
                    return (x != null && !isNaN(x)) ? x.toFixed(3) : x;
                }
            }, {
                "targets": 4,
                "render": function (data, type, row, meta) {
                    return row.precursorType;
                }
            },
            {
                "targets": 5,
                "render": function (data, type, row, meta) {
                    let x = row.significance;
                    return (x != null && !isNaN(x)) ? x.toFixed(3) : x;
                }
            }, {
                "targets": 6,
                "render": function (data, type, row, meta) {
                    let x = row.mass;
                    return (x != null && !isNaN(x)) ? x.toFixed(3) : x;
                }
            }, {
                "targets": 7,
                "render": function (data, type, row, meta) {
                    return (row.integerMz) ? 'Yes' : 'No';
                }
            },
            {
                // "orderable": true,
                "targets": 8,
                "render": function (data, type, row, meta) {
                    content = '<img' +
                        ' src="${pageContext.request.contextPath}/' + row.chromatographyTypeIconPath + '"'
                        + ' alt="' + row.chromatographyTypeLabel + '"'
                        + ' title="' + row.chromatographyTypeLabel + '"'
                        + ' class="icon"/>';

                    return content;
                }
            },
            {
                "orderable": false,
                "targets": 9,
                "render": function (data, type, row, meta) {
                    let href = (row.id > 0) ? `spectrum/\${row.id}/` : `\${row.fileIndex}/\${row.spectrumIndex}/`;
                    let content = `<a href="\${href}"><i class="material-icons" title="View spectrum">&#xE5D3;</i></a>`;
                    content += `<a href="\${href}search/"><i class="material-icons" title="Search spectrum">&#xE8B6;</i></a>`
                    if (JSON.parse("${submissionForm.authorized && edit}"))
                        content += `<a href="spectrum/\${row.id}/delete"><i class="material-icons" title="Delete spectrum">&#xE872;</i></a>`;
                    return content;
                }
            }
        ]
    });

    // Table with submissionForm information
    $('#info_table').DataTable({
        bLengthChange: false,
        info: false,
        ordering: false,
        paging: false,
        searching: false
    });

    // Table with a list of files
    $('#file_table').DataTable();

    $('#tags').tagify({
            // pattern: /^.{0,50}$/,  // Validate typed tag(s) by Regex. Here maximum chars length is defined as "20"
            // delimiters: ", ",         // add new tags when a comma or a space character is entered
            // maxTags: 6,
            keepInvalidTags: true,         // do not remove invalid tags (but keep them marked as invalid)
            backspace: "edit",
            <%--@elvariable id="availableTags" type="java.util.List<java.lang.String>"--%>
            whitelist:${dulab:stringsToJson(availableTags)},
            dropdown: {
                classname: "color-blue",
                enabled: 2,
                maxItems: 6
            }
        }
    )

    // Adjust column widths when a table becomes visible
    $(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
        $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
    });

</script>


