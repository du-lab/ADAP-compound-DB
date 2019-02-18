<%--@elvariable id="spectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
    <c:when test="${spectrum.file.submission.id > 0}">
        <c:set var="submissionUrl">/submission/${spectrum.file.submission.id}/</c:set>
    </c:when>
    <c:otherwise>
        <c:set var="submissionUrl">/file/</c:set>
    </c:otherwise>
</c:choose>

<section>
    <div class="tabbed-pane">
        <span class="active" data-tab="spectrum">Spectrum Properties</span>
        <span data-tab="peaks">Peaks</span>
    </div>

    <div id="spectrum" align="center">
        <table id="property_table" class="display responsive" style="clear: none; max-width: 1000px;">
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
            <c:if test="${spectrum.file != null}">
                <tr>
                    <td><strong>File:</strong></td>
                    <td>${spectrum.file.name}</td>
                </tr>
            </c:if>
            <c:if test="${spectrum.file!= null && spectrum.file.submission != null}">
                <tr>
                    <td><strong>Submission:</strong></td>
                    <td><a href="${submissionUrl}">${spectrum.file.submission.name}</a></td>
                </tr>
            </c:if>
            <c:if test="${spectrum.cluster != null}">
                <tr>
                    <td><strong>Cluster:</strong></td>
                    <td><a href="/cluster/${spectrum.cluster.id}/">${spectrum.cluster}</a></td>
                </tr>
            </c:if>
            <c:forEach items="${spectrum.properties}" var="property">
                <tr>
                    <td><strong>${property.name}:</strong></td>
                    <td>${property.value}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div id="peaks" class="hide" align="center">
        <div id="plot" style="" class="plot"></div>
        <div style="display: inline-block; max-width: 100%; vertical-align: top;">
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
<div align="center">
    <a href="search/" class="button">Search</a>
</div>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrum_plot.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/AdapCompoundDb/js/tabs.js"/>"></script>
<script>

    $(".tabbed-pane").each(function() {
        $(this).tabbedPane();
    });
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

        $('#peak_table').DataTable({
            responsive: true,
            scrollX: true,
            scroller: true,
            responsive: true,
            scrollX: true,
            scroller: true
        });

        SpectrumPlot('plot', ${dulab:spectrumToJson(spectrum)});
    })
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