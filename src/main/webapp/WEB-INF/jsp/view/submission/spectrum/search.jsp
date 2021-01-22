<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="row row-content">
    <div class="col col-md-4 px-2">
        <%--@elvariable id="searchResults" type="java.util.List<org.dulab.adapcompounddb.models.dto.SearchResultDTO>"--%>
        <c:if test="${searchResults != null && searchResults.size() > 0}">
            <div class="row mb-1">
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
                    <div class="card-body small">
                        <%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
                        <strong>${querySpectrum.name}</strong>&nbsp;
                        <span class="badge badge-info">${querySpectrum.chromatographyType.label}</span><br/>
                        <strong>${querySpectrum.file.submission.name}</strong>
                        <table>
                            <c:forEach items="${querySpectrum.properties}" var="property">
                                <tr>
                                    <td><strong>${property.name}:</strong>&nbsp;</td>
                                    <td>${property.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
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
                                <td><img src="${pageContext.request.contextPath}/${searchResult.chromatographyTypePath}"
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

                <%--@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"--%>
                <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
                <form:form modelAttribute="filterForm" method="post">
                    <div class="table-dropdown">
                        <form:label path="species">Species:</form:label>
                        <form:select path="species">
                            <form:option value="all">All</form:option>
                            <form:options items="${filterOptions.speciesList}"/>
                        </form:select>
                    </div>
                    <div class="table-dropdown">
                        <form:label path="source">Source:</form:label>
                        <form:select path="source">
                            <form:option value="all">All</form:option>
                            <form:options items="${filterOptions.sourceList}"/>
                        </form:select>
                    </div>
                    <div class="table-dropdown">
                        <form:label path="disease">Disease:</form:label>
                        <form:select path="disease">
                            <form:option value="all">All</form:option>
                            <form:options items="${filterOptions.diseaseList}"/>
                        </form:select>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<%--<section>--%>
<%--    <h1>Query Spectrum</h1>--%>

<%--    <div>--%>
<%--        <table style="margin: auto">--%>
<%--            <tr>--%>
<%--                &lt;%&ndash;@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"&ndash;%&gt;--%>
<%--                <td>--%>
<%--                    ${querySpectrum.name}<br/>--%>
<%--                    <small>${dulab:abbreviate(querySpectrum.properties, 80)}</small>--%>
<%--                </td>--%>
<%--                <td>--%>
<%--                    ${querySpectrum.file.submission.name}<br/>--%>
<%--                    <small>${querySpectrum.chromatographyType.label}</small>--%>
<%--                </td>--%>
<%--            </tr>--%>
<%--        </table>--%>
<%--    </div>--%>
<%--</section>--%>


<%--<section>--%>
<%--    <h1>Matching Hits</h1>--%>
<%--    <div style="alignment: center">--%>

<%--    </div>--%>
<%--</section>--%>

<div align="center">
    <a id="searchButton" class="btn btn-primary" onclick="$('#filterForm').submit()">Search</a>
</div>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script>
    $(document).ready(function () {

        let table = $('#table').DataTable({
            dom: 'l<"#filter">frtip',
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

        let plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(querySpectrum)}'))

        table.on('select', function (e, dt, type, indexes) {
            let row = table.row(indexes).node();
            let spectrum = $(row).attr('data-spectrum');
            if (spectrum) {
                $('#chartSection').show();
                plot.update(JSON.parse(spectrum));
            } else {
                $('#chartSection').hide();
            }
        });

        table.rows(':eq(0)').select();

        $('#filterForm').appendTo('#filter');

        $('#species, #source, #disease').change(function () {
            $('#filterForm').submit();
        });
    });
</script>

<script>

</script>