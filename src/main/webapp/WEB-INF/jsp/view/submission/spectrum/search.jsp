<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="font" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>

<div id="filterModal" class="modal fade" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Filter</h5>
                <%--                <button type="button" class="close" data-dismiss="modal" aria-label="Close">--%>
                <%--                    <span aria-hidden="true">&times;</span>--%>
                <%--                </button>--%>
            </div>
            <div class="modal-body">
                <div class="container-fluid">

                    <%--@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"--%>
                    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
                    <form:form modelAttribute="filterForm" method="post">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="row form-group">
                                    <form:label path="submissionIds" cssClass="col-form-label">Libraries:</form:label>
                                    <form:select path="submissionIds" cssClass="custom-select" multiple="multiple"
                                                 size="10">
                                        <c:forEach items="${filterOptions.submissions}" var="entry">
                                            <form:option value="${entry.key}"
                                                         selected="${filterForm.submissionIds.contains(entry.key) ? 'selected' : ''}">
                                                ${entry.value}
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="row form-group">
                                    <form:label path="species" cssClass="col-md-6 col-form-label">Species:</form:label>
                                    <form:select path="species" cssClass="col-md-6 form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.speciesList}"/>
                                    </form:select>
                                </div>
                                <div class="row form-group">
                                    <form:label path="source" cssClass="col-md-6 col-form-label">Source:</form:label>
                                    <form:select path="source" cssClass="col-md-6 form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.sourceList}"/>
                                    </form:select>
                                </div>
                                <div class="row form-group">
                                    <form:label path="disease" cssClass="col-md-6 col-form-label">Disease:</form:label>
                                    <form:select path="disease" cssClass="col-md-6 form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.diseaseList}"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-end" role="toolbar">
                <button id="filterButton" type="button" class="btn btn-primary mr-2" data-toggle="modal"
                        data-target="#filterModal">Filter...
                </button>
                <button id="searchButton" type="button" class="btn btn-primary">Search</button>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Query</div>
                <div class="card-body small overflow-auto" style="height: 300px">
                    <div id="queryInfo"></div>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Plot</div>
                <div id="plot" style="height: 300px"></div>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Match</div>
                <div class="card-body small overflow-auto" style="height: 300px">
                    <div id="matchInfo"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Matching Hits</div>
                <div class="card-body small">
                    <table id="table" class="display compact" style="width: 100%;">
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Query</th>
                            <th title="Match spectra">Match</th>
                            <th title="Molecular weight">Molecular weight</th>
                            <th title="Number of studies" class="Count">Studies</th>
                            <th title="Minimum matching score between all spectra in a cluster">Score</th>
                            <th title="Mass Error">Mass Error (mDa)</th>
                            <th title="Mass Error">Mass Error (PPM)</th>
                            <th title="Retention Time Error"> Ret Time Error</th>
                            <th title="Average P-value of ANOVA tests">Average P-value</th>
                            <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
                            <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
                            <th title="Ontology Level">Ontology Level</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%--@elvariable id="searchResults" type="java.util.List<org.dulab.adapcompounddb.models.dto.SearchResultDTO>"--%>
                        <c:if test="${searchResults != null}">
                            <c:forEach items="${searchResults}" var="searchResult" varStatus="status">
                                <tr data-id="${searchResult.spectrumId}">
                                    <td>${status.index + 1}</td>
                                    <td>${searchResult.querySpectrumName}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}${searchResult.getHRef()}">
                                                ${searchResult.name}
                                        </a>
                                    </td>
                                    <td>${searchResult.mass}</td>
                                    <td>${searchResult.size}</td>
                                    <td>${searchResult.getNISTScore()}</td>
                                    <td>${(searchResult.massError != null) ? dulab:formatDouble(1000 * searchResult.massError) : ''}</td>
                                    <td>${dulab:formatDouble(searchResult.massErrorPPM)}</td>
                                    <td>${dulab:formatDouble(searchResult.retTimeError)}</td>
                                    <td>${dulab:formatDouble(searchResult.aveSignificance)}</td>
                                    <td>${dulab:formatDouble(searchResult.minSignificance)}</td>
                                    <td>${dulab:formatDouble(searchResult.maxSignificance)}</td>
                                    <td><span class="badge badge-info">${searchResult.ontologyLevel}</span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}${searchResult.getHRef()}">
                                            <i class="material-icons" title="View">&#xE5D3;</i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:if>
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
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumInfo.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumPlot.js"/>"></script>
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
                url: "${pageContext.request.contextPath}/file/individual_search/data.json",
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
                    table.column(11).visible(d.data.map(row => row['aveSignificance']).join(''));
                    table.column(12).visible(d.data.map(row => row['minSignificance']).join(''));
                    table.column(13).visible(d.data.map(row => row['maxSignificance']).join(''));
                    return d.data;
                },
                error: function (xhr, error, code) {
                    logging.logToServer('<c:url value="/js-log"/>', `\${xhr.status} - \${error} - \${code}`);
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
                {data: row => (row.isotopicSimilarity != null) ? row.isotopicSimilarity.toFixed(3) * 1000 : ''},
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

            $.ajax({
                url: `${pageContext.request.contextPath}/ajax/spectrum/info?spectrumId=\${queryId}&fileIndex=\${queryFileIndex}&spectrumIndex=\${querySpectrumIndex}`,
                success: d => $('#queryInfo').html(d)
            })

            $.ajax({
                url: `${pageContext.request.contextPath}/ajax/spectrum/info?spectrumId=\${matchId}`,
                success: d => $('#matchInfo').html(d)
            })

            // $('#queryInfo').spectrumInfo(queryUrl + 'info.json');
            // $('#matchInfo').spectrumInfo(matchUrl + 'info.json');
            $('#plot').spectrumPlot(position, queryUrl + 'positive/peaks.json', matchUrl + 'negative/peaks.json');
            $('#queryStructure').spectrumStructure(queryUrl + 'structure.json', function (x) {
                $('#queryColumn').attr('hidden', !x);
            });
            $('#matchStructure').spectrumStructure(matchUrl + 'structure.json', function (x) {
                $('#matchColumn').attr('hidden', !x);
            });

        });



        <%--        <c:if test="${pageContext.request.method == 'GET'}">$('#filterForm').submit();--%>
        <%--        </c:if>--%>
    });
</script>