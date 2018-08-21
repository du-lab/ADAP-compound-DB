<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submissionDTO" type="org.dulab.adapcompounddb.models.entities.submissionDTO"--%>
<%--@elvariable id="authorized" type="java.lang.Boolean"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<!-- submissionDTO information -->

<c:if test="${!edit}">
    <section>
        <h1>submissionDTO</h1>
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
                    <td>${submissionDTO.name}</td>
                </tr>
                <tr>
                    <td><strong>Description:</strong></td>
                    <td><pre style="white-space: pre-wrap;">${submissionDTO.description}</pre></td>
                </tr>
                <c:if test="${submissionDTO.reference != null}">
                    <tr>
                        <td><strong>URL:</strong></td>
                        <td><a href="${submissionDTO.reference}" title="${submissionDTO.reference}" target="_blank">${dulab:abbreviate(submissionDTO.reference, 80)}</a></td>
                    </tr>
                </c:if>
                <c:if test="${submissionDTO.tags > 0}">
                    <tr>
                        <td><strong>Equipment:</strong></td>
                        <td>${submissionDTO.tags}</td>
                    </tr>
                </c:if>
                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <tr>
                        <td><strong>${type.label}:</strong></td>
                        <td>${submissionDTO.getCategory(type)}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </section>
</c:if>

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
        <c:forEach items="${submissionDTO.files}" var="file" varStatus="loop">
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


<c:choose>
    <c:when test="${edit}">
       <jsp:include page="../../includes/submission_form.jsp">
           <jsp:param value="${submissionDTO}" name="submissionDTO"/>
       </jsp:include>
        <%-- <section>
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
                    <form:form method="POST" modelAttribute="submissionDTO">
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
                                <c:when test="${submissionDTO.id > 0}">
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
        </section> --%>
    </c:when>
    <c:when test="${authorized}">
        <section class="no-background">
            <div align="center">
                <a href="edit" class="button">Edit Submission</a>
            </div>
        </section>
    </c:when>
</c:choose>
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
                url: "${pageContext.request.contextPath}/spectrum/findSpectrumBySubmissionId.json?submissionId=${submissionDTO.id}",

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
                {"data": "retentionTime", "targets": 2}, //<img src="${pageContext.request.contextPath}/${spectrum.chromatographyType.iconPath}" alt="${spectrum.chromatographyType.label}" title="${spectrum.chromatographyType.label}" class="icon"/>
                {"data": "precursor", "targets": 3},
                {
                    "orderable": false,
                    "targets": 4,
                    "render": function (data, type, row, meta) {
                        content = '<img' +
                        ' src="${pageContext.request.contextPath}/' + row.chromatographyTypeIconPath + '"'
                        + ' alt="' + row.chromatographyTypeLabel + '"'
                        + ' title="' + row.chromatographyTypeLabel + '"'
                        + ' class="icon"/>';

                        return content;
                    }
                },
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

        // Table with submissionDTO information
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

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>