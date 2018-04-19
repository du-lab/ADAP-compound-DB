<%--@elvariable id="querySpectrum" type="org.dulab.models.Spectrum"--%>
<%--@elvariable id="hits" type="java.util.List<org.dulab.models.Hit>"--%>
<%--@elvariable id="form" type="org.dulab.site.controllers.SubmissionController.SpectrumSearchForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section id="chartSection">
    <h1>Spectrum peaks</h1>
    <div id="chartDiv" align="center"></div>
</section>

<section>
    <h1>Matching Hits</h1>

    <div align="center">
        <table class="clickable">
            <tr>
                <th>Score</th>
                <th>Spectrum</th>
                <th>Submission</th>
            </tr>
            <c:forEach items="${hits}" var="hit" varStatus="status">
                <fmt:formatNumber type="number"
                                  maxFractionDigits="0"
                                  groupingUsed="false"
                                  value="${hit.score * 1000}"
                                  var="score"/>

                <tr ${status.first ? 'id="firstRow"' : ''} onclick="select(this);
                            addPlot('chartDiv', '${dulab:peaksToJson(querySpectrum.peaks)}', '${querySpectrum.name}',
                                                '${dulab:peaksToJson(hit.spectrum.peaks)}', '${hit.spectrum.name}',
                                                '${score}');">

                    <td>${score}</td>
                    <td>${hit.spectrum.name}</td>
                    <td>${hit.spectrum.submission.name}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Query Spectrum</h1>
    <h2>${querySpectrum.name}</h2>
    <div align="center">
        <table>
            <c:forEach var="e" items="${querySpectrum.properties}">
                <tr>
                    <td>${e.name}</td>
                    <td>${e.value}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Query Parameters</h1>
    <div align="center">
        <div align="left" class="subsection">
            <c:if test="${validationErrors != null}">
                <div class="errors">
                    <ul>
                        <c:forEach items="${validationErrors}" var="error">
                            <li><c:out value="${error.message}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <form:form method="post" modelAttribute="form">
                <form:label path="mzTolerance">M/z tolerance:</form:label><br/>
                <form:input path="mzTolerance"/><br/>
                <form:errors path="mzTolerance" cssClass="errors"/><br/>

                <form:label path="numHits">Maximum number of hits:</form:label><br/>
                <form:input path="numHits"/><br/>
                <form:errors path="numHits" cssClass="errors"/><br/>

                <form:label path="scoreThreshold">Matching score threshold:</form:label><br/>
                <form:input path="scoreThreshold"/><br/>
                <form:errors path="scoreThreshold" cssClass="errors"/><br/>

                <div align="center">
                    <input type="submit" value="Search"/>
                </div>
            </form:form>
        </div>
    </div>
</section>

<script src="<c:url value="/resources/js/select.js"/>"></script>
<script src="<c:url value="/resources/js/zingchart/zingchart.min.js"/>"></script>
<script src="<c:url value="/resources/js/spectrumMatch.js"/>"></script>
<script>
    document.getElementById("firstRow").click();
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />