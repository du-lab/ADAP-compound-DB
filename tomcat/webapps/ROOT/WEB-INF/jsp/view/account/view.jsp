<%--@elvariable id="submissionList" type="java.util.List<org.dulab.adapcompounddb.models.entities.Submission>"--%>
<%--@elvariable id="user" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>

<section>
    <h1>Account</h1>
    <div align="center">
        <div style="display: inline-block">
            <i class="material-icons color-primary-light" style="font-size:4.5em; margin: 20px;">person</i>
        </div>
        <div align="left" style="display: inline-block;">
            <p><strong>Username:&nbsp;</strong>${user.username}</p>
            <p><strong>E-mail:&nbsp;</strong><a href="mailto:${user.email}">${user.email}</a></p>
            <p><strong>Role(s):&nbsp;</strong><c:forEach items="${user.roles}"
                                                         var="role">${role.label}&nbsp;</c:forEach></p>
        </div>
    </div>
    <div align="center">
        <a href="${pageContext.request.contextPath}/account/changePassword" class="button">Changing Password</a>
    </div>
</section>

<section>
    <h1>Studies</h1>
    <div align="center">
        <table id="study_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>ID</th>
                <th>Date</th>
                <th>Name</th>
                <th>External ID</th>
                <th>Properties</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${submissionList}" var="study" varStatus="loop">
                <tr>
                    <td></td>
                    <td><fmt:formatDate value="${study.dateTime}" type="DATE" pattern="yyyy-MM-dd"/><br/>
                            <%--                            <small><fmt:formatDate value="${study.dateTime}" type="TIME"/></small>--%>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/submission/${study.id}/">${study.name}</a><br/>
                        <small>${dulab:abbreviate(study.description, 80)}</small>
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
                                spanColor( ${study.id}, spanId );
                            </script>
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
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section class="no-background">
    <div align="center">
        <a href="${pageContext.request.contextPath}/file/upload/" class="button">New Study</a>
    </div>
</section>

<div id="dialog-confirm"></div>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script>
    var confirmDeleteDialog = $( '#dialog-confirm' ).confirmDeleteDialog();

    $( document ).ready( function () {
        var t = $( '#study_table' ).DataTable( {
            order: [[1, 'DESC']],
            responsive: true,
            scrollX: true,
            scroller: true,
            columnDefs: [
                {
                    targets:0,
                    sortable: false
                },
                {
                targets:4,
                sortable: false

            }/*,
                {
                    "className": "dt-center", "targets": "_all"
                }*/],
        } );

        t.on( 'order.dt search.dt', function () {
            t.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
                cell.innerHTML = i+1;
            } );
        } ).draw();
    } );
</script>