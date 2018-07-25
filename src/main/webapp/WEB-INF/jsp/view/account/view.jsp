<%--@elvariable id="submissionList" type="java.util.List<org.dulab.adapcompounddb.models.entities.Submission>"--%>
<%--@elvariable id="user" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section>
    <h1>Account</h1>
    <p>Username: <span class="highlighted">${user.username}</span></p>
    <p>E-mail: <span class="highlighted">${user.email}</span></p>
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
                        <c:forEach items="${submission.tags}" var="tag">${tag.id.name}&nbsp;</c:forEach>
                            <%--<small>${submission.chromatographyType.label}</small>--%>
                    </td>
                    <td>
                        <!-- more horiz -->
                        <a href="${pageContext.request.contextPath}/submission/${submission.id}/"><i
                                class="material-icons" title="View">&#xE5D3;</i></a>

                        <!-- delete -->
                        <a href="${pageContext.request.contextPath}/submission/${submission.id}/delete/"><i
                                class="material-icons" title="Delete">&#xE872;</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#submission_table').DataTable({
            order: [[1, 'DESC']],
            columnDefs: [{
                targets: [3, 4],
                sortable: false
            }]
        });
    })
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>