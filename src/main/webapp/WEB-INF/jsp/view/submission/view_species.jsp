<%--@elvariable id="species" type="java.util.List<org.dulab.adapcompounddb.site.controllers.ControllerUtils.CategoryWithSubmissionCount>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<!-- Start the middle column -->

<section>
    <h1>Species</h1>
    <div align="center">
        <table id="species_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>Id</th>
                <th>Specimen</th>
                <th>Description</th>
                <th>Submissions</th>
                <th>View/Delete</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${species}" var="specimen">
                <tr>
                    <td>${specimen.category.id}</td>
                    <td>${specimen.category.name}</td>
                    <td>${dulab:abbreviate(specimen.category.description, 80)}</td>
                    <td>${specimen.count}</td>
                    <!--more horiz-->
                    <td>
                        <a href="${specimen.category.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                        <a href="${specimen.category.id}/delete/"><i class="material-icons" title="View">delete</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <a href="add/" class="button">Add new specimen...</a>
    </div>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#species_table').DataTable();
    });
</script>

<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>