<%--@elvariable id="submissionSources" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionSource>"--%>
<%--@elvariable id="submissionSpecies" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionSpecimen>"--%>
<%--@elvariable id="submissionDiseases" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionDisease>"--%>
<%--@elvariable id="cluster" type="org.dulab.adapcompounddb.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

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

                    <tr data-spectrum='${dulab:spectrumToJson(spectrum)}'>
                        <td>${spectrum.name}<br/>
                            <small>${spectrum.submission.name}</small>
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
        <div align="center" style="display: inline-block; margin: 10px;">
            <h1>Sources Distribution</h1>
            <div id="sourcePieChart" align="center"></div>
        </div>
        <div align="center" style="display: inline-block; margin: 10px;">
            <h1>Species Distribution</h1>
            <div id="specimenPieChart" align="center"></div>
        </div>
        <div align="center" style="display: inline-block; margin: 10px;">
            <h1>Diseases Distribution</h1>
            <div id="diseasePieChart" align="center"></div>
        </div>
    </div>
</section>

<section>
    <h1>Spectrum List</h1>
    <table id="big_spectrum_table" class="display nowrap" style="width: 100%;">
        <thead>
        <tr>
            <th>Id</th>
            <th>Spectrum</th>
            <th>Sources</th>
            <th>Species</th>
            <th>Diseases</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${cluster.spectra}" var="spectrum">
            <tr>
                <td>${spectrum.id}</td>
                <td><a href="/spectrum/${spectrum.id}/">${spectrum.name}</a><br/>
                    <small><a href="/submission/${spectrum.submission.id}/">${spectrum.submission.name}</a></small>
                </td>
                <td>${spectrum.submission.source.name}</td>
                <td>${spectrum.submission.specimen.name}</td>
                <td>${spectrum.submission.disease.name}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
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

        $('#big_spectrum_table').DataTable();
    });
</script>

<script src="/resources/js/d3/d3.min.js"></script>
<script src="/resources/js/twospectraplot.js"></script>
<script src="/resources/js/piechart.js"></script>
<script>
    // Add Spectrum Plot
    var plot = new TwoSpectraPlot('plot', ${dulab:spectrumToJson(cluster.consensusSpectrum)});

    // Add pie chart
    addPieChart('sourcePieChart', ${dulab:clusterSourceToJson(cluster, submissionSources)});
    addPieChart('specimenPieChart', ${dulab:clusterSpecimenToJson(cluster, submissionSpecies)});
    addPieChart('diseasePieChart', ${dulab:clusterDiseaseToJson(cluster, submissionDiseases)});
</script>

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>