<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>

<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-header card-header-tabs">
                <ul class="nav nav-tabs nav-fill nav-justified">
                    <%--@elvariable id="edit" type="java.util.Boolean"--%>
                    <%--@elvariable id="submissionForm" type="org.dulab.adapcompounddb.models.entities.submissionForm"--%>
                    <li class="nav-item"><a class="nav-link active" data-toggle="tab"
                                            href="#submission_${(edit && submissionForm.authorized) || submissionForm.id == 0 ? "edit" : "view"}">
                        Study Properties</a></li>
                    <li class="nav-item"><a id="mass_spectra_link" class="nav-link" data-toggle="tab"
                                            href="#mass_spectra">Mass Spectra</a>
                    </li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#files">Files</a></li>
                </ul>
            </div>

            <div class="card-body tab-content">

                <c:choose>
                    <c:when test="${(edit && submissionForm.authorized) || submissionForm.id == 0}">
                        <!-- Submission Information (Edit Mode) -->
                        <div id="submission_edit" class="tab-pane">
                            <div class="container">
                                <div class="row row-content">
                                    <div class="col-12 col-md-8 offset-md-2">
                                        Please provide name and detailed description of the data when you submit
                                        mass
                                        spectra to the
                                        knowledgebase.
                                    </div>
                                </div>
                                    <%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>"--%>
                                <c:if test="${validationErrors != null}">
                                    <div class="row">
                                        <div class="col-8 offset-2 text-danger">
                                            <ul>
                                                <c:forEach items="${validationErrors}" var="error">
                                                    <li><c:out value="${error.message}"/></li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </div>
                                </c:if>

                                <form:form method="POST" modelAttribute="submissionForm" cssStyle="width: 100%">
                                    <form:errors path="" cssClass="errors"/><br/>
                                    <form:hidden path="id"/><br/>

                                    <div class="row form-group">
                                        <form:label path="name"
                                                    cssClass="col-12 col-md-2 col-form-label">Name</form:label>
                                        <form:input path="name" cssClass="col-12 col-md-10 form-control"/>
                                        <form:errors path="name" cssClass="text-danger"/>
                                    </div>

                                    <div class="row form-group">
                                        <form:label path="externalId"
                                                    cssClass="col-12 col-md-2 col-form-label">External ID</form:label>
                                        <form:input path="externalId" cssClass="col-12 col-md-6 form-control"/>
                                        <form:errors path="externalId" cssClass="text-danger"/>
                                    </div>

                                    <div class="row form-group">
                                        <form:label path="description"
                                                    cssClass="col-12 col-md-2 col-form-label">Description</form:label>
                                        <form:textarea path="description" cssClass="col-12 col-md-8 form-control"
                                                       rows="10"/>
                                        <form:errors path="description" cssClass="text-danger"/>
                                    </div>

                                    <div class="row form-group">
                                        <form:label path="reference"
                                                    cssClass="col-12 col-md-2 col-form-label">URL</form:label>
                                        <form:input path="reference" cssClass="col-12 col-md-10 form-control"/>
                                        <form:errors path="reference" cssClass="text-danger"/>
                                    </div>

                                    <%--                                <form:errors path="submissionCategoryIds" cssClass="errors"/><br/>--%>

                                    <div class="row form-group">
                                        <form:label path="tags"
                                                    cssClass="col-12 col-md-2 col-form-label">Tags</form:label>
                                        <form:input placeholder="Add tags here!" path="tags"
                                                    cssClass="col-12 col-md-10 form-control"/>
                                        <form:errors path="tags" cssClass="errors"/>
                                    </div>

                                    <div class="row form-group">
                                        <div class="col-md-2 offset-md-2">
                                            <input class="btn btn-primary w-100" type="submit"
                                                   value="${(submissionForm.id > 0) ? "Save" : "Submit"}"/>
                                        </div>
                                    </div>
                                </form:form>
                            </div>
                        </div>

                    </c:when>
                    <c:otherwise>

                        <!-- Submission Information (View Mode) -->
                        <div id="submission_view" class="tab-pane active">
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
                            <div class="row">
                                <div class="col-md-4 offset-md-4">
                                    <a href="edit" class="btn btn-primary w-100">Edit Submission</a>
                                </div>
                            </div>

                                <%--                            </c:otherwise>--%>
                                <%--                        </c:choose>--%>
                        </div>

                    </c:otherwise>
                </c:choose>

                <!-- List of spectra -->
                <div id="mass_spectra" class="tab-pane">
                    <div class="row row-content">
                        <div class="col-12">
                            <table id="spectrum_table" class="display responsive" style="width: 100%">
                                <thead>
                                <tr>
                                    <td></td>
                                    <td>Name</td>
                                    <td>Ret Time (min)</td>
                                    <td>Precursor mass</td>
                                    <td>Significance</td>
                                    <td>Molecular Weight</td>
                                    <td>Integer m/z</td>
                                    <td>Type</td>
                                    <td></td>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 offset-md-3">
                            <a href="<c:url value="group_search/"/>" class="btn btn-primary w-100">Search all
                                spectra</a>
                        </div>
                        <div class="col-md-3">
                            <a href="<c:url value="study_search/"/>" class="btn btn-primary w-100">Search
                                studies</a>
                        </div>
                    </div>
                </div>

                <!-- List of files -->
                <div id="files" class="tab-pane">
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

<c:if test="${submissionForm.id == 0}">
    <div align="center">
        <!-- <div style="text-align: right; margin-right: 40px;"> -->
        <a href="clear/" class="button">Clear</a>
    </div>
</c:if>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.slim.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/datatables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/tag-it-6ccd2de/js/tag-it.min.js"/>"></script>
<script src="<c:url value="/resources/tagify-master/jQuery.tagify.min.js"/>"></script>
<script>
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
                table.column(2).visible(d.data.map(row => row['retentionTime']).join(''));
                table.column(3).visible(d.data.map(row => row['precursor']).join(''));
                table.column(4).visible(d.data.map(row => row['significance']).join(''));
                table.column(5).visible(d.data.map(row => row['molecularWeight']).join(''));
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
            },
            {
                "targets": 4,
                "render": function (data, type, row, meta) {
                    let x = row.significance;
                    return (x != null && !isNaN(x)) ? x.toFixed(3) : x;
                }
            }, {
                "targets": 5,
                "render": function (data, type, row, meta) {
                    let x = row.molecularWeight;
                    return (x != null && !isNaN(x)) ? x.toFixed(3) : x;
                }
            }, {
                "targets": 6,
                "render": function (data, type, row, meta) {
                    return (row.integerMz) ? 'Yes' : 'No';
                }
            },
            {
                // "orderable": true,
                "targets": 7,
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
                "targets": 8,
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

    $('#mass_spectra_link').on('shown.bs.tab', function (e) {
        table.columns.adjust();
    });

    // $('#mass_spectra_link').click(function () {
    //     table.columns.adjust();
    // });
    //
    // table.one('draw.dtr', function () {table.columns.adjust()});

    // $(".tabbed-pane").each(function () {
    //     $(this).tabbedPane('#spectrum_table');
    // });

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

    <%--// Selector with autocomplete--%>
    <%--$( '#tags' ).tagit( {--%>
    <%--    autocomplete: {--%>
    <%--        source: ${dulab:stringsToJson(availableTags)}--%>
    <%--    }--%>
    <%--} );--%>

    // });


</script>


