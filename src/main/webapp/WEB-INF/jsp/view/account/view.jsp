<%--@elvariable id="submissionList" type="java.util.List<org.dulab.adapcompounddb.models.entities.Submission>"--%>
<%--@elvariable id="user" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

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
	                <th></th>
	            </tr>
            </thead>
            <tbody>
	            <c:forEach items="${submissionList}" var="submission">
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
	                        <%--<c:forEach items="${submission.tags}" var="tag">${tag.id.name}&nbsp;</c:forEach>--%>
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
	            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section class="no-background">
    <div align="center">
        <a href="${pageContext.request.contextPath}/file/upload/" class="button">New Submission</a>
    </div>
</section>

<div id="dialog-confirm"></div>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/confirm-delete-dialog.js"/>"></script>
<script>
    var confirmDeleteDialog = $('#dialog-confirm').confirmDeleteDialog();

    $(document).ready(function () {
        $('#submission_table').DataTable({
            order: [[1, 'DESC']],
            columnDefs: [{
                targets: [3, 4],
                sortable: false
            }]
        });
    });
</script>