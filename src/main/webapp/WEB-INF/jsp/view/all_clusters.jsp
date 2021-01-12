<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<section>
    <h1>Consensus spectra</h1>

    <div style="text-align: center">
        <table id="cluster_table" class="display responsive" style="width: 100%;">
            <thead>
            <tr>
                <th>ID</th>
                <th title="Cluster ID in database">Cluster ID</th>
                <th title="Consensus spectrum">Spectrum</th>
                <th title="Number of studies">Studies</th>
                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                <th title="P-value of the In-study ANOVA test">Average P-value</th>
                <th title="Gini-Simpson Index">Minimum P-value</th>
                <th title="P-value of the Cross-study Goodness-of-fit test">Maximum P-value</th>
                <th title="Chromatography type">Type</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>

        <%--@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"--%>
        <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
        <div id="filterForm">
            <div class="table-dropdown">
                <label for="species">Species:</label>
                <select id="species">
                    <option value="all">All</option>
                    <c:forEach items="${filterOptions.speciesList}" var="it">
                        <option value="${it}">${it}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="table-dropdown">
                <label for="source">Source:</label>
                <select id="source">
                    <option value="all">All</option>
                    <c:forEach items="${filterOptions.sourceList}" var="it">
                        <option value="${it}">${it}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="table-dropdown">
                <label for="disease">Disease:</label>
                <select id="disease">
                    <option value="all">All</option>
                    <c:forEach items="${filterOptions.diseaseList}" var="it">
                        <option value="${it}">${it}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
    </div>
</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $(document).ready(function () {
        let dataTable = $('#cluster_table').DataTable({
            dom: 'l<"#filter">frtip',
            serverSide: true,
            processing: true,
            responsive: true,
            scrollX: true,
            scroller: true,
            sortable: true,
            visible: true,
            ajax: {
                url: "${pageContext.request.contextPath}/spectrum/findClusters.json",

                data: function (d) {
                    d.column = d.order[0].column;
                    d.sortDirection = d.order[0].dir;
                    d.search = d.search["value"];
                    d.species = $('#species').val();
                    d.source = $('#source').val();
                    d.disease = $('#disease').val();
                }
            },
            "aoColumnDefs": [
                {
                    "targets": 0,
                    "bSortable": false,
                    "searchable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return meta.settings.oAjaxData.start + meta.row + 1;
                    }
                },
                {
                    "targets": 1,
                    "data": "id"
                },
                {
                    "targets": 2,
                    "render": function (data, type, row, meta) {
                        return `<a href="${pageContext.request.contextPath}/\${row.matchType.toLowerCase()}/\${row.id}/">\${row.name}</a>`;
                    }
                },
                {
                    "targets": 3,
                    "data": "size"
                },
                {
                    "targets": 4,
                    "render": function (data, type, row, meta) {
                        return (row.score != null) ? row.score.toFixed(3) * 1000 : '';
                    }
                },
                {
                    "targets": 5,
                    "render": function (data, type, row, meta) {
                        return (row.aveSignificance != null) ? row.aveSignificance.toFixed(3) : '';
                    }
                },
                {
                    "targets": 6,
                    "render": function (data, type, row, meta) {
                        return (row.minSignificance != null) ? row.minSignificance.toFixed(3) : '';
                    }
                },
                {
                    "targets": 7,
                    "render": function (data, type, row, meta) {
                        return (row.maxSignificance != null) ? row.maxSignificance.toFixed(3) : '';
                    }
                },
                {
                    "targets": 8,
                    "render": function (data, type, row, meta) {
                        var content = '<img' +
                            ' src="${pageContext.request.contextPath}/' + row.chromatographyTypePath + '"'
                            + ' alt="' + row.chromatographyTypeLabel + '"'
                            + ' title="' + row.chromatographyTypeLabel + '"'
                            + ' class="icon"/>';

                        return content;
                    }
                },
                {
                    "targets": 9,
                    "bSortable": false,
                    "render": function (data, type, row, meta) {
                        return `<a href="${pageContext.request.contextPath}/\${row.matchType.toLowerCase()}/\${row.id}/">
                                <i class="material-icons" title="view">&#xE5D3;</i></a>`;
                    }
                },
                {"className": "dt-center", "targets": "_all"}
            ]
        });

        let filterForm = $('#filterForm');
        filterForm.appendTo('#filter');

        $('#species, #source, #disease').change(function () {
            dataTable.ajax.reload(null, false);
        });
    });


</script>