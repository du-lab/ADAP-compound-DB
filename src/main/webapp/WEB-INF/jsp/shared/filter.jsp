<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
<table style="padding: 10px">
    <tbody>
    <tr>
        <td><label for="species_filter">Species</label></td>
        <td>
            <select id="species_filter">
                <option value="all">All</option>
                <c:forEach items="${filterOptions.speciesList}" var="it">
                    <option value="${it}">${it}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <td>
            <label for="source_filter">Sample Source:</label>
        </td>
        <td>
            <select id="source_filter">
                <option value="all">All</option>
                <c:forEach items="${filterOptions.sourceList}" var="it">
                    <option value="${it}">${it}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <td>
            <label for="disease_filter">Sample Source:</label>
        </td>
        <td>
            <select id="disease_filter">
                <option value="all">All</option>
                <c:forEach items="${filterOptions.diseaseList}" var="it">
                    <option value="${it}">${it}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
    </tbody>
</table>

<script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    <%--$(document).ready(function() {--%>
    <%--    $("#species_filter, #source_filter, #disease_filter").change(function () {--%>
    <%--        $('${param.table_id}').dataTable().ajax.reload();--%>
    <%--    });--%>
    <%--})--%>
</script>