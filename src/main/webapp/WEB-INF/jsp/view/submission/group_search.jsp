<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container-fluid">
    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-end" role="toolbar">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Export
                    </button>
                    <div class="dropdown-menu">
                        <a class="dropdown-item"
                           href="${pageContext.request.contextPath}/export/session/${dulab:groupSearchResultsAttributeName()}/simple_csv"
                           title="Exports the top match for each query feature">
                            Simple export...
                        </a>
                        <a class="dropdown-item"
                           href="${pageContext.request.contextPath}/export/session/${dulab:groupSearchResultsAttributeName()}/advanced_csv"
                           title="Exports all matches for each query feature">
                            Advanced export...
                        </a>
                    </div>
                </div>
                <div class="progress flex-grow-1 align-self-center mx-2">
                    <div id="progressBar" class="progress-bar" role="progressbar" aria-valuenow="0"
                         aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <a class="btn btn-primary mr-2" href="<c:url value="parameters"/>">Search Parameters</a>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col col-compact" id="queryColumn">
            <div class="card">
                <div class="card-header card-header-single">Query Structure</div>
                <div class="card-body small overflow-auto" style="height: 300px">
                    <div id="queryStructure"></div>
                </div>
            </div>
        </div>
        <div class="col col-compact">
            <div class="card">
                <div class="card-header card-header-single">Query</div>
                <div class="card-body card-body-compact small overflow-auto" style="height: 300px">
                    <div id="queryInfo"></div>
                </div>
            </div>
        </div>
        <div class="col col-compact">
            <div class="card">
                <div class="card-header card-header-single">Plot</div>
                <%--                <div class="card-body small overflow-auto" style="height: 300px">--%>
                <div id="plot" style="height: 300px"></div>
                <%--                </div>--%>
            </div>
        </div>
        <div class="col col-compact">
            <div class="card">
                <div class="card-header card-header-single">Match</div>
                <div class="card-body card-body-compact small overflow-auto" style="height: 300px">
                    <div id="matchInfo"></div>
                </div>
            </div>
        </div>
        <div class="col col-compact" id="matchColumn">
            <div class="card">
                <div class="card-header card-header-single">Match Structure</div>
                <div class="card-body small overflow-auto" style="height: 300px">
                    <div id="matchStructure"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Group Search Results
                </div>
                <div class="card-body small">
                    <table id="match_table" class="display compact" style="width: 100%; clear:none;">
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Query</th>
                            <th title="Match spectra">Match</th>
                            <th title="Molecular weight">Molecular weight</th>
                            <th title="Number of studies" class="Count">Sources</th>
                            <th title="Minimum matching score between all spectra in a cluster">Score</th>
                            <th title="Difference between query and library neutral masses">Mass Error (mDa)</th>
                            <th title="Difference between query and library neutral masses">Mass Error (PPM)</th>
                            <th title="Difference between query and library retention times">Ret Time Error</th>
                            <th title="Difference between query and library retention indices">Ret Index error</th>
                            <th title="Average P-value of ANOVA tests">Average P-value</th>
                            <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
                            <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
                            <th title="Ontology level">Ontology Level</th>
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
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/SpeckTackle/st.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumInfo.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumPlot.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumStructure.js"/>"></script>
<script>
    $(document).ready(function () {

        let table = $('#match_table').DataTable({
            // dom: 'lfrtip',
            serverSide: true,
            order: [[0, 'desc']],
            processing: true,
            responsive: true,
            scrollX: true,
            select: {style: 'single'},
            // scroller: true,
            rowId: 'position',
            ajax: {
                url: "${pageContext.request.contextPath}/file/group_search/data.json",
                data: function (data) {

                    data.columnStr = [];
                    for (let i = 0; i < data.order.length; i++) {
                        data.columnStr += data.order[i].column + "-" + data.order[i].dir + ",";
                    }
                    data.search = data.search["value"];
                },
                dataSrc: function (d) {
                    // Hide columns with no data
                    table.column(3).visible(d.data.map(row => row['mass']).join(''));
                    table.column(4).visible(d.data.map(row => row['size']).join(''));
                    // table.column(5).visible(d.data.map(row => row['score']).join(''));
                    // table.column(6).visible(d.data.map(row => row['massError']).join(''));
                    // table.column(7).visible(d.data.map(row => row['massErrorPPM']).join(''));
                    // table.column(8).visible(d.data.map(row => row['retTimeError']).join(''));
                    // table.column(9).visible(d.data.map(row => row['retIndexError']).join(''));
                    table.column(10).visible(d.data.map(row => row['aveSignificance']).join(''));
                    table.column(11).visible(d.data.map(row => row['minSignificance']).join(''));
                    table.column(12).visible(d.data.map(row => row['maxSignificance']).join(''));
                    return d.data;
                }
            },
            fnCreatedRow: function (row, data, dataIndex) {
                $(row).attr('data-position', data.position);
                $(row).attr('data-matchId', data.spectrumId);
                $(row).attr('data-queryHRef', data.queryHRef);
                $(row).attr('data-queryId', data.querySpectrumId);
                $(row).attr('data-queryFileIndex', data.queryFileIndex);
                $(row).attr('data-querySpectrumIndex', data.querySpectrumIndex);
            },
            columns: [
                {data: 'position'},
                {
                    data: function (row) {
                        const href = (row.querySpectrumId !== 0)
                            ? `<c:url value="/spectrum/\${row.querySpectrumId}/"/>`
                            : `<c:url value="/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/"/>`;
                        return `<a href="\${href}">\${row.querySpectrumName}</a>`;
                    }
                },
                {data: row => (row.name != null) ? `<a href="<c:url value="/\${row.href}" />">\${row.name}</a>` : ''},
                {data: row => (row.mass != null) ? row.mass.toFixed(3) : ''},
                {data: row => (row.size != null) ? row.size : ''},
                {data: row => (row.score != null) ? row.score.toFixed(3) * 1000 : ''},
                {data: row => (row.massError != null) ? (1000 * row.massError).toFixed(3) : ''},
                {data: row => (row.massErrorPPM != null) ? row.massErrorPPM.toFixed(3) : ''},
                {data: row => (row.retTimeError != null) ? row.retTimeError.toFixed(3) : ''},
                {data: row => (row.retIndexError != null) ? row.retIndexError.toFixed(1) : ''},
                {data: row => (row.aveSignificance != null) ? row.aveSignificance.toFixed(3) : ''},
                {data: row => (row.minSignificance != null) ? row.minSignificance.toFixed(3) : ''},
                {data: row => (row.maxSignificance != null) ? row.maxSignificance.toFixed(3) : ''},
                {data: 'ontologyLevel'},
                {
                    data: row => (row.chromatographyTypeLabel != null)
                        ? `<span class="badge badge-secondary">\${row.chromatographyTypeLabel}</span>` : ''
                },
                {
                    data: function (row) {
                        const href = (row.querySpectrumId !== 0)
                            ? `<c:url value="/spectrum/\${row.querySpectrumId}/search/"/>`
                            : `<c:url value="/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/search/"/>`;
                        return `<a href="\${href}"><i class="material-icons" title="Search spectrum">&#xE8B6;</i></a>`;
                    }
                }
            ]
        });

        table.on('select', function (e, dt, type, indexes) {

            let row = table.row(indexes).node();
            let position = $(row).attr('data-position');
            let queryHRef = $(row).attr('data-queryHRef');
            let queryId = $(row).attr('data-queryId');
            let queryFileIndex = $(row).attr('data-queryFileIndex');
            let querySpectrumIndex = $(row).attr('data-querySpectrumIndex');
            let matchId = $(row).attr('data-matchId');

            <%--let queryUrl = `${pageContext.request.contextPath}/file/\${queryFileIndex}/\${querySpectrumIndex}/search/`;--%>
            let queryUrl = `${pageContext.request.contextPath}\${queryHRef}search/`;
            let matchUrl = `${pageContext.request.contextPath}/spectrum/\${matchId}/search/`;

            $('#queryInfo').spectrumInfo(queryUrl + 'info.json');
            $('#matchInfo').spectrumInfo(matchUrl + 'info.json');
            $('#plot').spectrumPlot(position, queryUrl + 'positive/peaks.json', matchUrl + 'negative/peaks.json');
            $('#queryStructure').spectrumStructure(queryUrl + 'structure.json', function (x) {
                $('#queryColumn').attr('hidden', !x);
            });
            $('#matchStructure').spectrumStructure(matchUrl + 'structure.json', function (x) {
                $('#matchColumn').attr('hidden', !x);
            });

        });

        // refresh the datatable and progress bar every 1 second
        setInterval(function () {
            table.ajax.reload(null, false);
            $.getJSON(window.location.origin + window.location.pathname + 'progress', function (x) {
                const width = x + '%';
                const progressBar = $('#progressBar')
                    .css('width', width)
                    .attr('aria-valuenow', x)
                    .html(width);
                if (0 < x && x < 100)
                    progressBar.addClass('progress-bar-striped progress-bar-animated');
                else {
                    progressBar.removeClass('progress-bar-striped progress-bar-animated');
                }
            });
        }, 1000);
<%--        <c:if test="${pageContext.request.method == 'GET'}">$('#filterForm').submit();--%>
<%--        </c:if>--%>
    });
</script>
