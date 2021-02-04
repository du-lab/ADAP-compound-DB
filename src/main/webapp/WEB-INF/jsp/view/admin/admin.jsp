<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddv.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="availableUserRoles" type="org.dulab.adapcompound.models.UserRole[]"--%>
<%--@elvariable id="clusters" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumCluster>"--%>
<%--@elvariable id="users" type="java.util.List<org.dulab.adapcompounddb.models.entities.UserPrinicpal>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="deleteUserModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Delete user</h4>
                <button type="button" class="close" data-dismiss="modal">
                    &times;
                </button>
            </div>
            <div class="modal-body">
                <p>User &quot;<span id="username"></span>&quot; and all user's studies will be deleted. Are you sure?
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <a id="deleteUserButton" type="button" class="btn btn-primary" href="#">Delete</a>
            </div>
        </div>
    </div>
</div>

<div id="deleteSubmissionModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Delete user</h4>
                <button type="button" class="close" data-dismiss="modal">
                    &times;
                </button>
            </div>
            <div class="modal-body">
                <p>Submission &quot;<span id="submissionName"></span>&quot; and all its spectra will be deleted. Are you
                    sure?
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <a id="deleteSubmissionButton" type="button" class="btn btn-primary" href="#">Delete</a>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-tabs">
                    <ul class="nav nav-tabs nav-fill nav-justified" role="tablist">
                        <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#tools">Tools</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#users">Users</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#submissions">Studies</a>
                        </li>
                    </ul>
                </div>

                <div class="card-body tab-content">
                    <div id="tools" class="tab-pane active" role="tabpanel">
                        <div class="container small">
                            <div class="row row-content">
                                <div id="progressBarDiv" class="progress_bar"></div>
                            </div>
                            <div class="row row-content">
                                <h4 class="col text-center">Number of Spectra in Knowledgebase</h4>
                            </div>

                            <div class="row row-content">
                                <div class="col">
                                    <table class="mx-auto">
                                        <tr>
                                            <th></th>
                                            <th>Total</th>
                                            <th>Clusterable</th>
                                            <th>Consensus</th>
                                            <th>Reference</th>
                                            <th>Other</th>
                                            <th>Matches</th>
                                        </tr>
                                        <%--@elvariable id="statistics" type="java.util.Map<org.dulab.adapcompounddb.models.enums.ChromatographyType, org.dulab.adapcompounddb.models.Statistics>"--%>
                                        <c:forEach items="${statistics}" var="mapEntry">
                                            <tr>
                                                <td>${mapEntry.key.label}</td>
                                                <td>${mapEntry.value.numSpectra}</td>
                                                <td>${mapEntry.value.numClusterableSpectra}</td>
                                                <td>${mapEntry.value.numConsensusSpectra}</td>
                                                <td>${mapEntry.value.numReferenceSpectra}</td>
                                                <td>${mapEntry.value.numOtherSpectra}</td>
                                                <td>${mapEntry.value.numSpectrumMatches}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                            </div>

                            <div class="row row-content">
                                <h4 class="col text-center">Admin Tools</h4>
                            </div>

                            <div class="row row-content">
                                <div class="col">
                                    <div class="btn-toolbar" role="toolbar">
                                        <button id="calculateMatchButton" type="button" class="btn btn-primary mr-2">
                                            Calculate Matching Scores
                                        </button>
                                        <div class="progress flex-grow-1 align-self-center mx-2">
                                            <div id="calculateMatchProgressBar" class="progress-bar" role="progressbar"
                                                 aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row row-content">
                                <div class="col">
                                    <div class="btn-toolbar" role="toolbar">
                                        <button id="clusterButton" type="button" class="btn btn-primary mr-2">
                                            Cluster Spectra
                                        </button>
                                        <div class="progress flex-grow-1 align-self-center mx-2">
                                            <div id="clusterProgressBar" class="progress-bar" role="progressbar"
                                                 aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="users" class="tab-pane" role="tabpanel">
                        <div class="row row-content">
                            <div class="col">
                                <table id="user_table" class="display compact" style="width: 100%;">
                                    <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Email</th>
                                        <c:forEach items="${availableUserRoles}"
                                                   var="role">
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
                                            <c:forEach items="${availableUserRoles}"
                                                       var="role">
                                                <td><c:if
                                                        test="${user.roles.contains(role)}">
                                                    <i class="material-icons">check</i>
                                                </c:if></td>
                                            </c:forEach>
                                            <td>
                                                <a data-username="${user.name}" data-userid="${user.id}" href="#"
                                                   data-toggle="modal" data-target="#deleteUserModal">
                                                    <i class="material-icons">delete</i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div id="submissions" class="tab-pane" role="tabpanel">
                        <div class="row row-content">
                            <div class="col small">
                                <table id="submission_table" class="display compact" style="width: 100%;">
                                    <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Date / Time</th>
                                        <th>Name</th>
                                        <th>External ID</th>
                                        <th>User</th>
                                        <th>Properties</th>
                                        <th>Clusterable</th>
                                        <th>Reference</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%--<div id="confirm-delete-dialog"></div>--%>
