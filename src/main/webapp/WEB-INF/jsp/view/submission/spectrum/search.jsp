<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="font" uri="http://www.springframework.org/tags/form" %>

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
                                    <form:select path="submissionIds" cssClass="custom-select" multiple="multiple">
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
        <div class="col col-md-4 px-2">
            <%--@elvariable id="searchResults" type="java.util.List<org.dulab.adapcompounddb.models.dto.SearchResultDTO>"--%>
            <c:if test="${searchResults != null && searchResults.size() > 0}">
                <div id="chartRow" class="row mb-1">
                    <div class="col-12 px-0">
                        <div class="card">
                            <div class="card-header card-header-single">Plot</div>
                            <div class="card-body p-1">
                                <div id="chartSection">
                                    <div id="plot" style="max-width: 100%; height: auto"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            <div class="row">
                <div class="col col-12 px-0">
                    <div class="card">
                        <div class="card-header card-header-single">Query Spectrum</div>
                        <div class="card-body small overflow-auto" style="max-height: 400px">
                            <%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
                            <strong>${querySpectrum.name}</strong>&nbsp;
                            <span class="badge badge-info">${querySpectrum.chromatographyType.label}</span><br/>
                            ${querySpectrum.file.submission.name}
                            <ul class="list-group list-group-flush">
                                <c:forEach items="${querySpectrum.properties}" var="property">
                                    <li class="list-group-item py-1">
                                        <strong>${property.name}:</strong>&nbsp
                                        <span style="word-break: break-all">${property.value}</span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col col-md-8 px-2">
            <div class="card">
                <div class="card-header card-header-single">Matching Hits</div>
                <div class="card-body small">
                    <table id="table" class="display compact" style="width: 100%;">
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Query Spectrum</th>
                            <th title="Match spectra">Match Spectrum</th>
                            <th title="Molecular weight">Molecular weight</th>
                            <th title="Number of studies" class="Count">Studies</th>
                            <th title="Minimum matching score between all spectra in a cluster">Score</th>
                            <th title="Error">Error</th>
                            <th title="Average P-value of ANOVA tests">Average P-value</th>
                            <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
                            <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
                            <th title="Chromatography type">Type</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%--@elvariable id="searchResults" type="java.util.List<org.dulab.adapcompounddb.models.dto.SearchResultDTO>"--%>
                        <c:if test="${searchResults != null}">
                            <c:forEach items="${searchResults}" var="searchResult" varStatus="status">
                                <tr data-spectrum='${searchResult.json}'>
                                    <td>${status.index + 1}</td>
                                    <td>${searchResult.querySpectrumName}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/${fn:toLowerCase(searchResult.matchType)}/${searchResult.id}/">
                                                ${searchResult.name}
                                        </a>
                                    </td>
                                    <td>${searchResult.molecularWeight}</td>
                                    <td>${searchResult.size}</td>
                                    <td>${searchResult.getNISTScore()}</td>
                                    <td>${dulab:formatDouble(searchResult.error)}</td>
                                    <td>${dulab:formatDouble(searchResult.aveSignificance)}</td>
                                    <td>${dulab:formatDouble(searchResult.minSignificance)}</td>
                                    <td>${dulab:formatDouble(searchResult.maxSignificance)}</td>
                                    <td><img
                                            src="${pageContext.request.contextPath}/${searchResult.chromatographyTypePath}"
                                            alt="${searchResult.chromatographyTypeLabel}"
                                            title="${searchResult.chromatographyTypeLabel}"/></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/${fn:toLowerCase(searchResult.matchType)}/${searchResult.id}/">
                                            <i class="material-icons" title="View">&#xE5D3;</i>
                                        </a></td>
                                </tr>
                            </c:forEach>
                        </c:if>
                        </tbody>
                    </table>

                    <%--                    &lt;%&ndash;@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"&ndash;%&gt;--%>
                    <%--                    &lt;%&ndash;@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"&ndash;%&gt;--%>
                    <%--                    <form:form modelAttribute="filterForm" method="post">--%>
                    <%--                        <div class="table-dropdown">--%>
                    <%--                            <form:label path="species">Species:</form:label>--%>
                    <%--                            <form:select path="species">--%>
                    <%--                                <form:option value="all">All</form:option>--%>
                    <%--                                <form:options items="${filterOptions.speciesList}"/>--%>
                    <%--                            </form:select>--%>
                    <%--                        </div>--%>
                    <%--                        <div class="table-dropdown">--%>
                    <%--                            <form:label path="source">Source:</form:label>--%>
                    <%--                            <form:select path="source">--%>
                    <%--                                <form:option value="all">All</form:option>--%>
                    <%--                                <form:options items="${filterOptions.sourceList}"/>--%>
                    <%--                            </form:select>--%>
                    <%--                        </div>--%>
                    <%--                        <div class="table-dropdown">--%>
                    <%--                            <form:label path="disease">Disease:</form:label>--%>
                    <%--                            <form:select path="disease">--%>
                    <%--                                <form:option value="all">All</form:option>--%>
                    <%--                                <form:options items="${filterOptions.diseaseList}"/>--%>
                    <%--                            </form:select>--%>
                    <%--                        </div>--%>
                    <%--                    </form:form>--%>
                </div>
            </div>
        </div>
    </div>

    <%--    <div align="center">--%>
    <%--        <a id="searchButton" class="btn btn-primary" onclick="$('#filterForm').submit()">Search</a>--%>
    <%--    </div>--%>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
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

        let plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(querySpectrum)}'));

        table.on('select', function (e, dt, type, indexes) {

            let chartRow = $('#chartRow');
            chartRow.hide();

            let row = table.row(indexes).node();
            let spectrum = $(row).attr('data-spectrum');
            if (spectrum == null) return;

            let spectrumJson = JSON.parse(spectrum);
            if (spectrumJson["peaks"].length === 0) return;

            plot.update(spectrumJson);
            chartRow.show();
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