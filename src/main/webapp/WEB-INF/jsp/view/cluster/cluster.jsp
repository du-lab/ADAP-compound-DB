<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submissionCategoryMap" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, org.dulab.adapcompounddb.models.entities.SubmissionCategory>"--%>
<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>

<section>
    <h1>Spectrum Plot</h1>

    <div align="center">
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
</section>

<section id="pieChartSection">
    <div align="center">
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
</section>

<section>
    <h1>Spectrum List</h1>
    <table id="big_spectrum_table" class="display nowrap" style="width: 100%;">
        <thead>
        <tr>
            <th>Id</th>
            <th>Spectrum</th>
            <c:forEach items="${submissionCategoryTypes}" var="type">
                <th>${type.label}</th>
            </c:forEach>
            <th>View</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${cluster.spectra}" var="spectrum">
            <tr>
                <td>${spectrum.id}</td>
                <td><a href="/spectrum/${spectrum.id}/">${spectrum.name}</a><br/>
                    <small><a href="/submission/${spectrum.file.submission.id}/">${spectrum.file.submission.name}</a>
                    </small>
                </td>
                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <td>${spectrum.file.submission.getCategory(type)}</td>
                </c:forEach>
                <td><a href="/spectrum/${spectrum.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $(document).ready(function () {

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

<script src="/resources/d3/d3.min.js"></script>
<script src="/resources/AdapCompoundDb/js/twospectraplot.js"></script>
<script src="/resources/AdapCompoundDb/js/piechart.js"></script>
<script>
    // Add Spectrum Plot
    var plot = new TwoSpectraPlot('plot', ${dulab:spectrumToJson(cluster.consensusSpectrum)});

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