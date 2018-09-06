<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddv.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="availableUserRoles" type="org.dulab.adapcompound.models.UserRole[]"--%>
<%--@elvariable id="statistics" type="java.util.Map<org.dulab.adapcompounddb.models.ChromatographyType, org.dulab.adapcompounddb.models.Statistics>"--%>
<%--@elvariable id="clusters" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumCluster>"--%>
<%--@elvariable id="users" type="java.util.List<org.dulab.adapcompounddb.models.entities.UserPrinicpal>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <div>
        <div id="progressBarDiv" class="progress_bar"></div>
    </div>
</section>

<section>
    <h1>Number of Spectra in Library</h1>
    <div align="center">
        <table>
            <tr>
                <th></th>
                <th>Submitted</th>
                <th>Unmatched</th>
                <th>Consensus</th>
                <th>Matches</th>
            </tr>
            <c:forEach items="${statistics}" var="mapEntry">
                <tr>
                    <td>${mapEntry.key.label}</td>
                    <td>${mapEntry.value.numSubmittedSpectra}</td>
                    <td>${mapEntry.value.numUnmatchedSpectra}</td>
                    <td>${mapEntry.value.numConsensusSpectra}</td>
                    <td>${mapEntry.value.numSpectrumMatches}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Admin Tools</h1>
    <div>
        <table>
            <tr>
                <td><a href="calculatescores/"
                       class="button"
                       onclick="progressBar.start('calculatescores/progress')">Calculate Matching Scores...</a></td>
                <td>Calculates matching scores for all spectra in the library</td>
            </tr>
            <tr>
                <td><a id="button-cluster" href="cluster/" class="button">Cluster spectra...</a></td>
                <td>Cluster spectra into clusters</td>
            </tr>
        </table>
    </div>
</section>

<section>
    <h1>Clusters</h1>
    <div align="center">
        <table id="cluster_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>ID</th>
                <th title="Consensus spectrum">Consensus</th>
                <th title="Number of spectra in a cluster">Count</th>
                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                <th title="Average, minimum, and maximum values of the statistical significance">Significance</th>
                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <th>${type.label} Diversity</th>
                </c:forEach>
                <th title="Chromatography type">Type</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${clusters}" var="cluster">
                <tr>
                    <td>${cluster.id}</td>
                    <td><a href="/cluster/${cluster.id}/">${cluster.consensusSpectrum.name}</a></td>
                    <td>${cluster.size}</td>
                    <td>${dulab:toIntegerScore(cluster.diameter)}</td>
                    <td title="Ave: ${cluster.aveSignificance}; Min: ${cluster.minSignificance}; Max: ${cluster.maxSignificance}">
                        <c:if test="${cluster.aveSignificance != null}">
                            <fmt:formatNumber type="number" maxFractionDigits="2"
                                              value="${cluster.aveSignificance}"/><br/>
                        </c:if>
                    </td>

                    <c:forEach items="${submissionCategoryTypes}" var="type">
                        <td>
                            <c:forEach items="${cluster.diversityIndices}" var="diversityIndex">
                                <c:if test="${diversityIndex.id.categoryType == type}">
                                    <fmt:formatNumber type="number" maxFractionDigits="3"
                                                      value="${diversityIndex.diversity}"/>
                                </c:if>
                            </c:forEach>
                        </td>
                    </c:forEach>

                    <td><img src="${pageContext.request.contextPath}/${cluster.consensusSpectrum.chromatographyType.iconPath}"
                             alt="${cluster.consensusSpectrum.chromatographyType.name()}"
                             title="${cluster.consensusSpectrum.chromatographyType.label}"
                             class="icon"/></td>
                    <td>
                        <!--more horiz-->
                        <a href="/cluster/${cluster.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section>
    <h1>Users</h1>
    <div align="center">
        <table id="user_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>User</th>
                <th>Email</th>
                <c:forEach items="${availableUserRoles}" var="role">
                    <th>${role.label}</th>
                </c:forEach>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${users}" var="user">
                <tr>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <c:forEach items="${availableUserRoles}" var="role">
                        <td><c:if test="${user.roles.contains(role)}">
                            <i class="material-icons">check</i>
                        </c:if></td>
                    </c:forEach>
                    <td>
                        <a onclick="confirmDeleteDialog.show(
                                'User &quot;${user.name}&quot; and all user\'s submissions will be deleted. Are you sure?',
                                '${pageContext.request.contextPath}/user/${user.id}/delete');">
                            <i class="material-icons">delete</i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section>
    <h1>Submissions</h1>
    <div align="center">
        <table id="submission_table" class="display" style="width: 100%;">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Date / Time</th>
                    <th>Name</th>
                    <th>Properties</th>
                    <th>Reference (Off/On)</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <%-- <c:forEach items="${submissionList}" var="submission">
                    <tr>
                        <td>${submission.id}</td>
                        <td><fmt:formatDate value="${submission.dateTime}" type="DATE" pattern="yyyy-MM-dd"/><br/>
                            <small><fmt:formatDate value="${submission.dateTime}" type="TIME"/></small>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/submission/${submission.id}/">${submission.name}</a><br/>
                            <small>${dulab:abbreviate(submission.description, 80)}</small>
                        </td>
                        <td>
                            ${submission.tagsAsString}
                            <c:forEach items="${submission.tags}" var="tag">${tag.id.name}&nbsp;</c:forEach>
                        </td>
                        <td>
                            <!-- more horiz -->
                            <a href="${pageContext.request.contextPath}/submission/${submission.id}/"><i
                                    class="material-icons" title="View">&#xE5D3;</i></a>

                            <!-- delete -->
                            <a onclick="confirmDeleteDialog.show(
                                    'Submission &quot;${submission.name}&quot; and all its spectra will be deleted. Are you sure?',
                                    '${pageContext.request.contextPath}/submission/${submission.id}/delete/');">
                                <i class="material-icons" title="Delete">&#xE872;</i>
                            </a>
                        </td>
                    </tr>
                </c:forEach> --%>
            </tbody>
        </table>
    </div>
