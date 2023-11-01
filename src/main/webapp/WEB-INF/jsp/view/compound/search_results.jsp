<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="font" uri="http://www.springframework.org/tags/form" %>
<head>
    <script src="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"></script>
    <script src="/resources/AdapCompoundDb/js/filterSearchResults.js"></script>
</head>
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
                                                 size="10" id="submission-select">
                                        <c:forEach items="${filterOptions.submissions}" var="entry">
                                            <form:option value="${entry.key}"
                                                         selected="${filterForm.submissionIds.contains(entry.key) ? 'selected' : ''}">
                                                ${entry.value}
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </div>
                            <div class="col-md-6" id="species-container">
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
                            <th title="Library">Submission</th>
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
                                        <a href="${pageContext.request.contextPath}/${searchResult.getHRef()}">
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
                                    <td>
                                        <a href="${pageContext.request.contextPath}/submission/${searchResult.submissionId}/">${searchResult.submissionName}</a>
                                        <span class="badge badge-info">${searchResult.chromatographyTypeLabel}</span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/${searchResult.getHRef()}">
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



        let table = $('#table').DataTable({
            // dom: 'lfrtip',
            // order: [[5, 'desc']],
            processing: true,  // Show indicator when loading ajax
            responsive: true,
            scrollX: true,
            scroller: true,
            select: {style: 'single'},
            "aoColumnDefs": [
                {
                    "targets": 1,
                    visible: false,
                }
            ]
            // // Hide columns with no data
            // "fnDrawCallback": function() {
            //     const api = this.api();
            //     api.columns().flatten().each(function(colIndex) {
            //         const column = api.column(colIndex);
            //         const columnData = column.data().join('');
            //         column.visible(columnData);
            //     })
            // }
        });

        <%--let plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(querySpectrum)}'));--%>

        table.on('select', function (e, dt, type, indexes) {

            // let chartRow = $('#chartRow');
            // chartRow.hide();

            let row = table.row(indexes).node();
            let spectrumId = $(row).attr('data-id');
            console.log(spectrumId);
            $.ajax({
                url: `${pageContext.request.contextPath}/ajax/spectrum/info?spectrumId=\${spectrumId}`,
                success: d => $('#queryInfo').html(d)
            })

            $('#matchInfo').spectrumInfo(`${pageContext.request.contextPath}/spectrum/\${spectrumId}/search/info.json`);
            console.log(`${pageContext.request.contextPath}/spectrum/\${spectrumId}/search/negative/peaks.json`);
            $('#plot').spectrumPlot(indexes,
                `${pageContext.request.contextPath}/spectrum/\${spectrumId}/search/positive/peaks.json`,
                `${pageContext.request.contextPath}/spectrum/\${spectrumId}/search/negative/peaks.json`);

        });

        table.rows(':eq(0)').select();

        $('#searchButton').click(function () {
            $('#filterForm').submit();
            $(this).prop('disabled', true);
            $(this).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>&nbsp;Search')
        });

        // $('span[data-id="property_table"]').spa(function () {
        //     console.log(this.style.overflow);
        // });

        // $('#filterForm').appendTo('#filter');

        // $('#species, #source, #disease').change(function () {
        //     $('#filterForm').submit();
        // });
    })
</script>