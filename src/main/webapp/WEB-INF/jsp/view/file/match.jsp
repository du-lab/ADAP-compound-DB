<%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%--@elvariable id="matches" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumMatch>"--%>
<%--@elvariable id="submissionSources" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionSource"--%>
<%--@elvariable id="submissionSpecies" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionSpecimen"--%>
<%--@elvariable id="submissionDiseases" type="java.util.List<org.dulab.adapcompounddb.models.entities.SubmissionDisease"--%>
<%--@elvariable id="searchForm" type="org.dulab.adapcompounddb.site.controllers.SearchController.SearchForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

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
                    <small>${querySpectrum.chromatographyType.label}</small>
                </td>
            </tr>
        </table>
    </div>
</section>

<c:if test="${matches != null && matches.size() > 0}">
    <section id="chartSection">
        <h1>Comparison</h1>
        <div id="plot" align="center"></div>
    </section>
</c:if>

<section>
    <h1>Matching Hits</h1>

    <div align="center">
        <c:choose>
            <c:when test="${matches != null && matches.size() > 0}">
                <table id="match_table" class="display nowrap" style="width: 100%;">
                    <thead>
                    <tr>
                        <th>Score</th>
                        <th>Spectrum</th>
                        <th>Size</th>
                        <th>Sources</th>
                        <th>Species</th>
                        <th>Disease</th>
                        <th>View</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${matches}" var="match" varStatus="status">
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.score * 1000}"
                                          var="score"/>

                        <tr data-spectrum='${dulab:spectrumToJson(match.matchSpectrum)}'>
                            <td>${score}</td>
                            <td><a href="/cluster/${match.matchSpectrum.cluster.id}/">${match.matchSpectrum.name}</a></td>
                            <td>${match.matchSpectrum.cluster.size}</td>
                            <td>${dulab:jsonToHtml(dulab:clusterSourceToJson(match.matchSpectrum.cluster, submissionSources))}</td>
                            <td>${dulab:jsonToHtml(dulab:clusterSpecimenToJson(match.matchSpectrum.cluster, submissionSpecies))}</td>
                            <td>${dulab:jsonToHtml(dulab:clusterDiseaseToJson(match.matchSpectrum.cluster, submissionDiseases))}</td>
                            <!--more horiz-->
                            <td><a href="/cluster/${match.matchSpectrum.cluster.id}/"><i
                                    class="material-icons">&#xE5D3;</i></a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
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

                    <%--<form:label path="numHits">Maximum number of hits:</form:label><br/>--%>
                    <%--<form:input path="numHits"/><br/>--%>
                    <%--<form:errors path="numHits" cssClass="errors"/><br/>--%>

                    <form:label path="scoreThreshold">Matching score threshold:</form:label><br/>
                    <form:input path="scoreThreshold"/><br/>
                    <form:errors path="scoreThreshold" cssClass="errors"/><br/>
                </div>

                <%--<div class="subsection">--%>
                    <%--<label>--%>
                        <%--<form:checkbox path="chromatographyTypeCheck"--%>
                                       <%--onchange="document.getElementById('chromatographyTypeSelect').disabled = !this.checked"/>--%>
                        <%--Chromatography Type--%>
                    <%--</label><br/>--%>
                    <%--<form:select id="chromatographyTypeSelect"--%>
                                 <%--path="chromatographyType"--%>
                                 <%--items="${chromatographyTypes}"--%>
                                 <%--itemLabel="label"--%>
                                 <%--disabled="true"/><br/>--%>
                    <%--<form:errors path="chromatographyType" cssClass="errors"/><br/>--%>

                    <%--<label>--%>
                        <%--<form:checkbox path="submissionCategoryCheck"--%>
                                       <%--onchange="document.getElementById('submissionCategorySelect').disabled = !this.checked"/>--%>
                        <%--Submission Categories--%>
                    <%--</label><br/>--%>
                    <%--<form:select id="submissionCategorySelect"--%>
                                 <%--path="submissionCategoryIds"--%>
                                 <%--items="${submissionCategories}"--%>
                                 <%--itemValue="Id"--%>
                                 <%--disabled="true"--%>
                                 <%--size="${fn:length(submissionCategories)}"--%>
                                 <%--cssStyle="max-height: 200px;"/><br/>--%>
                    <%--<form:errors path="submissionCategoryIds" cssClass="errors"/><br/>--%>
                <%--</div>--%>

                <div align="center">
                    <input type="submit" value="Search"/>
                </div>
            </form:form>
        </div>
    </div>
</section>

<script src="<c:url value="/resources/js/DataTables/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/js/DataTables/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $(document).ready(function () {

        var table = $('#match_table').DataTable({
            'order': [[0, 'desc']],
            scrollX: true,
            select: {style: 'single'}
        });

        table.on('select', function (e, dt, type, indexes) {
            var row = table.row(indexes).node();
            var spectrum = JSON.parse($(row).attr('data-spectrum'));
            plot.update(spectrum);
        });

        table.rows(':eq(0)').select();
    });
</script>

<script src="/resources/js/d3/d3.min.js"></script>
<script src="/resources/js/twospectraplot.js"></script>
<script>
    var plot = new TwoSpectraPlot('plot', ${dulab:spectrumToJson(querySpectrum)})
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>