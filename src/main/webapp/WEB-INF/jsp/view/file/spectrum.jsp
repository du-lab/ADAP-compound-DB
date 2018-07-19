<%--@elvariable id="spectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<%--<section class="transparent">--%>
<%--<div align="right">--%>
<%--<a href="<c:url value="${header.referer}"/>" class="button">Back to file</a>--%>
<%--</div>--%>
<%--</section>--%>

<section>
    <h1>Spectrum ${spectrum.name}</h1>

    <div align="left" style="float: left">
        <p><a href="/submission/${spectrum.file.submission.id}/" class="button">Submission</a></p>
    </div>

    <div align="right" style="float: right">
        <p><a href="search/" class="button">Library Search</a></p>
    </div>

    <div align="center">
        <table>
            <tr>
                <th>Property</th>
                <th>Value</th>
            </tr>
            <c:forEach var="e" items="${spectrum.properties}">
                <tr>
                    <td>${e.name}</td>
                    <td>${e.value}</td>
                </tr>
            </c:forEach>
            <tr>
                <td>Chromatography Type</td>
                <td>${spectrum.chromatographyType.label}</td>
            </tr>
            <tr>
                <td>Submission</td>
                <td>
                    <a href="/submission/${spectrum.file.submission.id}/">
                        ${spectrum.file.submission.name} (${spectrum.chromatographyType.label})<br>
                        <small>${spectrum.file.submission.description}</small>
                    </a>
                </td>
            </tr>
        </table>
    </div>
</section>

<section>
    <h1>Peaks</h1>
    <div align="center">
        <div id="chartDiv" style="display: inline-block;"></div>
        <div style="display: inline-block; max-width: 400px; max-height: 400px; overflow-y: scroll;">
            <table>
                <tr>
                    <th>M/z</th>
                    <th>Intensity</th>
                </tr>
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
            </table>
        </div>
    </div>
</section>

<%--<section class="transparent">--%>
<%--<div align="right">--%>
<%--<a href="<c:url value="${header.referer}"/>" class="button">Back to file</a>--%>
<%--</div>--%>
<%--</section>--%>

<script src="<c:url value="/resources/js/zingchart/zingchart.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/spectrum.js"/>"></script>
<script>
    addPlot("chartDiv", '${dulab:peaksToJson(spectrum.peaks)}');
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>