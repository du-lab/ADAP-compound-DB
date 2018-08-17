<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%--@elvariable id="authorized" type="java.lang.Boolean"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<!-- Submission information -->
<section>
    <h1>Submission</h1>
    <div align="center">
        <table id="info_table" class="display" style="width: 100%; clear: none;">
            <thead>
            <tr>
                <th>Property</th>
                <th>Value</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td><strong>Name:</strong></td>
                <td>${submission.name}</td>
            </tr>
            <tr>
                <td><strong>Description:</strong></td>
                <td><pre style="white-space: pre-wrap;">${submission.description}</pre></td>
            </tr>
            <c:if test="${submission.reference != null}">
                <tr>
                    <td><strong>URL:</strong></td>
                    <td><a href="${submission.reference}" title="${submission.reference}" target="_blank">${dulab:abbreviate(submission.reference, 80)}</a></td>
                </tr>
            </c:if>
            <c:if test="${submission.tagsAsString.length() > 0}">
                <tr>
                    <td><strong>Equipment:</strong></td>
                    <td>${submission.tagsAsString}</td>
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
</section>

<!-- List of submitted files -->
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

<!-- List of submitted spectra -->
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
                <th>Type</th>
                <th>File</th>
                <th></th>
            </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>
</section>

<c:if test="${authorized}">
    <section class="no-background">
        <div align="center">
            <a href="edit" class="button">Edit Submission</a>
        </div>
    </section>
</c:if>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/tag-it-6ccd2de/js/tag-it.min.js"/>"></script>
<script>
    $(document).ready(function () {

        // Table with a list of spectra
        var table = $('#spectrum_table').DataTable({
            serverSide: true,
            processing: true,
            ajax: {
                url: "${pageContext.request.contextPath}/spectrum/findSpectrumBySubmissionId.json?submissionId=${submission.id}",

                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                }
            },
            "columnDefs": [
                {"defaultContent": "", "targets": 0, "orderable": false},
                {
                    "data": "name", "orderable": false,
                    "targets": 1,
                    "render": function (data, type, row, meta) {
                        content = '<a href="spectrum/' + row.id + '/">' +
                            row.name +
                            '</a>';
                        return content;
                    }
                },
                {"data": "retentionTime", "targets": 2},
                {"data": "precursor", "targets": 3},
                {"data": "chromatographyTypeLabel", "targets": 4},
                {"data": "fileName", "targets": 5},
                {
                    "orderable": false,
                    "targets": 6,
                    "render": function (data, type, row, meta) {
                        content = '<a href="spectrum/' + row.id + '/">' +
                            '<i class="material-icons" title="View spectrum">&#xE5D3;</i>' +
                            '</a>' +
                            '<a href="spectrum/' + row.id + '/search/">' +
                            '<i class="material-icons" title="Search spectrum">&#xE8B6;</i>' +
                            '</a>';
                        if (JSON.parse("${authorized && edit}")) {
                            content += '<a href="spectrum/' + row.id + '/delete">' +
                                '<i class="material-icons" title="Delete spectrum">&#xE872;</i>' +
                                '</a>';
                        }
                        return content;
                    }
                }
            ]
        });

        table.on('order.dt search.dt', function () {
            table.column(0, {search: 'applied', order: 'applied'})
                .nodes()
                .each(function (cell, i) {
                    cell.innerHTML = i + 1;
                })
        }).draw();

        // Table with submission information
        $('#info_table').DataTable({
            bLengthChange: false,
            info: false,
            ordering: false,
            paging: false,
            searching: false
        });

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