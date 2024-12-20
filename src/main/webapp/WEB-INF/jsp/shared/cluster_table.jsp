<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<table id="table" class="display responsive" style="width: 100%;">
    <thead>
    <tr>
        <th>Id</th>
        <th>Query Spectrum</th>
        <th title="Match spectra">Match Spectrum</th>
        <th title="Number of studies" class="Count">Studies</th>
        <th title="Minimum matching score between all spectra in a cluster">Score</th>
        <th title="Average P-value of ANOVA tests">Average P-value</th>
        <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
        <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
        <th title="Spectrum type">Type</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $(document).ready(function () {

        let table = $('#table').DataTable({
            dom: 'l<"#species"><"#source"><"#disease">frtip',
            order: [[4, 'desc']],
            // select: {style: 'single'},
            processing: true,  // Show indicator when loading ajax
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

                    let speciesFilter = $('#species_filter');
                    d.species = speciesFilter.length ? speciesFilter.val() : 'all';

                    let sourceFilter = $('#source_filter');
                    d.source = sourceFilter.length ? sourceFilter.val() : 'all';

                    let diseaseFilter = $('#disease_filter');
                    d.disease = diseaseFilter.length ? diseaseFilter.val() : 'all';
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
                    "bVisible": ${param.hide_query_spectrum ? false : true},
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
                        if (row.aveSignificance != null)
                            return row.aveSignificance.toFixed(3);
                        return '';
                    }
                },
                {
                    "targets": 6,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.minSignificance != null)
                            return row.minSignificance.toFixed(3);
                        return '';
                    }
                },
                {
                    "targets": 7,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.maxSignificance != null)
                            return row.maxSignificance.toFixed(3);
                        return '';
                    }
                },
                {
                    "targets": 8,
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
                    "targets": 9,
                    "bSortable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '<a href="${pageContext.request.contextPath}/cluster/'
                            + row.clusterId + '/"><i class="material-icons" title="View">&#xE5D3;</i></a>';
                        return content;
                    }
                }
            ]
        });

        $('#species').addClass('table-dropdown');
        // $('#species').css('padding-left', '30px');
        $('<label/>').text('Species: ').appendTo('#species');
        let speciesSelect = $('<select id="species_filter"/>').appendTo('#species');
        $('<option/>').val('all').text('All').appendTo(speciesSelect);
        <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
        <c:forEach items="${filterOptions.speciesList}" var="it">$('<option/>').val('${it}').text('${it}').appendTo(speciesSelect);
        </c:forEach>

        $('#source').addClass('table-dropdown');
        $('<label/>').text('Sample source: ').appendTo('#source');
        let sourceSelect = $('<select id="source_filter"/>').appendTo('#source');
        $('<option/>').val('all').text('All').appendTo(sourceSelect);
        <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
        <c:forEach items="${filterOptions.sourceList}" var="it">$('<option/>').val('${it}').text('${it}').appendTo(sourceSelect);
        </c:forEach>

        $('#disease').addClass('table-dropdown');
        $('<label/>').text('Disease: ').appendTo('#disease');
        let diseaseSelect = $('<select id="disease_filter"/>').appendTo('#disease');
        $('<option/>').val('all').text('All').appendTo(diseaseSelect);
        <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
        <c:forEach items="${filterOptions.diseaseList}" var="it">$('<option/>').val('${it}').text('${it}').appendTo(diseaseSelect);
        </c:forEach>

        $('#species_filter, #source_filter, #disease_filter').change(function () {
            table.ajax.reload();
        });

    })
</script>