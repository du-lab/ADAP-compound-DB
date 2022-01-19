<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="filterModal" class="modal fade" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Filter</h5>
            </div>
            <div class="modal-body">
                <div class="container-fluid">
                    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="row form-row form-group">
                                <label for="chromatographyType" class="col-md-6 col-form-label">Chromatography
                                    Type:</label>
                                <select id="chromatographyType" class="col-md-6 form-control">
                                    <option value="all">All</option>
                                    <c:forEach items="${filterOptions.chromatographyTypes}" var="chromatographyType">
                                        <option value="${chromatographyType}">${chromatographyType.label}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="row form-row form-group">
                                <label for="search" class="col-md-6 col-form-label">Name Search:</label>
                                <input id="search" type="search" class="col-md-6 form-control"/>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="row form-row form-group">
                                <label for="species" class="col-md-6 col-form-label">Species:</label>
                                <select id="species" class="col-md-6 form-control">
                                    <option value="all">All</option>
                                    <c:forEach items="${filterOptions.speciesList}" var="species">
                                        <option value="${species}">${species}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="row form-row form-group">
                                <label for="source" class="col-md-6 col-form-label">Source:</label>
                                <select id="source" class="col-md-6 form-control">
                                    <option value="all">All</option>
                                    <c:forEach items="${filterOptions.sourceList}" var="source">
                                        <option value="${source}">${source}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="row form-row form-group">
                                <label for="disease" class="col-md-6 col-form-label">Disease:</label>
                                <select id="disease" class="col-md-6 form-control">
                                    <option value="all">All</option>
                                    <c:forEach items="${filterOptions.diseaseList}" var="disease">
                                        <option value="${disease}">${disease}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button id="filterModalOkButton" type="button" class="btn btn-primary" data-dismiss="modal">Filter</button>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-end" role="toolbar">
                <button id="filterButton" type="button" class="btn btn-primary mr-2" data-toggle="modal"
                        data-target="#filterModal">Filter...
                </button>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Consensus Spectra
                </div>
                <div class="card-body small">
                    <table id="cluster_table" class="display compact" style="width: 100%;">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th title="Cluster ID in database">Cluster ID</th>
                            <th title="Consensus spectrum">Spectrum</th>
                            <th title="Number of studies">Studies</th>
                            <th title="Minimum matching score between all spectra in a cluster">Score</th>
                            <th title="P-value of the In-study ANOVA test">ANOVA P-value (average)</th>
                            <th title="Gini-Simpson Index">ANOVA P-value (minimum)</th>
                            <th title="P-value of the Goodness-of-Fit test for the distribution of disease">GOF P-value (disease)</th>
                            <th title="P-value of the Goodness-of-Fit test for the distribution of species">GOF P-value (species)</th>
                            <th title="P-value of the Goodness-of-Fit test for the distribution of sample source">GOF P-value (sample source)</th>
                            <th title="The Minimum P-value of the Goodness-of-Fit test">GOF P-value (minimum)</th>
                            <th title="Chromatography type">Type</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <%--    <div style="text-align: center">--%>


    <%--        &lt;%&ndash;@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"&ndash;%&gt;--%>
    <%--        &lt;%&ndash;@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"&ndash;%&gt;--%>
    <%--        <div id="filterForm">--%>
    <%--            <div class="table-dropdown">--%>
    <%--                <label for="species">Species:</label>--%>
    <%--                <select id="species">--%>
    <%--                    <option value="all">All</option>--%>
    <%--                    <c:forEach items="${filterOptions.speciesList}" var="it">--%>
    <%--                        <option value="${it}">${it}</option>--%>
    <%--                    </c:forEach>--%>
    <%--                </select>--%>
    <%--            </div>--%>
    <%--            <div class="table-dropdown">--%>
    <%--                <label for="source">Source:</label>--%>
    <%--                <select id="source">--%>
    <%--                    <option value="all">All</option>--%>
    <%--                    <c:forEach items="${filterOptions.sourceList}" var="it">--%>
    <%--                        <option value="${it}">${it}</option>--%>
    <%--                    </c:forEach>--%>
    <%--                </select>--%>
    <%--            </div>--%>
    <%--            <div class="table-dropdown">--%>
    <%--                <label for="disease">Disease:</label>--%>
    <%--                <select id="disease">--%>
    <%--                    <option value="all">All</option>--%>
    <%--                    <c:forEach items="${filterOptions.diseaseList}" var="it">--%>
    <%--                        <option value="${it}">${it}</option>--%>
    <%--                    </c:forEach>--%>
    <%--                </select>--%>
    <%--            </div>--%>
    <%--        </div>--%>
    <%--    </div>--%>
</div>

<%--<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>--%>
<%--<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>--%>
<%--<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>--%>
<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(document).ready(function () {
        let dataTable = $('#cluster_table').DataTable({
            dom: 'lrtip',  // Removes search text box
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
                    // d.search = d.search["value"];
                    d.chromatographyType = $('#chromatographyType').val();
                    d.search = $('#search').val();
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
                    "data": "clusterId"
                },
                {
                    "targets": 2,
                    "render": function (data, type, row, meta) {
                        if (row.matchType === 'CLUSTER')
                            return `<a href="${pageContext.request.contextPath}/cluster/\${row.clusterId}/">\${row.name}</a>`;
                        if (row.matchType === 'SPECTRUM')
                            return `<a href="${pageContext.request.contextPath}/spectrum/\${row.spectrumId}/">\${row.name}</a>`;
                        return '';
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
                        return (row.diseasePValue != null) ? row.diseasePValue.toFixed(3) : '';
                    }
                },
                {
                    "targets": 8,
                    "render": function (data, type, row, meta) {
                        return (row.speciesPValue != null) ? row.speciesPValue.toFixed(3) : '';
                    }
                },
                {
                    "targets": 9,
                    "render": function (data, type, row, meta) {
                        return (row.sampleSourcePValue != null) ? row.sampleSourcePValue.toFixed(3) : '';
                    }
                },
                {
                    "targets": 10,
                    "render": function (data, type, row, meta) {
                        return (row.minPValue != null) ? row.minPValue.toFixed(3) : '';
                    }
                },
                {
                    "targets": 11,
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
                    "targets": 12,
                    "bSortable": false,
                    "render": function (data, type, row, meta) {
                        if (row.matchType === 'CLUSTER')
                            return `<a href="${pageContext.request.contextPath}/cluster/\${row.clusterId}/">
                                <i class="material-icons" title="view">&#xE5D3;</i></a>`;
                        if (row.matchType === 'SPECTRUM')
                            return `<a href="${pageContext.request.contextPath}/spectrum/\${row.spectrumId}/">
                                <i class="material-icons" title="view">&#xE5D3;</i></a>`;
                        return '';
                    }
                },
                {"className": "dt-center", "targets": "_all"}
            ]
        });

        // let filterForm = $('#filterForm');
        // filterForm.appendTo('#filter');m

        // $('#species, #source, #disease').change(function () {
        //     dataTable.ajax.reload(null, false);
        // });

        $('#filterModalOkButton').click(function () {
            dataTable.ajax.reload(null, false);
        })
    });


</script>