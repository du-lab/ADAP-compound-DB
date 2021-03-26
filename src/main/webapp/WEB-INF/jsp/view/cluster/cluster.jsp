<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submissionCategoryMap" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, org.dulab.adapcompounddb.models.entities.SubmissionCategory>"--%>
<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="desktop" style="text-align: center">
    <strong>
        ${cluster.consensusSpectrum.name} |
        <img src="${pageContext.request.contextPath}/${cluster.consensusSpectrum.chromatographyType.iconPath}"
             class="icon" alt="${cluster.consensusSpectrum.chromatographyType.name()}"/> |
        ${cluster.size} total spectra | ${dulab:toIntegerScore(cluster.diameter)} similarity score
        | ${dulab:formatDouble(cluster.aveSignificance)} significance
    </strong>
</div>

<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-tabs">
                    <ul class="nav nav-tabs nav-fill nav-justified" role="tablist">
                        <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#consensus_spectrum">Consensus
                            Spectrum</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#spectrum_plot">Spectrum
                            Plot</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab"
                                                href="#tag_distributions">Distributions</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#pie_chart">Pie Chart</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#spectrum_list">Spectrum
                            List</a></li>
                    </ul>
                </div>

                <div class="card-body tab-content small">
                    <div id="consensus_spectrum" class="tab-pane active" role="tabpanel">
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
                                <td>
                                    <a href="${pageContext.request.contextPath}/spectrum/${cluster.consensusSpectrum.id}/">
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
                                    <td><strong>Average ANOVA P-value</strong></td>
                                    <td>${dulab:formatDouble(cluster.aveSignificance)}</td>
                                </tr>
                            </c:if>
                            <c:if test="${cluster.minSignificance != null}">
                                <tr>
                                    <td><strong>Minimum ANOVA P-value</strong></td>
                                    <td>${dulab:formatDouble(cluster.minSignificance)}</td>
                                </tr>
                            </c:if>
                            <c:if test="${cluster.maxSignificance != null}">
                                <tr>
                                    <td><strong>Maximum ANOVA P-value</strong></td>
                                    <td>${dulab:formatDouble(cluster.maxSignificance)}</td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>

                    <div id="spectrum_plot" class="tab-pane" role="tabpanel">
                        <div id="plot" style="display: inline-block; vertical-align: top;" class="plot"></div>

                        <div align="center" style="display: inline-block; vertical-align: top;">
                            <table id="spectrum_table" class="display responsive" style="width: 100%;">
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

                    <div id="tag_distributions" class="tab-pane" role="tabpanel">
                        <script src="<c:url value="/resources/npm/node_modules/d3v5/dist/d3.min.js"/>"></script>
                        <script src="${pageContext.request.contextPath}/resources/AdapCompoundDb/js/twohistograms.js"></script>
                        <p>
                        <div id="rectangle"
                             style=" display:inline-block; width:20px; height:20px; background-color:#b47cff"></div>
                        is the tag distributions of all database.</p>
                        <p>
                        <div id="rectangle"
                             style=" display:inline-block; width:20px; height:20px; background-color:#ffb47c"></div>
                        is the tag distributions of individual cluster.</p>
                        <c:forEach items="${cluster.tagDistributions}" var="tagDistribution" varStatus="status">
                            <div id="div${status.index}" style="display: inline-block; margin: 10px;text-align: left;">
                                <script>
                                    var tag = '${tagDistribution.label}';
                                    var dataSet = '${tagDistribution.distribution}';
                                    var pValue = '${tagDistribution.PValue}';
                                    addClusterTagsHistogram('div' +${status.index}, tag, dataSet, pValue);
                                </script>
                            </div>
                        </c:forEach>
                    </div>

                    <div id="pie_chart" class="tab-pane" role="tabpanel" style="text-align: center">
                        <div id='charts'></div>
                    </div>

                    <div id="spectrum_list" class="tab-pane" role="tabpanel">
                        <table id="big_spectrum_table" class="display responsive">
                            <thead>
                            <tr>
                                <th>Spectrum</th>
                                <th title="Retention time (min)">RT</th>
                                <th>Precursor m/z</th>
                                <th>Significance</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${cluster.spectra}" var="spectrum">
                                <tr>
                                    <td><a href="${pageContext.request.contextPath}/spectrum/${spectrum.id}/">${spectrum.name}</a><br/>
                                        <small><a
                                                href="${pageContext.request.contextPath}/submission/${spectrum.file.submission.id}/">${spectrum.file.submission.name}</a>
                                        </small>
                                    </td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.retentionTime}"/></td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.precursor}"/></td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${spectrum.significance}"/></td>
                                    <td><a href="${pageContext.request.contextPath}/spectrum/${spectrum.id}/"><i class="material-icons"
                                                                                                                 title="View">&#xE5D3;</i></a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
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
<script>
    $(document).ready(function () {

        $('#property_table').DataTable({
            info: false,
            ordering: false,
            paging: false,
            searching: false,
            responsive: true,
            scrollX: true,
            scroller: true
        });

        var table = $('#spectrum_table').DataTable({
            bLengthChange: false,
            scrollX: true,
            select: {style: 'single'},
            responsive: true,
            scroller: true,
            "fnInitComplete": function (oSettings, json) {
                $('#spectrum_plot').addClass("hide");
            }
        });

        table.on('select', function (e, dt, type, indexes) {
            var row = table.row(indexes).node();
            var spectrum = JSON.parse($(row).attr('data-spectrum'));
            plot.update(spectrum);
        });

        table.rows(':eq(0)').select();

        $('#big_spectrum_table').DataTable({
            responsive: true,
            scrollX: true,
            scroller: true,
            "fnInitComplete": function (oSettings, json) {
                $('#spectrum_list').addClass("hide");
            }
        });

        // Adjust column widths when a table becomes visible
        $(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
            $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
        });
    });
</script>

<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/piechart.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twohistograms.js"/>"></script>

<script>
    // Add Spectrum Plot
    var plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(cluster.consensusSpectrum)}'));

    // $(".tabbed-pane").each(function () {
    //     $(this).tabbedPane();
    // });

    var pieChartVal = '${dulab:clusterTagsToJson(cluster.spectra)}';
    var jsonVal = JSON.parse(pieChartVal);

    $pieDiv = $("#charts");
    $.each(jsonVal, function (k, v) {
        $pieDiv.append('<div style="display: inline-block; margin: 10px;">' +
            '<div>diversity: ' + parseFloat(v.diversity).toFixed(2) + '</div>' +
            '<div id="PieChart-' + k + '" style="margin: 10px"></div>' +
            '<div>' + v.name + '</div>' +
            '</div>');
        addPieChart('PieChart-' + k, v.values);
    });
</script>
<%--<style>--%>
<%--    .selection {--%>
<%--        fill: #ADD8E6;--%>
<%--        stroke: #ADD8E6;--%>
<%--        fill-opacity: 0.3;--%>
<%--        stroke-opacity: 0.7;--%>
<%--        stroke-width: 2;--%>
<%--        stroke-dasharray: 5, 5;--%>
<%--    }--%>

<%--    .charts div {--%>
<%--        display: inline-block;--%>
<%--    }--%>
<%--</style>--%>