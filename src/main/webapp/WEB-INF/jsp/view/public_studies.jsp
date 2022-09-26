<%--@elvariable id="user" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>

<div class="container">


    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Public Studies
                </div>
                <div class="card-body tab-content small">
                    <div id="studies" class="tab-pane active" role="tabpanel">
                        <table id="submission_table" class="display nowrwap" style="width: 100%;">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Date</th>
                                    <th>Name / Time</th>
                                    <th>External ID</th>
                                    <th>Properties</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${publicSubmissions}" var="submission">

                                <tr>
                                    <td>${submission.id}</td>
                                    <td><fmt:formatDate value="${submission.dateTime}" type="DATE" pattern="yyyy-MM-dd"/></td>
<%--                                    <td>${submission.name}</td>--%>
                                    <td><a href="${pageContext.request.contextPath}/submission/${submission.id}/">${submission.name}&nbsp</a></td>
                                    <td>${submission.externalId}</td>
                                    <td>
                                        <c:forEach items="${submission.tags}" var="tag" varStatus="status">
                                            <span id="${submission.id}color${status.index}">${tag.toString()}&nbsp;</span>
                                            <script>
                                                var spanId = '${fn:length(submission.tags)}';
                                                spanColor(${submission.id}, spanId);
                                            </script>
                                        </c:forEach>
                                    </td>

                                </tr>

                                </c:forEach>
                            </tbody>
                        </table>

                    </div>

        </div>
    </div>
</div>



<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>



<script>


    $(document).ready(function () {
       $('#submission_table').DataTable({

        });

    });


</script>