<%--<div id="progress-dialog"></div>--%>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<%--<script src="<c:url value="/resources/npm/node_modules/bootstrap4-toggle/js/bootstrap4-toggle.min.js"/>"></script>--%>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<%--<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>--%>
<%--<script src="<c:url value="/resources/AdapCompoundDb/js/progressBar.js"/>"></script>--%>
<script>

    $(document).ready(function () {
        // $('#cluster_table').DataTable();
        $('#user_table').DataTable({
            scrollX: true,
            scroller: true
        });

        $('#submission_table').DataTable({
            "order": [[1, "desc"]],
            serverSide: true,
            processing: true,
            scrollX: true,
            scroller: true,
            ajax: {
                url: "${pageContext.request.contextPath}/admin/get/submissions.json",

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
                        href  = `${pageContext.request.contextPath}/submission/\${row.id}/`;
                        privateBadge = (row.private) ? `<span class="badge badge-info">private</span>` : '';
                        return `<a href="\${href}">\${row.name}&nbsp;\${privateBadge}</a>`;
                    }
                },
                {
                    "targets": 3,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        if (row.externalId == null) {
                            content = ''
                        } else {
                            content = row.externalId;
                        }
                        return content;
                    }
                },
                {
                    "targets": 4,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        content = row.userName + '<br/><small>' + row.userEMail + '<small>';
                        return content;
                    }
                },
                {
                    "targets": 5,
                    "orderable": false,
                    "data": "tagsAsString"
                },
                {
                    "targets": 6,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        const text = (row.clusterable) ? 'Yes' : 'No';
                        const href = `${pageContext.request.contextPath}/admin/set/submission/\${row.id}/clusterable/\${!row.clusterable}`;
                        return `<a href="\${href}">\${text}</a>`
                    }
                },
                {
                    "targets": 7,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        const text = (row.reference) ? 'Yes' : 'No';
                        const href = `${pageContext.request.contextPath}/admin/set/submission/\${row.id}/reference/\${!row.reference}`;
                        return `<a href="\${href}">\${text}</a>`
                    }
                },
                {
                    "targets": 8,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        return `<a href="${pageContext.request.contextPath}/submission/\${row.id}/">
                                <i class="material-icons" title="View">&#xE5D3;</i></a>
                                <a data-submissionname="\${row.name}" data-submissionid="\${row.id}" href="#"
                                   data-toggle="modal" data-target="#deleteSubmissionModal">
                                   <i class="material-icons">delete</i></a>`;
                    }
                }
            ]

        });

        // Adjust column widths when a table becomes visible
        $(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
            $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
        });

        $('#deleteUserModal').on('show.bs.modal', function (event) {
            const button = $(event.relatedTarget);
            const userName = button.data('username');
            const userId = button.data('userid');

            const modal = $(this);
            modal.find('#username').text(userName);
            modal.find('#deleteUserButton').attr('href', `${pageContext.request.contextPath}/user/\${userId}/delete`);
        });

        $('#deleteSubmissionModal').on('show.bs.modal', function (event) {
            const button = $(event.relatedTarget);
            const submissionName = button.data('submissionname');
            const submissionId = button.data('submissionid');

            const modal = $(this);
            modal.find('#submissionName').text(submissionName);
            modal.find('#deleteSubmissionButton').attr(
                'href', `${pageContext.request.contextPath}/submission/\${submissionId}/delete/`);
        });

        const matchButton = $('#calculateMatchButton');
        const clusterButton = $('#clusterButton');
        const matchProgressBar = $('#calculateMatchProgressBar');
        const clusterProgressBar = $('#clusterProgressBar');

        matchButton.click(function () {
            // clusterButton.attr("disabled", "disabled");
            // matchButton.attr("disabled", "disabled");
            $.ajax({url: "admin/calculatescores"});
            // }).done(function () {
            //     matchProgressBar.removeClass("hide");
            //     scoreProgressBar.start();
            // });
        });

        $(clusterButton).click(function () {
            // $(clusterButton).attr("disabled", "disabled");
            // $(matchButton).attr("disabled", "disabled");
            $.ajax({url: "admin/cluster"});
            //     .done(function () {
            //     $("#cluster_progress").removeClass("hide");
            //     clusterProgressBar.start();
            // });
        });

        setInterval(function () {
            $.getJSON(window.location.href + 'calculatescores/progress', function (x) {
                const width = x + '%';
                const progressBar = $('#calculateMatchProgressBar')
                    .css('width', width)
                    .attr('aria-valuenow', x)
                    .html(width);
                if (0 < x && x < 100)
                    progressBar.addClass('progress-bar-striped progress-bar-animated');
                else {
                    progressBar.removeClass('progress-bar-striped progress-bar-animated');
                }
            });

            $.getJSON(window.location.href + 'cluster/progress', function (x) {
                const width = x + '%';
                const progressBar = $('#clusterProgressBar')
                    .css('width', width)
                    .attr('aria-valuenow', x)
                    .html(width);
                if (0 < x && x < 100)
                    progressBar.addClass('progress-bar-striped progress-bar-animated');
                else {
                    progressBar.removeClass('progress-bar-striped progress-bar-animated');
                }
            });
        }, 1000);
    });
</script>