<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<%--<section id="chartSection">--%>
<%--<h1>Mass Spectrum</h1>--%>
<%--<div id="chartDiv" align="center"></div>--%>
<%--</section>--%>

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

                <tr data-peaks='${dulab:peaksToJson(spectrum.peaks)}'>
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

<script>
    $(document).ready(function () {

        var table = $('#spectrum_table').DataTable({
            bLengthChange: false,
            scrollX: true,
            select: {style: 'single'}
        });

        table.on('select', function (e, dt, type, indexes) {
            var row = table.row(indexes).node();
            var data = $(row).attr('data-peaks');
            <%--addPlot('chartDiv', '${dulab:peaksToJson(cluster.consensusSpectrum.peaks)}',--%>
                <%--'${cluster.consensusSpectrum.name}',--%>
                <%--data,--%>
                <%--'${spectrum.name}', 0);--%>
            addPlot(JSON.parse(data))
        });

        table.rows(':eq(0)').select();
    });
</script>

<%--<script src="<c:url value="/resources/js/select.js"/>"></script>--%>
<%--<script src="<c:url value="/resources/js/zingchart/zingchart.min.js"/>"></script>--%>
<%--<script src="<c:url value="/resources/js/spectrumMatch.js"/>"></script>--%>

<script src="https://d3js.org/d3.v4.min.js"></script>
<script src="/resources/js/piechart.js"></script>
<script>
    addPieChart('pieChart', ${dulab:pieChartData(cluster)})
</script>
<script>
    function addPlot(dataset) {

        var width = 600;
        var height = 400;

        var xScale = d3.scaleLinear()
            .domain([
                d3.min(dataset, function(d) {return d[0]}),
                d3.max(dataset, function(d) {return d[1]})
            ])
            .range([0, width]);

        var yScale = d3.scaleLinear()
            .domain([0, 100])
            .range([height, 0]);

        var line = d3.line()
            .x(function(d) {console.log('d: ', d); return xScale(d[0])})
            .y(function(d) {return yScale(d[1])});

        var svg = d3.select('#plot')
            .append('svg')
            .attr('width', width)
            .attr('height', height);

        svg.append('path')
            .datum(dataset)
            .attr('class', 'line')
            .attr('d', line);
    }

</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>