<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section>
    <h1>Spectrum List</h1>

    <div id="plot" style="display: inline-block; vertical-align: top;"></div>

    <div align="center" style="display: inline-block; vertical-align: top; width: 400px;">
        <table id="spectrum_table" class="display nowrap" style="width: 100%;">
            <thead>
            <tr>
                <th>Spectrum</th>
                <th>Sample Source</th>
                <th>View</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${cluster.spectra}" var="spectrum" varStatus="status">

                <tr data-spectrum='${dulab:spectrumToJson(spectrum)}'>
                    <td>${spectrum.name}<br/>
                        <small>${spectrum.submission.name}</small>
                    </td>
                    <td>${spectrum.submission.sampleSourceType.label}</td>
                    <!--more horiz-->
                    <td><a href="/spectrum/${spectrum.id}/"><i class="material-icons">&#xE5D3;</i></a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section id="pieChartSection">
    <h1>Sample Source Distribution</h1>
    <div id="pieChart" align="center"></div>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
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
    });
</script>

<script src="/resources/js/d3/d3.min.js"></script>
<script src="/resources/js/twospectraplot.js"></script>
<script src="/resources/js/piechart.js"></script>
<script>
    // Add Spectrum Plot
    var plot = new TwoSpectraPlot('plot', ${dulab:spectrumToJson(cluster.consensusSpectrum)});

    // Add pie chart
    addPieChart('pieChart', ${dulab:pieChartData(cluster)});
</script>

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>