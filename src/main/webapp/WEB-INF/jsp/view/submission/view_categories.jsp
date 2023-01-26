<%--@elvariable id="categories" type="java.util.List<org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.CategoryWithSubmissionCount>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<section>
    <h1>Categories</h1>
    <div align="center">
        <table id="categories_table" class="display" style="width: 100%; clear: none;">
            <thead>
            <tr>
                <th>Id</th>
                <th>Source</th>
                <th>Description</th>
                <th>Submissions</th>
                <th>View/Delete</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${categories}" var="category">
                <tr>
                    <td>${category.category.id}</td>
                    <td>${category.category.name}</td>
                    <td>${dulab:abbreviate(category.category.description, 80)}</td>
                    <td>${category.count}</td>
                    <!--more horiz-->
                    <td>
                        <a href="${category.category.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                        <a href="${category.category.id}/delete/"><i class="material-icons" title="Delete">delete</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <a href="add/" class="button">Add new source...</a>
    </div>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#categories_table').DataTable();
    });
</script>