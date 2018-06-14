<%--@elvariable id="diseases" type="java.util.List<org.dulab.adapcompounddb.site.controllers.ControllerUtils.CategoryWithSubmissionCount>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section>
    <h1>Diseases</h1>
    <div align="center">
        <table id="disease_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>Id</th>
                <th>Disease</th>
                <th>Description</th>
                <th>Submissions</th>
                <th>View/Delete</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${diseases}" var="disease">
                <tr>
                    <td>${disease.category.id}</td>
                    <td>${disease.category.name}</td>
                    <td>${dulab:abbreviate(disease.category.description, 80)}</td>
                    <td>${disease.count}</td>
                    <!--more horiz-->
                    <td>
                        <a href="${disease.category.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                        <a href="${disease.category.id}/delete/"><i class="material-icons" title="View">delete</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <a href="add/" class="button">Add new disease...</a>
    </div>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#disease_table').DataTable();
    });
</script>

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>