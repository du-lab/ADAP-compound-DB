<%--@elvariable id="user" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>
<script>
    $( function() {
        $( "#accordion" ).accordion({
            heightStyle: "content"
        });
    } );
</script>
<style>
    .ui-accordion-header {
        background: #b77a60;
    }
    .ui-accordion-header-active {
        background: #854e36;
    }
</style>
<div id="progressModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Delete Study</h4>
                <button type="button" class="close" data-dismiss="modal">
                    &times;
                </button>
            </div>
            <div class="modal-body">
                <p>Deleting large study may take a while. Please wait.</p>
            </div>
        </div>
    </div>
</div>
<div class="container">
    <div class="row row-content">
        <div class="col-12">
            <div class="card">
                <div class="card-header card-header-single">
                    Account
                </div>
                <div class="card-body">
                    <div align="center">
                        <div style="display: inline-block">
                            <i class="material-icons color-primary-light"
                               style="font-size:4.5em; margin: 20px;">person</i>
                        </div>
                        <div align="left" style="display: inline-block;">
                            <p id="full_username"><strong>Username:&nbsp;</strong>${user.fullUserName}</p>
                            <p><strong>E-mail:&nbsp;</strong><a href="mailto:${user.email}">${user.email}</a></p>
                            <p><strong>Role(s):&nbsp;</strong><c:forEach items="${user.roles}"
                                                                         var="role">${role.label}&nbsp;</c:forEach></p>
                            <c:if test="${not empty user.organizationId}">
                                <p><strong>Organization:&nbsp;</strong>${user.organizationUser.username}</p>
                            </c:if>
                        </div>
                    </div>
                    <div align="center">
                        <a href="${pageContext.request.contextPath}/account/changePassword" class="btn btn-secondary">Change
                            Password</a>

                        <a href="${pageContext.request.contextPath}/account/organization/${user.username}/delete/"
                           class="btn btn-secondary" style="${user.organizationId != null
                            ? '' : 'display: none;'}">
                            Leave Organization
                        </a>
                    </div>
                    <c:if test="${not user.organization and empty user.organizationId}">
                        <div align="center" style="margin-top: 10px;">

                            <a href="${pageContext.request.contextPath}/account/convertToOrganization"
                               class="btn btn-secondary">
                                Convert to Organization
                            </a>
                        </div>
                    </c:if>
                    <hr>
                    <div class="row row-content" align="center">
                        <div class="col">
                            <div class="btn-toolbar justify-content-end" role="toolbar">
                                Current Disk Space (in GB)
                                <div class="progress flex-grow-1 align-self-center mx-2">
                                    <div id="progressBar" class="progress-bar" role="progressbar"
                                         aria-valuenow=${currentDiskSpace}
                                                 aria-valuemin="0" aria-valuemax=${maxDiskSpace}></div>
                                </div>
                                Maximum
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-tabs">
                    <ul class="nav nav-tabs nav-fill nav-justified" role="tablist">
                        <li class="nav-item"><a id="studiesTab"
                                                class="nav-link active"
                                                data-toggle="tab" href="#studies">Studies</a>
                        </li>
                        <li class="nav-item"><a id="librariesTab" class="nav-link" data-toggle="tab" href="#libraries">Libraries</a></li>
                        <li class="nav-item"><a id="parametersTab" class="nav-link" data-toggle="tab" href="#parameters">Parameters</a></li>
                        <li class="nav-item"><a id="searchTaskTab" class="nav-link" data-toggle="tab" href="#searchTask">Search History</a></li>
                        <c:if test="${user.organization}">
                            <li class="nav-item"  ${user.organization ? '' : 'style="display: none;"'}>
                                <a id="organizationTab"
                                   class="nav-link"
                                   data-toggle="tab"
                                   href="#organization">
                                    Manage Organization
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>

                <%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
                <div class="card-body tab-content small">
                    <div id="studies" class="tab-pane active" role="tabpanel">
                        <table id="study_table" class="display" style="width: 100%;">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Date</th>
                                <th>Name</th>
                                <th>External ID</th>
                                <th>Properties</th>
                                <th>Chromatography Type</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <%--@elvariable id="submissionList" type="java.util.List<org.dulab.adapcompounddb.models.dto.SubmissionDTO>"--%>
                            <c:forEach items="${submissionList}" var="study" varStatus="loop">
                                <c:if test="${!study.library}">
                                    <tr>
                                        <td></td>
                                        <td><fmt:formatDate value="${study.dateTime}" type="DATE" pattern="yyyy-MM-dd"/><br/>
                                                <%--                            <small><fmt:formatDate value="${study.dateTime}" type="TIME"/></small>--%>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/submission/${study.id}/">${study.name}&nbsp;
                                                <c:if test="${study.isPrivate()}">
                                                    <span class="badge badge-info">private</span>
                                                </c:if>
                                            </a><br/>
                                                <%--                        <small>${dulab:abbreviate(study.description, 80)}</small>--%>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/submission/${study.id}/">${study.externalId}</a><br/>
                                        </td>
                                        <td>
                                                <%--                            ${study.tagsAsString}--%>
                                            <c:forEach items="${study.tags}" var="tag" varStatus="status">
                                                <span class="color-${status.index mod 5}">${tag}&nbsp;</span>
                                                <%--                                                <script>--%>
                                                <%--                                                    let spanId = '${fn:length(study.tags)}';--%>
                                                <%--                                                    spanColor(${study.id}, spanId);--%>
                                                <%--                                                </script>--%>
                                            </c:forEach>

                                        </td>
                                        <td>
                                                <%--@elvariable id="submissionIdToChromatographyListMap" type="java.util.Map<java.lang.Long, java.util.List<org.dulab.adapcompounddb.models.enums.ChromatographyType>>"--%>
                                            <c:forEach items="${submissionIdToChromatographyListMap.get(study.id)}"
                                                       var="chromatographyType">
                                                <span class="badge badge-info">${chromatographyType.label}</span>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <!-- more horiz -->
                                            <a href="${pageContext.request.contextPath}/submission/${study.id}/"><i
                                                    class="material-icons" title="View">&#xE5D3;</i></a>

                                            <!-- delete -->
                                            <a onclick="confirmDeleteDialog.show(
                                                    'Submission &quot;${study.name}&quot; and all its spectra will be deleted. Are you sure?',
                                                    '${pageContext.request.contextPath}/submission/${study.id}/delete/');">
                                                <i class="material-icons" title="Delete">&#xE872;</i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div id="libraries" class="tab-pane" role="tabpanel">
                        <table id="library_table" class="display" style="width: 100%;">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Date</th>
                                <th>Name</th>
                                <th>External ID</th>
                                <th>Properties</th>
                                <th>Chromatography Type</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach items="${submissionList}" var="study" varStatus="loop">
                                <c:if test="${study.library}">
                                    <tr>
                                        <td></td>
                                        <td><fmt:formatDate value="${study.dateTime}" type="DATE" pattern="yyyy-MM-dd"/><br/>
                                                <%--                            <small><fmt:formatDate value="${study.dateTime}" type="TIME"/></small>--%>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/submission/${study.id}/">${study.name}&nbsp</a>
                                            <c:if test="${study.isPrivate()}">
                                                <span class="badge badge-info">private</span>
                                            </c:if>
                                            <c:if test="${study.inHouseLibrary}">
                                                <span class="badge badge-success">in-house</span>
                                            </c:if>
                                                <%--                        <small>${dulab:abbreviate(study.description, 80)}</small>--%>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/submission/${study.id}/">${study.externalId}</a><br/>
                                        </td>
                                        <td>
                                                <%--                            ${study.tagsAsString}--%>
                                            <c:forEach items="${study.tags}" var="tag" varStatus="status">
                                                <span id="${study.id}color${status.index}">${tag.toString()}&nbsp;</span>
                                                <script>
                                                    var spanId = '${fn:length(study.tags)}';
                                                    spanColor(${study.id}, spanId);
                                                </script>
                                            </c:forEach>

                                        </td>
                                        <td>
                                                <%--@elvariable id="submissionIdToChromatographyListMap" type="java.util.Map<java.lang.Long, java.util.List<org.dulab.adapcompounddb.models.enums.ChromatographyType>>"--%>
                                            <c:forEach items="${submissionIdToChromatographyListMap.get(study.id)}"
                                                       var="chromatographyType">
                                                <span class="badge badge-info">${chromatographyType.label}</span>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <!-- more horiz -->
                                            <a href="${pageContext.request.contextPath}/submission/${study.id}/"><i
                                                    class="material-icons" title="View">&#xE5D3;</i></a>

                                            <!-- delete -->
                                            <a onclick="confirmDeleteDialog.show(
                                                    'Submission &quot;${study.name}&quot; and all its spectra will be deleted. Are you sure?',
                                                    '${pageContext.request.contextPath}/submission/${study.id}/delete/');">
                                                <i class="material-icons" title="Delete">&#xE872;</i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div id="parameters" class="tab-pane" role="tabpanel">
                        <%--@elvariable id="searchParametersForm" type="org.dulab.adapcompounddb.site.controllers.forms.SearchParametersForm"--%>
                        <%--@elvariable id="disableBtn" type="java.lang.Boolean"--%>
                        <form:form modelAttribute="searchParametersForm"
                                   action="${pageContext.request.contextPath}/account/saveparameters" method="POST">
                            <div id="accordion">
                                <h3>GC-MS</h3>
                                <div>
                                    <jsp:include page="../compound/user_search_parameters.jsp">
                                        <jsp:param name="PARAM_FOR" value="Gas"/>
                                        <jsp:param name="SCORE_THRESHOLD" value="${searchParameters.gas.scoreThreshold}"/>
                                        <jsp:param name="RETENTION_INDEX_TOLERANCE"
                                                   value="${searchParameters.gas.retentionIndexTolerance}"/>
                                        <jsp:param name="RETENTION_INDEX_MATCH"
                                                   value="${searchParameters.gas.retentionIndexMatch}"/>
                                        <jsp:param name="MZ_TOLERANCE" value="${searchParameters.gas.mzTolerance}"/>
                                        <jsp:param name="MATCHES_PER_SPECTRUM" value="${searchParameters.gas.limit}"/>
                                        <jsp:param name="MZ_TOLERANCE_TYPE" value="${searchParameters.gas.mzToleranceType}"/>
                                        <jsp:param name="SHOW_DIALOG" value="false"/>
                                    </jsp:include>
                                </div>
                                <h3>LC-MS</h3>
                                <div>
                                    <jsp:include page="../compound/user_search_parameters.jsp">
                                        <jsp:param name="PARAM_FOR" value="Liquid"/>
                                        <jsp:param name="SCORE_THRESHOLD" value="${searchParameters.liquid.scoreThreshold}"/>
                                        <jsp:param name="RETENTION_INDEX_TOLERANCE"
                                                   value="${searchParameters.gas.retentionIndexTolerance}"/>
                                        <jsp:param name="RETENTION_INDEX_MATCH"
                                                   value="${searchParameters.liquid.retentionIndexMatch}"/>
                                        <jsp:param name="MZ_TOLERANCE" value="${searchParameters.liquid.mzTolerance}"/>
                                        <jsp:param name="MATCHES_PER_SPECTRUM" value="${searchParameters.liquid.limit}"/>
                                        <jsp:param name="MZ_TOLERANCE_TYPE" value="${searchParameters.liquid.mzToleranceType}"/>
                                        <jsp:param name="SHOW_DIALOG" value="false"/>
                                    </jsp:include>
                                </div>
                                <h3>OTHER</h3>
                                <div>
                                    <jsp:include page="../compound/user_search_parameters.jsp">
                                        <jsp:param name="PARAM_FOR" value="Other"/>
                                        <jsp:param name="SCORE_THRESHOLD" value="${searchParameters.other.scoreThreshold}"/>
                                        <jsp:param name="RETENTION_INDEX_TOLERANCE"
                                                   value="${searchParameters.other.retentionIndexTolerance}"/>
                                        <jsp:param name="RETENTION_INDEX_MATCH"
                                                   value="${searchParameters.other.retentionIndexMatch}"/>
                                        <jsp:param name="MZ_TOLERANCE" value="${searchParameters.other.mzTolerance}"/>
                                        <jsp:param name="MATCHES_PER_SPECTRUM" value="${searchParameters.other.limit}"/>
                                        <jsp:param name="MZ_TOLERANCE_TYPE" value="${searchParameters.other.mzToleranceType}"/>
                                        <jsp:param name="SHOW_DIALOG" value="false"/>
                                    </jsp:include>
                                </div>
                            </div>
                            <div class="row row-content">
                                <div class="col">
                                    <div class="form-row">
                                        <div class="col">
                                            <div class="btn-toolbar justify-content-end" role="toolbar">
                                                <button id="searchButton" class="btn btn-primary align-self-end"
                                                        type="submit"
                                                        style="height: 100%;"
                                                        <c:if test="${disableBtn}">
                                                            <c:out value="disabled='disabled'"/>
                                                        </c:if>>
                                                    Save
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form:form>
                    </div>
                    <c:set var="members" value="${user.members}" scope="request"/>
                    <c:if test="${searchMembersList ne null and not empty searchMembersList}">
                        <c:set var="searchMembersList" value="${searchMembersList}" scope="request"/>
                    </c:if>
                    <c:if test="${user.organization}">
                        <div id="organization" class="tab-pane" role="tabpanel">
                            <jsp:include page="./organization.jsp">
                                <jsp:param name="SHOW_ORGANIZATION" value="${user.organization}"/>
                            </jsp:include>
                        </div>
                    </c:if>
                    <div id="searchTask" class="tab-pane" role="tabpanel">
                        <table id="search_task_table" class="display" style="width: 100%;">
                            <thead>
                            <tr>
                                <th>Submission</th>
                                <th>Status</th>
                                <th>Time</th>
                                <th>Libraries Searched Against</th>
                                <th>Software Version</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${searchTaskList}" var="searchTask" varStatus="loop">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/submission/${searchTask.submission.id}/">${searchTask.submission.name}</a><br/>
                                    </td>
                                    <td>${searchTask.status}</td>
                                    <td><fmt:formatDate value="${searchTask.dateTime}" type="both" timeStyle="short"
                                                        pattern="yyyy-MM-dd HH:mm:ss"/><br/></td>
                                    <td>
                                        <c:forEach items="${searchTask.libraries}" var="library" varStatus="loop">
                                            <c:choose>
                                                <c:when test="${library.key >0}">
                                                    <a href="${pageContext.request.contextPath}/submission/${library.key}/">${library.value}</a><br/>
                                                </c:when>
                                                <c:otherwise>
                                                    ${library.value}<br/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </td>
                                    <td>
                                        ${appVersion}
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${searchTask.status == 'RUNNING' && sessionScope[dulab:groupSearchResultsAttributeName()] != null}">
                                                <a href="${pageContext.request.contextPath}/group_search/" type="button"
                                                   >View Matches</a>
                                            </c:when>
                                            <c:when test="${searchTask.status == 'FINISHED'}">
                                                <a href="${pageContext.request.contextPath}/submission/group_search/${searchTask.submission.id}"
                                                   >View Matches</a>
                                            </c:when>
                                            <c:otherwise>
                                            </c:otherwise>
                                        </c:choose>

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
    <div class = "row row-content no-background">
        <div class = "col">
            <div class = "card" style = "background-color:transparent; border:none;">
                <div class = "card-body " style = "display: flex; justify-content: space-between; padding:0px;">
                        <a href="${pageContext.request.contextPath}/file/upload/" class="btn btn-primary">New Study</a>
                        <a class="btn btn-danger" onclick="confirmDeleteDialog.show(
                                'Your current account &quot;${user.name}&quot; will be deleted. Are you sure?',
                                '${pageContext.request.contextPath}/user/${user.id}/delete/');">
                            Delete Account
                        </a>
                </div>
            </div>
        </div>
    </div>
