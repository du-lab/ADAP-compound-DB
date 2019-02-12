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
    <div class="tabbed-pane">
        <span class="active" data-tab="tools">Tools</span>
        <span data-tab="users">All Users</span>
        <span data-tab="submissions">All Submissions</span>
    </div>

    <div id="tools">
        <div>
            <div id="progressBarDiv" class="progress_bar"></div>
        </div>
        <h2>Number of Spectra in Library</h2>
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

        <h3>Admin Tools</h3>
        <div>
            <table style="width: 100%;">
                <thead>
                    <tr><th class="desktop" style="width: 30%;"></th><th style="width: 25%;"></th><th></th></tr>
                </thead>
                <tbody>
                    <tr>
                        <td class="desktop">Calculates matching scores for all spectra in the library</td>
                        <td>
                            <!-- <a href="calculatescores/" class="button"
                                onclick="progressBar.start('calculatescores/progress')">Calculate Matching Scores...</a> -->
                            <button class="button" id="calculate_match_button" style="width: 100%;">Calculate Matching Scores</button>
                        </td>
                        <td>
                            <progress id="match_progress" value="0" max="100" style="width:100%; height: 1.4em;"></progress>
                        </td>
                    </tr>
                    <tr>
                        <td class="desktop">Cluster spectra into clusters</td>
                        <td>
                            <!-- <a id="button-cluster" href="cluster/"
                                class="button">Cluster spectra...</a> -->
                            <button class="button" id="cluster_button" style="width: 100%;">Cluster spectra</button>
                        </td>
                        <td>
                            <progress id="cluster_progress" value="0" max="100" style="width:100%; height: 1.4em;"></progress>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div id="users" class="hide">
        <div align="center">
            <table id="user_table" class="display responsive" style="width: 100%;">
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
                            <td><a onclick="confirmDeleteDialog.show(
                                'User &quot;${user.name}&quot; and all user\'s submissions will be deleted. Are you sure?',
                                '${pageContext.request.contextPath}/user/${user.id}/delete');">
                                    <i class="material-icons">delete</i>
                            </a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div id="submissions" align="center" class="hide">
        <table id="submission_table" class="display responsive"
            style="width: 100%;">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Date / Time</th>
                    <th>Name</th>
                    <th>User</th>
                    <th>Properties</th>
                    <th>Reference (Off/On)</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
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
<script type="text/javascript" src="<c:url value="/resources/AdapCompoundDb/js/tabs.js"/>"></script>
<script>
    $(".tabbed-pane").each(function() {
        $(this).tabbedPane();
    });
    var confirmDeleteDialog = $('#confirm-delete-dialog').confirmDeleteDialog();
    var progressDialog = $('#progress-dialog').progressDialog();

    $(document).ready(function () {
        $('#cluster_table').DataTable();
        $('#user_table').DataTable();
    });

    $('#submission_table').DataTable({
        "order": [[1, "desc"]],
        serverSide: true,
        processing: true,
        responsive: true,
        scrollX: true,
        scroller: true,
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
                "orderable": true,
                "render": function (data, type, row, meta) {
                    content = row.user.name + '<br/><small>' + row.user.email + '<small>';
                    return content;
                }
            },
            {
                "targets": 4,
                "orderable": false,
                "data": "tagsAsString"
            },
            {
                "targets": 5,
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
                "targets": 6,
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



    var clusterButton = $('#cluster_button');
    var matchButton = $('#calculate_match_button');
    $(clusterButton).attr("disabled", "disabled");
    $(matchButton).attr("disabled", "disabled");

    var counter = 0;
    var buttonHandler = function() {
        counter++;
        if(counter >= 2) {
            $(clusterButton).removeAttr("disabled");
            $(matchButton).removeAttr("disabled");
        }
    }
    var scoreProgressBar = new ProgressBar('calculatescores/progress', 'match_progress', 3000, buttonHandler);
    var clusterProgressBar = new ProgressBar('cluster/progress', 'cluster_progress', 500, buttonHandler);

    scoreProgressBar.start();
    clusterProgressBar.start();

    $(matchButton).click(function () {
        $(clusterButton).attr("disabled", "disabled");
        $(matchButton).attr("disabled", "disabled");
        $.ajax({
            url: "admin/calculatescores"
        }).done(function() {
            $("#match_progress").removeClass("hide");
            scoreProgressBar.start();
        });
    });

    $(clusterButton).click(function () {
        $(clusterButton).attr("disabled", "disabled");
        $(matchButton).attr("disabled", "disabled");
        $.ajax({
            url: "admin/cluster"
        }).done(function() {
            $("#cluster_progress").removeClass("hide");
            clusterProgressBar.start(); 
        });
    });
</script>