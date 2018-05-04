<%--@elvariable id="cluster" type="org.dulab.models.entities.SpectrumCluster"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section id="chartSection">
    <h1>Mass Spectrum</h1>
    <div id="chartDiv" align="center"></div>
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
                        addPlot('chartDiv', '${dulab:peaksToJson(spectrum.peaks)}');">

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
<script src="<c:url value="/resources/js/spectrum.js"/>"></script>
<script>
    var firstRow = document.getElementById("firstRow");
    if (firstRow != null)
        firstRow.click();
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />