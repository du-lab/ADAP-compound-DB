<%--@elvariable id="spectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<c:choose>
    <c:when test="${spectrum.file.submission.id > 0}">
        <c:set var="submissionUrl">${pageContext.request.contextPath}/submission/${spectrum.file.submission.id}/</c:set>
    </c:when>
    <c:otherwise>
        <c:set var="submissionUrl">/file/</c:set>
    </c:otherwise>
</c:choose>

<section>
    <h1>Spectrum ${spectrum}</h1>
    <table id="property_table" class="display">
        <thead>
        <tr>
            <th>Property</th>
            <th>Value</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><strong>Full Name:</strong></td>
            <td>${spectrum.name}</td>
        </tr>
        <tr>
            <td><strong>Chromatography:</strong></td>
            <td>${spectrum.chromatographyType.label}</td>
        </tr>
        <tr>
            <td><strong>File:</strong></td>
            <td>${spectrum.file.name}</td>
        </tr>
        <tr>
            <td><strong>Submission:</strong></td>
            <td><a href="${submissionUrl}">${spectrum.file.submission.name}</a></td>
        </tr>
        <c:forEach items="${spectrum.properties}" var="property">
            <tr>
                <td><strong>${property.name}:</strong></td>
                <td>${property.value}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>

<section>
    <h1>Peaks</h1>
    <div align="center">
        <div id="plot" style="display: inline-block; vertical-align: top; margin: 20px;"></div>
        <div style="display: inline-block; max-width: 500px; vertical-align: top;">
            <table id="peak_table" class="display" style="width: 100%;">
                <thead>
                <tr>
                    <th>M/z</th>
                    <th>Intensity</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${dulab:peaksToJson(spectrum.peaks)}" var="peak">
                    <tr>
                        <td>
                            <fmt:formatNumber maxFractionDigits="4" groupingUsed="false">${peak[0]}</fmt:formatNumber>
                        </td>
                        <td>
                            <fmt:formatNumber maxFractionDigits="4" groupingUsed="false">${peak[1]}</fmt:formatNumber>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</section>

<section>
    <div align="center">
        <a href="search/" class="button">Search</a>
    </div>
</section>

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/js/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/js/spectrum_plot.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#property_table').DataTable({
            info: false,
            ordering: false,
            paging: false,
            searching: false
        });

        $('#peak_table').DataTable();

        SpectrumPlot('plot', ${dulab:spectrumToJson(spectrum)});
    })
</script>


<%--<script src="<c:url value="/resources/js/zingchart/zingchart.min.js"/>"></script>--%>
<%--<script type="text/javascript" src="<c:url value="/resources/js/spectrum.js"/>"></script>--%>
<%--<script>--%>
<%--addPlot("chartDiv", '${dulab:peaksToJson(spectrum.peaks)}');--%>
<%--</script>--%>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>