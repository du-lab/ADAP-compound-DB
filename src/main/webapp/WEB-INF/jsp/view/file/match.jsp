<%--@elvariable id="querySpectrum" type="org.dulab.models.Spectrum"--%>
<%--@elvariable id="hits" type="java.util.List<org.dulab.models.Hit>"--%>
<%--@elvariable id="searchForm" type="org.dulab.site.controllers.SearchController.SearchForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Query Spectrum</h1>

    <div align="center">
        <table>
            <tr>
                <td>
                    ${querySpectrum.name}<br/>
                    <small>${dulab:abbreviate(querySpectrum.properties, 80)}</small>
                </td>
                <td>
                    ${querySpectrum.submission.name}<br/>
                    <small>${querySpectrum.submission.chromatographyType.label}</small>
                </td>
                <!--more horiz-->
                <td><a href="/spectrum/${querySpectrum.id}/"><i class="material-icons">&#xE5D3;</i></a></td>
            </tr>
        </table>
    </div>
</section>

<c:if test="${hits != null && hits.size() > 0}">
    <section id="chartSection">
        <h1>Comparison</h1>
        <div id="chartDiv" align="center"></div>
    </section>
</c:if>

<section>
    <h1>Matching Hits</h1>

    <div align="center">
        <c:choose>
            <c:when test="${hits != null && hits.size() > 0}">
                <table class="clickable">
                    <tr>
                        <th>Score</th>
                        <th>Spectrum</th>
                        <th>Submission</th>
                        <th>View</th>
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
                            <td>
                                ${hit.spectrum.name}<br/>
                                <small>${dulab:abbreviate(hit.spectrum.properties, 80)}</small>
                            </td>
                            <td>
                                ${hit.spectrum.submission.name}<br/>
                                <small>${hit.spectrum.submission.chromatographyType.label}</small>
                            </td>
                            <!--more horiz-->
                            <td><a href="/spectrum/${hit.spectrum.id}/"><i class="material-icons">&#xE5D3;</i></a></td>
                        </tr>
                    </c:forEach>
                </table>
            </c:when>
            <c:otherwise>
                <p>There is no mass spectra to display.</p>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<section>
    <h1>Query Parameters</h1>
    <div align="center">
        <div align="left" class="subsection">
            <p class="errors">${searchResultMessage}</p>
            <c:if test="${validationErrors != null}">
                <div class="errors">
                    <ul>
                        <c:forEach items="${validationErrors}" var="error">
                            <li><c:out value="${error.message}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <form:form method="post" modelAttribute="searchForm">
                <form:errors path="" cssClass="errors"/>

                <div class="subsection">
                    <form:label path="mzTolerance">M/z tolerance:</form:label><br/>
                    <form:input path="mzTolerance"/><br/>
                    <form:errors path="mzTolerance" cssClass="errors"/><br/>

                    <form:label path="numHits">Maximum number of hits:</form:label><br/>
                    <form:input path="numHits"/><br/>
                    <form:errors path="numHits" cssClass="errors"/><br/>

                    <form:label path="scoreThreshold">Matching score threshold:</form:label><br/>
                    <form:input path="scoreThreshold"/><br/>
                    <form:errors path="scoreThreshold" cssClass="errors"/><br/>
                </div>

                <div class="subsection">
                    <label>
                        <form:checkbox path="chromatographyTypeCheck"
                                       onchange="document.getElementById('chromatographyTypeSelect').disabled = !this.checked"/>
                        Chromatography Type
                    </label><br/>
                    <form:select id="chromatographyTypeSelect"
                                 path="chromatographyType"
                                 items="${chromatographyTypes}"
                                 itemLabel="label"
                                 disabled="true"/><br/>
                    <form:errors path="chromatographyType" cssClass="errors"/><br/>

                    <label>
                        <form:checkbox path="submissionCategoryCheck"
                                       onchange="document.getElementById('submissionCategorySelect').disabled = !this.checked"/>
                        Submission Categories
                    </label><br/>
                    <form:select id="submissionCategorySelect"
                                 path="submissionCategoryIds"
                                 items="${submissionCategories}"
                                 itemValue="Id"
                                 disabled="true"
                                 size="${fn:length(submissionCategories)}"
                                 cssStyle="max-height: 200px;"/><br/>
                    <form:errors path="submissionCategoryIds" cssClass="errors"/><br/>
                </div>

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
    var firstRow = document.getElementById("firstRow");
    if (firstRow != null)
        firstRow.click();
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />