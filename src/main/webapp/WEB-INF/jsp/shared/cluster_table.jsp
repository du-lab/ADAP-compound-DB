<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<div style="display: flex; justify-content: space-evenly">
    <div style="display: inline-block; vertical-align: middle" class="frame">
        <jsp:include page="/WEB-INF/jsp/shared/filter.jsp">
            <jsp:param name="table_id" value="#table"/>
            <jsp:param name="filterOptions" value="filterOptions"/>
        </jsp:include>
    </div>

    <div style="display: inline-block; width: 500px" class="frame">
        <jsp:include page="/WEB-INF/jsp/shared/column_visibility.jsp">
            <jsp:param name="table_id" value="#table"/>
        </jsp:include>
    </div>
</div>

<table id="table" class="display responsive" style="width: 100%;">
    <thead>
    <tr>
        <th>Id</th>
        <th>Query Spectrum</th>
        <th title="Match spectra">Match Spectrum</th>
        <th title="Number of studies" class="Count">Count</th>
        <th title="Minimum matching score between all spectra in a cluster">Score</th>
        <th title="P-value of the In-study ANOVA test">In-study P-value</th>
        <th title="Chromatography type">Type</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $(document).ready(function () {

        let table = $('#table').DataTable({
            order: [[4, 'desc']],
            // select: {style: 'single'},
            processing: true,  // Show indicator when loading ajax
            'language': {
                'loadingRecords': '&nbsp;',
                'processing': '<div class="spinner"></div>'
            },
            responsive: true,
            scrollX: true,
            scroller: true,
            serverSide: true,
            ajax: {
                url: "${pageContext.request.contextPath}/rest/individual_search/json",
                type: "POST",
                data: function (d) {
                    d.column = d.order[0].column;
                    d.sortDirection = d.order[0].dir;
                    d.search = d.search["value"];
                    <%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
                    d.queryJson = "${dulab:peaksToJsonString(querySpectrum.peaks)}";
                    d.scoreThreshold = $('${param.score_threshold}').val();
                    d.mzTolerance = $('${param.mz_tolerance}').val();
                    d.species = $('#species_filter').val();
                    d.source = $('#source_filter').val();
                    d.disease = $('#disease_filter').val();
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
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.querySpectrumId == null || row.querySpectrumName == null)
                            return '';
                        return '<a href="${pageContext.request.contextPath}/spectrum/' + row.querySpectrumId + '/">' +
                            row.querySpectrumName + '</a>';
                    }
                },
                {
                    "targets": 2,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        content = '<a href="${pageContext.request.contextPath}/cluster/' + row.clusterId + '/">' +
                            row.consensusSpectrumName +
                            '</a>';
                        return content
                    }
                },
                {
                    "targets": 3,
                    "bSortable": true,
                    "bVisible": true,
                    "data": "size"
                },
                {
                    "targets": 4,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return row.score.toFixed(3) * 1000;
                    }
                },
                {
                    "targets": 5,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.aveSignificance != null) {
                            var avgSignificance = row.aveSignificance.toFixed(3);
                            content += '<span title="{Average: ' + row.aveSignificance;
                            if (row.minSignificance) {
                                content += '; Min: ' + row.minSignificance.toFixed(3);
                            }
                            if (row.maxSignificance) {
                                content += '; Max: ' + row.maxSignificance.toFixed(3);
                            }
                            content += '}">' + avgSignificance + '</span>';
                        }
                        return content;
                    }
                },
                {
                    "targets": 6,
                    "bSortable": true,
                    "bVisible": true,
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
                    "targets": 7,
                    "bSortable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '<a href="${pageContext.request.contextPath}/cluster/'
                            + row.id + '/"><i class="material-icons" title="View">&#xE5D3;</i></a>';
                        return content;
                    }
                }
            ]
        });

        $("#species_filter, #source_filter, #disease_filter").change(function () {
            table.ajax.reload();
        });

        columnVisibilityInit();

    })
</script>