<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section>

    <div style="text-align: center">
        <table id="match_table" class="display responsive" style="width: 100%; clear:none;">
            <thead>
            <tr>
                <th></th>
                <th title="study external ID">Study ID</th>
                <th title="Match submission Name">Match Submission Name</th>
                <th title="study tags">Study Tags</th>
                <th title="Match Score">Matching Score</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${match_submissions}" var="match_submission" varStatus="theCount">
                <tr>
                    <td>${theCount.count}</td>
                    <td><c:out value="${match_submission.externalId}"/></td>
                    <td><c:out value="${match_submission.submissionName}"/></td>
                    <td><c:out value="${match_submission.studyTag}"/></td>
                    <td><c:out value="${match_submission.score}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

    </div>

</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>

<script>

    $(document).ready(function () {
        var t = $('#match_table').DataTable({
            ordering:true,
            order: [[0,'asc'],[4, 'desc']],
            responsive: true,
            scrollX: true,
            scroller: true,
            columnDefs: [
                {
                    targets: 0,
                    sortable: false
                },
                {
                    targets: 1,
                    sortable: false
                },
                {
                    targets: 2,
                    sortable: false
                },
                {
                    targets: 3,
                    sortable: false
                },
                {
                    targets: 4,
                    sortable: false
                }
            ],
        });
    });
</script>