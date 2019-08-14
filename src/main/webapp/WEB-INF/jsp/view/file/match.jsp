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
        <c:choose>
            <c:when test="${matches != null && matches.size() > 0}">
                <div>
                    Click to hide/show columns:
                    <label><input type="checkbox" data-column="0" checked/><strong>Match Spectrum</strong></label> -
                    <label><input type="checkbox" data-column="1" checked/><strong>Count</strong></label> -
                    <label><input type="checkbox" data-column="2" checked/><strong>Score</strong></label> -
                    <label><input type="checkbox" data-column="3" checked/><strong>In-study P-value</strong></label> -
                    <label><input type="checkbox" data-column="4" checked/><strong>Maximum Diversity</strong></label> -
                    <label><input type="checkbox" data-column="5" checked/><strong>Cross-study P-value</strong></label> -
                    <label><input type="checkbox" data-column="6" class="checkboxHide"/><strong>Cross-study P-value (disease)</strong></label> -
                    <label><input type="checkbox" data-column="7" class="checkboxHide"/><strong>Cross-study P-value (species)</strong></label> -
                    <label><input type="checkbox" data-column="8" class="checkboxHide"/><strong>Cross-study P-value (sample source)</strong></label> -
                    <label><input type="checkbox" data-column="9" checked/><strong>Type</strong></label>
                </div>

                <table id="match_table" class="display responsive" style="max-width: 100%;">
                    <thead>
                    <tr>
                        <th title="Match spectra">Match Spectrum</th>
                        <th title="Number of studies">Count</th>
                        <th title="Minimum matching score between all spectra in a cluster">Score</th>
                        <th title="P-value of the In-study ANOVA test">In-study P-value</th>
                        <th title="Gini-Simpson Index">Maximum Diversity</th>
                        <th title="P-value of the Cross-study Goodness-of-fit test">Cross-study P-value</th>
                        <th title="P-value of disease">Cross-study P-value (disease)</th>
                        <th title="P-value of species">Cross-study P-value (species)</th>
                        <th title="P-value of sample source">Cross-study P-value (sample source)</th>
                        <th title="Chromatography type">Type</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${matches}" var="match" varStatus="status">
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.score * 1000}"
                                          var="score"/>
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.matchSpectrum.cluster.aveSignificance}"
                                          var="aveSignificance"/>
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.matchSpectrum.cluster.maxDiversity}"
                                          var="maxDiversity"/>
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.matchSpectrum.cluster.minPValue}"
                                          var="minPValue"/>
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.matchSpectrum.cluster.diseasePValue}"
                                          var="diseasePValue"/>
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.matchSpectrum.cluster.speciesPValue}"
                                          var="speciesPValue"/>
                        <fmt:formatNumber type="number"
                                          maxFractionDigits="0"
                                          groupingUsed="false"
                                          value="${match.matchSpectrum.cluster.sampleSourcePValue}"
                                          var="sampleSourcePValue"/>

                        <tr data-spectrum='${dulab:spectrumToJson(match.matchSpectrum)}'>
                            <c:if test="${match.matchSpectrum.consensus}">
                                <td>
                                    <a href="/cluster/${match.matchSpectrum.cluster.id}/">
                                            ${dulab:abbreviate(match.matchSpectrum.name, 80)}</a><br/>
                                    <small>
                                        <c:if test="${match.matchSpectrum.precursor != null}">Precursor:
                                            ${match.matchSpectrum.precursor};
                                        </c:if>
                                        <c:if test="${match.matchSpectrum.retentionTime != null}">Ret Time:
                                            ${match.matchSpectrum.retentionTime};
                                        </c:if>
                                    </small>
                                </td>
                                <td>${match.matchSpectrum.cluster.size}</td>
                                <td>${score}</td>
                                <td>${aveSignificance}</td>
                                <td>${maxDiversity}</td>
                                <td>${minPValue}</td>
                                <td>${diseasePValue}</td>
                                <td>${speciesPValue}</td>
                                <td>${sampleSourcePValue}</td>
                                <td><img
                                        src="${pageContext.request.contextPath}/${match.matchSpectrum.chromatographyType.iconPath}"
                                        alt="${match.matchSpectrum.chromatographyType.label}"
                                        title="${match.matchSpectrum.chromatographyType.label}" class="icon"/></td>
                                <td><a href="/spectrum/${match.matchSpectrum.id}/"><i
                                        class="material-icons">&#xE5D3;</i></a></td>
                            </c:if>
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
    $( document ).ready( function () {

        var table = $( '#match_table' ).DataTable( {
            order: [[0, 'desc']],
            select: {style: 'single'},
            responsive: true,
            scrollX: true,
            scroller: true
        } );

        table.on( 'select', function (e, dt, type, indexes) {
            var row = table.row( indexes ).node();
            var spectrum = JSON.parse( $( row ).attr( 'data-spectrum' ) );
            plot.update( spectrum );
        } );

        table.rows( ':eq(0)' ).select();

        $( '#scoreThreshold' ).prop( 'disabled', !$( '#scoreThresholdCheck1' ).prop( 'checked' ) );
        $( '#mzTolerance' ).prop( 'disabled', !$( '#scoreThresholdCheck1' ).prop( 'checked' ) );
        $( '#massTolerance' ).prop( 'disabled', !$( '#massToleranceCheck1' ).prop( 'checked' ) );
        $( '#retTimeTolerance' ).prop( 'disabled', !$( '#retTimeToleranceCheck1' ).prop( 'checked' ) );

        $( '#accordion' ).accordion();
        $( '#tags' ).tagit( {
            autocomplete: {
                source: '${dulab:stringsToJson(searchForm.availableTags)}'
            }
        } )

        $( "input:checkbox" ).click( function () {

                // table
                var table = $( '#match_table' ).dataTable();

                // column
                var colNum = $( this ).attr( 'data-column' );

                // Define
                var bVis = table.fnSettings().aoColumns[colNum].bVisible;

                // Toggle
                table.fnSetColumnVis( colNum, bVis ? false : true );
            }
        );

        $( "input:checkbox" ).ready( function () {
            var table = $( '#match_table' ).dataTable();

            $(".checkboxHide").prop("checked",false);

            for (i = 6; i < 9; i++) {

                // Define
                if(table.fnSettings() != null) {
                    var bVis = table.fnSettings().aoColumns[i].bVisible;
                }
                // Toggle
                table.fnSetColumnVis( i, bVis ? false : true );
            }
        } )

    } );
</script>

<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script>
    var plot = new TwoSpectraPlot( 'plot', JSON.parse( '${dulab:spectrumToJson(querySpectrum)}' ) )
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