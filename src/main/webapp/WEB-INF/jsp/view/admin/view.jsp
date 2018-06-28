<%--@elvariable id="statistics" type="java.util.Map<org.dulab.adapcompounddb.models.ChromatographyType, org.dulab.adapcompounddb.models.Statistics>"--%>
<%--@elvariable id="clusters" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumCluster>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section>
    <div id="progressBarDiv" class="progress_bar"></div>
</section>

<section>
    <h1>Number of Spectra in Library</h1>
    <div align="center">
        <table>
            <tr>
                <th></th>
                <th>Submitted</th>
                <th>Unmatched</th>
                <th>Consensus</th>
                <th>Matches</th>
            </tr>
            <c:forEach items="${statistics}" var="mapEntry">
                <tr>
                    <td>${mapEntry.key.label}</td>
                    <td>${mapEntry.value.numSubmittedSpectra}</td>
                    <td>${mapEntry.value.numUnmatchedSpectra}</td>
                    <td>${mapEntry.value.numConsensusSpectra}</td>
                    <td>${mapEntry.value.numSpectrumMatches}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Admin Tools</h1>
    <table>
        <tr>
            <td><a href="calculatescores/"
                   class="button"
                   onclick="progressBar.start('calculatescores/progress')">Calculate Matching Scores...</a></td>
            <td>Calculates matching scores for all spectra in the library</td>
        </tr>
        <tr>
            <td><a href="cluster/" class="button">Cluster spectra...</a></td>
            <td>Cluster spectra into clusters</td>
        </tr>
    </table>
</section>

<section>
    <h1>Clusters</h1>
    <div align="center">
        <table id="cluster_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>Name</th>
                <th>Num Spectra</th>
                <th>Matching Score</th>
                <%--<th>Chromatography</th>--%>
                <th>View</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${clusters}" var="cluster">
                <tr>
                    <td>${cluster}</td>
                    <td>${cluster.size}</td>
                    <td>${dulab:toIntegerScore(cluster.diameter)}</td>
                        <%--<td>${cluster.chromatographyType.label}</td>--%>
                    <td>
                        <!--more horiz-->
                        <a href="/cluster/${cluster.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<!-- End the middle column -->

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/js/progressBar.js"/>"></script>
<script>
    var progressBar = new ProgressBar('progressBarDiv');

    $(document).ready(function () {
        $('#cluster_table').DataTable();
    });
</script>

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>