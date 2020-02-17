<%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%--@elvariable id="matches" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumMatch>"--%>
<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submissionCategoryMap" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, org.dulab.adapcompounddb.models.entities.SubmissionCategory>"--%>
<%--@elvariable id="searchForm" type="org.dulab.adapcompounddb.site.controllers.IndividualSearchController.SearchForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
                    ${querySpectrum.file.submission.name}<br/>
                    <small>${querySpectrum.chromatographyType.label}</small>
                </td>
            </tr>
        </table>
    </div>
</section>

<c:if test="${matches != null && matches.size() > 0}">
    <section id="chartSection">
        <h1>Comparison</h1>
        <div id="plot" align="center" class="plot"></div>
    </section>
</c:if>

<section>
    <h1>Matching Hits</h1>
    <div align="center">
        <jsp:include page="/WEB-INF/jsp/shared/cluster_table.jsp">
            <jsp:param name="ajax_url" value="${pageContext.request.contextPath}/rest/individual_search/json"/>
            <jsp:param name="query_spectrum" value="${querySpectrum}"/>
            <jsp:param name="score_threshold" value="#scoreThreshold"/>
            <jsp:param name="mz_tolerance" value="#mzTolerance"/>
            <jsp:param name="hide_query_spectrum" value="true"/>
        </jsp:include>
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

                <div id="accordion">
                    <h3>Search Parameters</h3>
                    <div>
                        <label>
                            <form:checkbox path="scoreThresholdCheck" onchange="
                                    $('#scoreThreshold').prop('disabled', !this.checked);
                                    $('#mzTolerance').prop('disabled', !this.checked);"/>
                            Spectral Similarity
                        </label><br/>
                        <form:label path="scoreThreshold">Matching Score Threshold</form:label><br/>
                        <form:input path="scoreThreshold"/><br/>
                        <form:errors path="scoreThreshold" cssClass="errors"/><br/>

                        <form:label path="mzTolerance">Product Ion M/z tolerance</form:label><br/>
                        <form:input path="mzTolerance"/><br/>
                        <form:errors path="mzTolerance" cssClass="errors"/><br/>

                        <c:if test="${querySpectrum.chromatographyType != 'GAS'}">
                            <label><form:checkbox path="massToleranceCheck"
                                                  onchange="$('#massTolerance').prop('disabled', !this.checked);"/>
                                Precursor Ion M/z Tolerance
                            </label><br/>
                            <form:input path="massTolerance"/><br/>
                            <form:errors path="massTolerance" cssClass="errors"/><br/>

                            <label><form:checkbox path="retTimeToleranceCheck"
                                                  onchange="$('#retTimeTolerance').prop('disabled', !this.checked);"/>
                                Retention Time Tolerance
                            </label><br/>
                            <form:input path="retTimeTolerance"/><br/>
                            <form:errors path="retTimeTolerance" cssClass="errors"/><br/>
                        </c:if>
                    </div>

                    <h3>Equipment Selector</h3>
                    <div>
                        <form:input path="tags"/><br/>
                        <form:errors path="tags" cssClass="errors"/><br/>
                    </div>
                </div>


                <div align="center">
                    <input type="submit" value="Search"/>
                </div>
            </form:form>
        </div>
    </div>
</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/tag-it-6ccd2de/js/tag-it.min.js"/>"></script>
<script>
    $(document).ready(function () {

        <%--@elvariable id="querySpectrumJson" type="java.lang.String"--%>
        console.log("spectrum", ${querySpectrumJson})


        $('#scoreThreshold').prop('disabled', !$('#scoreThresholdCheck1').prop('checked'));
        $('#mzTolerance').prop('disabled', !$('#scoreThresholdCheck1').prop('checked'));
        $('#massTolerance').prop('disabled', !$('#massToleranceCheck1').prop('checked'));
        $('#retTimeTolerance').prop('disabled', !$('#retTimeToleranceCheck1').prop('checked'));

        $('#accordion').accordion();
        $('#tags').tagit({
            autocomplete: {
                source: '${dulab:stringsToJson(searchForm.availableTags)}'
            }
        });
    });
</script>

<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script>
    var plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(querySpectrum)}'))
</script>
