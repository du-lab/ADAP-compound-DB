<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submissionCategoryMap" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, org.dulab.adapcompounddb.models.entities.SubmissionCategory>"--%>
<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <div class="tabbed-pane">
        <span class="active" data-tab="consensus_spectrum">Consensus Spectrum</span>
        <span data-tab="spectrum_plot">Spectrum Plot</span>
        <span data-tab="pie_chart">Pie Chart</span>
        <span data-tab="spectrum_list">Spectrum List</span>
    </div>
    <div id="consensus_spectrum" align="center">
        <table id="property_table" class="display" style="width: 100%; max-width: 1000px;">
            <thead>
            <tr>
                <th>Property</th>
                <th>Value</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td><strong>Consensus Spectrum</strong></td>
                <td><a href="${pageContext.request.contextPath}/spectrum/${cluster.consensusSpectrum.id}/">
                    ${cluster.consensusSpectrum.name}</a></td>
            </tr>
            <c:if test="${cluster.consensusSpectrum.precursor != null}">
                <tr>
                    <td><strong>Precursor M/z</strong></td>
                    <td>${cluster.consensusSpectrum.precursor}</td>
                </tr>
            </c:if>
            <tr>
                <td><strong>Number of submitted spectra</strong></td>
                <td>${cluster.size}</td>
            </tr>
            <tr>
                <td><strong>Chromatography Type</strong></td>
                <td>
                    <img src="${pageContext.request.contextPath}/${cluster.consensusSpectrum.chromatographyType.iconPath}"
                         class="icon"/>&nbsp;${cluster.consensusSpectrum.chromatographyType.label}
                </td>
            </tr>
            <tr>
                <td><strong>Similarity Score</strong></td>
                <td>${dulab:toIntegerScore(cluster.diameter)}</td>
            </tr>
            <c:if test="${cluster.aveSignificance != null}">
                <tr>
                    <td><strong>Average significance</strong></td>
                    <td>${cluster.aveSignificance}</td>
                </tr>
            </c:if>
            <c:if test="${cluster.minSignificance != null}">
                <tr>
                    <td><strong>Minimum significance</strong></td>
                    <td>${cluster.minSignificance}</td>
                </tr>
            </c:if>
            <c:if test="${cluster.maxSignificance != null}">
                <tr>
                    <td><strong>Maximum significance</strong></td>
                    <td>${cluster.maxSignificance}</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div id="spectrum_plot" align="center" class="hide">
        <div id="plot" style="display: inline-block; vertical-align: top;"></div>

        <div align="center" style="display: inline-block; vertical-align: top; width: 400px;">
            <table id="spectrum_table" class="display nowrap" style="width: 100%;">
                <thead>
                <tr>
                    <th>Spectrum</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${cluster.spectra}" var="spectrum" varStatus="status">

                    <tr data-spectrum="<c:out value="${dulab:spectrumToJson(spectrum)}"/>">
                        <td>${spectrum.name}<br/>
                            <small>${spectrum.file.submission.name}</small>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div id="pie_chart" align="center" class="hide">
        <c:forEach items="${submissionCategoryTypes}" var="type">
            <div align="center" style="display: inline-block; margin: 10px;">
                <h2>${type.label} Distribution</h2>
                <c:forEach items="${cluster.diversityIndices}" var="diversityIndex">
                    <c:if test="${diversityIndex.id.categoryType == type}">
                        <div align="center" style="margin-bottom: 20px;">
                            <strong>Diversity:&nbsp;</strong>${diversityIndex.diversity}
                        </div>
                    </c:if>
                </c:forEach>
                <div id="${type.name()}PieChart" align="center"></div>
            </div>
        </c:forEach>
    </div>

    <div id="spectrum_list" align="center" class="hide">
        <table id="big_spectrum_table" class="display nowrap" style="width: 100%;">
            <thead>
            <tr>
                <th>Spectrum</th>
                <th title="Retention time (min)">RT</th>
                <th>Precursor m/z</th>
                <th>Significance</th>
                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <th>${type.label}</th>
                </c:forEach>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${cluster.spectra}" var="spectrum">
                <tr>
                    <td><a href="/spectrum/${spectrum.id}/">${spectrum.name}</a><br/>
                        <small><a href="/submission/${spectrum.file.submission.id}/">${spectrum.file.submission.name}</a>
                        </small>
                    </td>
                    <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.retentionTime}"/></td>
                    <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.precursor}"/></td>
                    <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.significance}"/></td>
                    <c:forEach items="${submissionCategoryTypes}" var="type">
                        <td>${spectrum.file.submission.getCategory(type)}</td>
                    </c:forEach>
                    <td><a href="/spectrum/${spectrum.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $(document).ready(function () {

        $('#property_table').DataTable({
            info: false,
            ordering: false,
            paging: false,
            searching: false
        });

        var table = $('#spectrum_table').DataTable({
            bLengthChange: false,
            scrollX: true,
            select: {style: 'single'}
        });

        table.on('select', function (e, dt, type, indexes) {
            var row = table.row(indexes).node();
            var spectrum = JSON.parse($(row).attr('data-spectrum'));
            plot.update(spectrum);
        });

        table.rows(':eq(0)').select();

        $('#big_spectrum_table').DataTable();
    });
</script>

<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/piechart.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/AdapCompoundDb/js/tabs.js"/>"></script>
<script>
    // Add Spectrum Plot
    var plot = new TwoSpectraPlot('plot', ${dulab:spectrumToJson(cluster.consensusSpectrum)});

    $(".tabbed-pane").each(function() {
        $(this).tabbedPane();
    });

    // Add pie chart
    <c:forEach items="${submissionCategoryTypes}" var="type">
    addPieChart('${type.name()}PieChart', ${dulab:clusterDistributionToJson(cluster.spectra, submissionCategoryMap.get(type))});
    </c:forEach>
</script>
<style>
    .selection {
        fill: #ADD8E6;
        stroke: #ADD8E6;
        fill-opacity: 0.3;
        stroke-opacity: 0.7;
        stroke-width: 2;
        stroke-dasharray: 5, 5;
    }
</style>