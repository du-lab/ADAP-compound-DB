<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section id="chartSection">
    <h1>Mass Spectrum</h1>
    <div id="chartDiv" align="center"></div>
</section>

<section id="pieChartSection">
    <h1>Sample Source Distribution</h1>
    <div id="pieChart" align="center"></div>
</section>

<section>
    <h1>Spectrum List</h1>

    <div align="center">
        <table class="clickable">
            <tr>
                <th>Spectrum</th>
                <th>Submission</th>
                <th>View</th>
            </tr>
            <c:forEach items="${cluster.spectra}" var="spectrum" varStatus="status">

                <tr ${status.first ? 'id="firstRow"' : ''} onclick="select(this);
                        addPlot('chartDiv', '${dulab:peaksToJson(cluster.consensusSpectrum.peaks)}',
                        '${cluster.consensusSpectrum.name}',
                        '${dulab:peaksToJson(spectrum.peaks)}',
                        '${spectrum.name}', 0);">
                        <%--addPlot('chartDiv', '${dulab:peaksToJson(spectrum.peaks)}');">--%>

                    <td>
                            ${spectrum.name}<br/>
                        <small>${dulab:abbreviate(spectrum.properties, 80)}</small>
                    </td>
                    <td>
                            ${spectrum.submission.name}<br/>
                        <small>${spectrum.submission.chromatographyType.label}</small>
                    </td>
                    <!--more horiz-->
                    <td><a href="/spectrum/${spectrum.id}/"><i class="material-icons">&#xE5D3;</i></a></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<script src="<c:url value="/resources/js/select.js"/>"></script>
<script src="<c:url value="/resources/js/zingchart/zingchart.min.js"/>"></script>
<script src="<c:url value="/resources/js/spectrumMatch.js"/>"></script>
<script>
    var firstRow = document.getElementById("firstRow");
    if (firstRow != null)
        firstRow.click();
</script>

<script src="https://d3js.org/d3.v4.min.js"></script>
<script>

    var dataset = ${dulab:pieChartData(cluster)};

    var width = 360;
    var height = 360;
    var radius = Math.min(width, height) / 2;
    var donutWidth = 75;
    var legendRectSize = 18;
    var legendSpacing = 4;

    var tooltip = d3.select('#pieChart')
        .append('div')
        .attr('class', 'tooltip');

    tooltip.append('div').attr('class', 'label');
    tooltip.append('div').attr('class', 'count');
    tooltip.append('div').attr('class', 'percent');

    // var color = d3.scaleOrdinal(d3.schemeCategory20b);
    var color = d3.scaleOrdinal(d3.schemeCategory10);

    var svg = d3.select('#pieChart')
        .append('svg')
        .attr('width', width)
        .attr('height', height)
        .append('g')
        .attr('transform', 'translate(' + (width / 2) + ', ' + (height / 2) + ')');

    var arc = d3.arc()
        .innerRadius(radius - donutWidth)
        .outerRadius(radius);

    var pie = d3.pie()
        .value(function(d) {return d.count})
        .sort(null);

    var path = svg.selectAll('path')
        .data(pie(dataset))
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', function(d, i) {return color(d.data.label);});

    path.on('mouseover', function(d) {

        var total = d3.sum(dataset.map(function(d) {
            return d.count;
        }))

        var percent = Math.round(1000 * d.data.count / total) / 10;
        tooltip.select('.label').html(d.data.label);
        tooltip.select('.count').html(d.data.count);
        tooltip.select('.percent').html(percent + '%');
        tooltip.style('display', 'block');
    });

    path.on('mouseout', function() {
        tooltip.style('display', 'none');
    });

    path.on('mousemove', function() {
        tooltip.style('top', (d3.event.layerY + 10) + 'px')
            .style('left', (d3.event.layerX + 10) + 'px');
    });

    var legend = svg.selectAll('.legend')
        .data(color.domain())
        .enter()
        .append('g')
        .attr('class', 'legend')
        .attr('transform', function(d, i) {
            var height = legendRectSize + legendSpacing;
            var offset = height * color.domain().length / 2;
            var horz = -4 * legendRectSize;
            var vert = i * height - offset;
            return 'translate(' + horz + ', ' + vert + ')';
        });

    legend.append('rect')
        .attr('width', legendRectSize)
        .attr('height', legendRectSize)
        .style('fill', color)
        .style('stroke', color);

    legend.append('text')
        .attr('x', legendRectSize + legendSpacing)
        .attr('y', legendRectSize - legendSpacing)
        .text(function(d) {return d;});

</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>