</section>

<div id="confirm-delete-dialog"></div>
<div id="progress-dialog"></div>

<!-- End the middle column -->

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/progressBar.js"/>"></script>
<script>
    var progressBar = new ProgressBar('progressBarDiv');
    var confirmDeleteDialog = $('#confirm-delete-dialog').confirmDeleteDialog();
    var progressDialog = $('#progress-dialog').progressDialog();

    $(document).ready(function () {
        $('#cluster_table').DataTable();
        $('#user_table').DataTable();
    });

    $('#submission_table').DataTable({
        serverSide: true,
        processing: true,
        ajax: {
            url: "${pageContext.request.contextPath}/submission/findAllSubmissions.json",

            data: function (data) {
                data.column = data.order[0].column;
                data.sortDirection = data.order[0].dir;
                data.search = data.search["value"];
            }
        },
        "columnDefs": [
            {
                "targets": 0,
                "orderable": true,
                "data": "id"
            },
            {
                "targets": 1,
                "orderable": true,
                "data": "formattedDate"
            },
            {
                "targets": 2,
                "orderable": true,
                "render": function (data, type, row, meta) {
                    content = '<a href="${pageContext.request.contextPath}/submission/' + row.id + '/">' +
                        row.name +
                        '</a>';
                    return content;
                }
            },
            {
                "targets": 3,
                "orderable": false,
                "data": "tagsAsString"
            },
            {
                "targets": 4,
                "orderable": false,
                "render": function (data, type, row, meta) {
                    var content = '<label class="switch" id="reference_checkbox">' +
                        '<input type="checkbox" value="' + row.id + '" ';
                    if(row.allSpectrumReference == 1) {
                    	content += 'checked'
                    }
                    content += '><span class="checkbox-slider ';
                    if(row.allSpectrumReference == null) {
                    	content += 'translate-middle';
                    }
                    content += '"></span></label>';
                    return content;
                }
            },
            {
                "targets": 5,
                "orderable": false,
                "render": function (data, type, row, meta) {
                    var clickEve = "confirmDeleteDialog.show(" +
                        "'Submission &quot;" + row.name + "&quot; and all its spectra will be deleted. Are you sure?'," +
                        "'${pageContext.request.contextPath}/submission/" + row.id + "/delete/');";
                    var content = '<a href="${pageContext.request.contextPath}/submission/' + row.id + '/">' +
                        '<i class="material-icons" title="View">&#xE5D3;</i>' +
                        '</a>' +
                        '<a onclick="' + clickEve + '">' +
                        '<i class="material-icons" title="Delete">&#xE872;</i></a>';

                    return content;
                }
            }
        ]
    }).on('draw', function() {
        $("#reference_checkbox > input[type='checkbox']").each(function() {
            $(this).on("change", function() {
                $(this).parent().find("span.checkbox-slider").removeClass("translate-middle");
                updateReferenceOfAllSpectraOfSubmission($(this).val(), $(this).is(":checked"));
            });
        });
    });

    function updateReferenceOfAllSpectraOfSubmission(submissionId, reference) {
        $.ajax({
              url: "${pageContext.request.contextPath}/spectrum/updateReferenceOfAllSpectraOfSubmission",
              type: "GET",
              contentType: 'application/json; charset=utf-8',
              data: {"value": reference, "submissionId": submissionId},
              success: function (r) {
              },
              error: function (xhr) {
                  alert('Error while selecting list..!!');
              }
        });
    }

    $('#button-cluster').click(function () {
        progressDialog.show('Clustering may take a while. Please wait...');
    })
</script>