</div>





<div id="dialog-confirm"></div>

<script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.13.2/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/saveTabSelection.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>

<script>
    var confirmDeleteDialog = $('#dialog-confirm').confirmDeleteDialog();

    $(document).ready(function () {
        var t1 = $('#study_table').DataTable({
            order: [[1, 'DESC']],
            responsive: true,
            scrollX: true,
            scroller: true,
            columnDefs: [
                {
                    targets: 0,
                    sortable: false
                },
                {
                    targets: 4,
                    sortable: false

                }/*,
                {
                    "className": "dt-center", "targets": "_all"
                }*/],
        });

        var t2 = $('#library_table').DataTable({
            order: [[1, 'DESC']],
            responsive: true,
            scrollX: true,
            scroller: true,
            columnDefs: [
                {
                    targets: 0,
                    sortable: false
                },
                {
                    targets: 4,
                    sortable: false

                }/*,
                {
                    "className": "dt-center", "targets": "_all"
                }*/],
        });
        var t3 = $('#search_task_table').DataTable({
            order: [[2, 'DESC']],
            responsive: true,
            scrollX: true,
            scroller: true,

        });
        t1.on('order.dt search.dt', function () {
            t1.column(0, {search: 'applied', order: 'applied'}).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        }).draw();

        t2.on('order.dt search.dt', function () {
            t2.column(0, {search: 'applied', order: 'applied'}).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        }).draw();


        const valuenow = parseFloat($("#progressBar").attr("aria-valuenow"));
        const valuemax = parseFloat($("#progressBar").attr("aria-valuemax"));

        const widthpercent = (valuenow / valuemax) * 100;
        const width = widthpercent + '%'
        const progressBar = $('#progressBar')
            .css('width', width)
            .attr('aria-valuenow', widthpercent)
            .html(valuenow.toFixed(2));
        // if (0 < widthpercent && widthpercent < 100)
        //     progressBar.addClass('progress-bar-striped progress-bar-animated');
        // else {
        //     progressBar.removeClass('progress-bar-striped progress-bar-animated');
        // }

    });
    var t3 = $('#organization_table').DataTable({
        order: [[1, 'DESC']],
        responsive: true,
        scrollX: true,
        scroller: true,
        columnDefs: [
            {
                targets: 0,
                sortable: false
            },
            {
                targets: 4,
                sortable: false

            }/*,
                {
                    "className": "dt-center", "targets": "_all"
                }*/],
    });

    t3.on('order.dt search.dt', function () {
        t3.column(0, {search: 'applied', order: 'applied'}).nodes().each(function (cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();
    // Adjust column widths when a table becomes visible
    $(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
        $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
    });
</script>