<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<section>
    <h1>Query Spectrum</h1>

    <div align="center">
        <table>
            <tr>
                <%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
                <td>
                    ${querySpectrum.name}<br/>
                    <small>${dulab:abbreviate(querySpectrum.properties, 80)}</small>
                </td>
                <td>
                    ${querySpectrum.file.submission.name}<br/>
                    <small>${querySpectrum.chromatographyType.label}</small>
                </td>
            </tr>
        </table>
    </div>
</section>

<%--@elvariable id="clusters" type="java.util.List<org.dulab.adapcompounddb.models.dto.ClusterDTO>"--%>
<c:if test="${clusters != null && clusters.size() > 0}">
    <section id="chartSection">
        <h1>Comparison</h1>
        <div id="plot" align="center" class="plot"></div>
    </section>
</c:if>

<section>
    <h1>Matching Hits</h1>
    <div align="center">
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
                <th title="Chromatography type">Type</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <%--@elvariable id="clusters" type="java.util.List<org.dulab.adapcompounddb.models.dto.ClusterDTO>"--%>
            <c:if test="${clusters != null}">
                <c:forEach items="${clusters}" var="cluster" varStatus="status">
                    <tr data-spectrum='${cluster.json}'>
                        <td>${status.index + 1}</td>
                        <td>${cluster.querySpectrumName}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/cluster/${cluster.clusterId}/">${cluster.consensusSpectrumName}</a>
                        </td>
                        <td>${cluster.size}</td>
                        <td>${cluster.getNISTScore()}</td>
                        <td>${dulab:formatDouble(cluster.aveSignificance)}</td>
                        <td>${dulab:formatDouble(cluster.minSignificance)}</td>
                        <td>${dulab:formatDouble(cluster.maxSignificance)}</td>
                        <td><img src="${pageContext.request.contextPath}/${cluster.chromatographyTypePath}"
                                 alt="${cluster.chromatographyTypeLabel}" title="${cluster.chromatographyTypeLabel}"/></td>
                        <td><a href="${pageContext.request.contextPath}/cluster/${cluster.clusterId}/">
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
</section>

<div align="center">
    <a class="button" onclick="$('#filterForm').submit()">Search</a>
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
            order: [[4, 'desc']],
            select: {style: 'single'},
            processing: true,  // Show indicator when loading ajax
            responsive: true,
            scrollX: true,
            scroller: true,
            "aoColumnDefs": [
                {
                    "targets": 1,
                    visible: false,
                }
            ]
        });

        let plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(querySpectrum)}'))

        table.on('select', function(e, dt, type, indexes) {
            let row = table.row(indexes).node();
            let spectrum = $(row).attr('data-spectrum');
            plot.update(JSON.parse(spectrum));
        });

        table.rows(':eq(0)').select();

        $('#filterForm').appendTo('#filter');

        $('#species, #source, #disease').change(function () {
            $('#filterForm').submit();
        })
    });
</script>
