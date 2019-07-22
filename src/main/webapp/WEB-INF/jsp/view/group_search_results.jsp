<%--@elvariable id="querySpectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%--@elvariable id="matches" type="java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumMatch>"--%>
<%--@elvariable id="submissionCategoryTypes" type="org.dulab.adapcompounddb.models.SubmissionCategoryType[]"--%>
<%--@elvariable id="submissionCategoryMap" type="java.util.Map<org.dulab.adapcompounddb.models.SubmissionCategoryType, org.dulab.adapcompounddb.models.entities.SubmissionCategory>"--%>
<%--@elvariable id="searchForm" type="org.dulab.adapcompounddb.site.controllers.SearchController.SearchForm"--%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<section>
    <div class="tabbed-pane" style="text-align: center">
        <span data-tab="files">Group Search Results</span>
    </div>
    <div align="center">
        <c:choose>
            <c:when test="${best_matches != null && best_matches.size() > 0}">
                <table id="match_table" class="display responsive" style="width: 100%; clear:none;">
                    <thead>
                    <tr>
                        <th>Compound from the Search List</th>
                        <th>Best Match</th>
                        <th>Score</th>
                        <th>P-Value</th>
                        <th>Diversity</th>
                        <th>Search Button</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${best_matches}" var="match" varStatus="theCount">
                    <tr data-spectrum='${dulab:spectrumToJson(match.querySpectrum)}'>
                        <td style="text-align:center">
                                ${dulab:abbreviate(match.querySpectrum.name, 80)}<br/>
                            <small>
                                <c:if test="${match.querySpectrum.precursor != null}">Precursor: ${match.querySpectrum.precursor};</c:if>
                                <c:if test="${match.querySpectrum.retentionTime != null}">Ret Time: ${match.querySpectrum.retentionTime};</c:if>
                            </small>
                        </td>

                        <c:choose>
                        <c:when test="${match.matchSpectrum != null}">

                            <fmt:formatNumber type="number"
                                              maxFractionDigits="0"
                                              groupingUsed="false"
                                              value="${match.score * 1000}"
                                              var="score"/>

                            <fmt:formatNumber type="number"
                                              maxFractionDigits="3"
                                              groupingUsed="false"
                                              value="${match.matchSpectrum.cluster.minPValue}"
                                              var="minPValue"/>

                        <td style="text-align:center">
                            <a href="/cluster/${match.matchSpectrum.cluster.id}/">${dulab:abbreviate(match.matchSpectrum.name, 80)}</a>
                            <small>
                                <c:if test="${match.matchSpectrum.precursor != null}">Precursor: ${match.matchSpectrum.precursor};</c:if>
                                <c:if test="${match.matchSpectrum.retentionTime != null}">Ret Time: ${match.matchSpectrum.retentionTime};</c:if>
                            </small>
                        </td>
                        <td style="text-align:center">${score}</td>
                        <td style="text-align:center">${minPValue}</td>
                        <td style="text-align:center">${match.matchSpectrum.cluster.maxDiversity}</td>
                        </c:when>

                        <c:otherwise>
                        <td style="text-align:center"></td>
                        <td style="text-align:center"></td>
                        <td style="text-align:center"></td>
                        <td style="text-align:center"></td>
                        </c:otherwise>
                        </c:choose>
                        <td style="text-align:center"><a href="/spectrum/${match.querySpectrum.id}/search" class="button">Search</a></td>
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

                            <%--                        <c:if test="${querySpectrum.chromatographyType != 'GAS'}">--%>
                            <%--                            <label><form:checkbox path="massToleranceCheck"--%>
                            <%--                                                  onchange="$('#massTolerance').prop('disabled', !this.checked);"/>--%>
                            <%--                                Precursor Ion M/z Tolerance--%>
                            <%--                            </label><br/>--%>
                            <%--                            <form:input path="massTolerance"/><br/>--%>
                            <%--                            <form:errors path="massTolerance" cssClass="errors"/><br/>--%>

                            <%--                            <label><form:checkbox path="retTimeToleranceCheck"--%>
                            <%--                                                  onchange="$('#retTimeTolerance').prop('disabled', !this.checked);"/>--%>
                            <%--                                Retention Time Tolerance--%>
                            <%--                            </label><br/>--%>
                            <%--                            <form:input path="retTimeTolerance"/><br/>--%>
                            <%--                            <form:errors path="retTimeTolerance" cssClass="errors"/><br/>--%>
                            <%--                        </c:if>--%>
                    </div>

                    <h3>Equipment Selector</h3>
                    <div>
                        <form:input path="tags"/><br/>
                        <form:errors path="tags" cssClass="errors"/><br/>
                    </div>
                </div>


                <div align="center">
                    <input type="submit" value="Group Match"/>
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

            select: {style: 'single'},
            responsive: true,
            scrollX: true,
            scroller: true,
            order: [[0, 'desc']]

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
    } );

    $( ".tabbed-pane" ).each( function () {
        $( this ).tabbedPane();
    } );

</script>
