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

<%--        <c:choose>--%>
<%--            <c:when test="${best_matches != null && best_matches.size() > 0}">--%>
                <table id="match_table" class="display responsive" style="width: 100%; clear:none;">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Compound from the Search List</th>
                        <th>Best Match</th>
                        <th>Score</th>
                        <th>P-Value</th>
                        <th>Diversity</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>


<%--            </c:when>--%>
<%--            <c:otherwise>--%>
<%--                <table id="match" class="display responsive" style="width: 100%; clear:none;">--%>
<%--                    <thead>--%>
<%--                    <tr>--%>
<%--                        <th>ID</th>--%>
<%--                        <th>Compound from the Search List</th>--%>
<%--                        <th>Best Match</th>--%>
<%--                        <th>Score</th>--%>
<%--                        <th>P-Value</th>--%>
<%--                        <th>Diversity</th>--%>
<%--                        <th>Search Button</th>--%>
<%--                    </tr>--%>
<%--                    </thead>--%>
<%--                    <tbody>--%>
<%--                    </tbody>--%>
<%--                </table>--%>
<%--                <p>There is no mass spectra to display.</p>--%>
<%--            </c:otherwise>--%>
<%--        </c:choose>--%>

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
            serverSide: true,
            processing: true,
            responsive: true,
            scrollX: true,
            scroller: true,
            ajax: {
                url: "${pageContext.request.contextPath}/file/group_search_results/data.json",

                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                }
            },
            "columnDefs": [
                {
                    "targets": 0,
                    "orderable": false,
                    "searchable": false,
                    "render": function (data, type, row, meta) {
                        return meta.settings.oAjaxData.start + meta.row + 1;
                    }
                },
                {
                    "targets": 1,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        content = row.querySpectrumName;
                        return content;
                    }
                },
                {
                    "targets": 2,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.matchSpectrumName != null) {
                            content = '<a href="${pageContext.request.contextPath}/cluster/' + row.matchSpectrumClusterId + '/">' +
                                row.matchSpectrumName +
                                '</a>';
                        }
                        return content;
                    }
                },
                {
                    "targets": 3,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        if (row.matchSpectrumName != null) {
                            return row.score.toFixed( 3 ) * 1000;
                        } else {
                            return row.score;
                        }

                    }
                },
                {
                    "targets": 4,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        if (row.matchSpectrumName != null) {
                            return row.minPValue.toFixed( 3 );
                        } else {
                            return row.minPValue;
                        }
                    }
                },
                {
                    "targets": 5,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        if (row.matchSpectrumName != null) {
                            return row.maxDiversity.toFixed( 3 );
                        } else {
                            return row.maxDiversity;
                        }
                    }
                },
                {
                    "targets": 6,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        var content = '<a href="${pageContext.request.contextPath}/file/'
                            + row.fileIndex + '/' + row.spectrumIndex + '/" class="button"> Search</a>';

                        return content;
                    }
                },
                {"className": "dt-center", "targets": "_all"}
            ]
        } );

        table.rows( ':eq(0)' ).select();


        $( '#scoreThreshold' ).prop( 'disabled', !$( '#scoreThresholdCheck1' ).prop( 'checked' ) );
        $( '#mzTolerance' ).prop( 'disabled', !$( '#scoreThresholdCheck1' ).prop( 'checked' ) );
        $( '#massTolerance' ).prop( 'disabled', !$( '#massToleranceCheck1' ).prop( 'checked' ) );
        $( '#retTimeTolerance' ).prop( 'disabled', !$( '#retTimeToleranceCheck1' ).prop( 'checked' ) );

        $( '#accordion' ).accordion();


        <%--$( '#tags' ).tagit( {--%>
        <%--    autocomplete: {--%>
        <%--        source: '${dulab:stringsToJson(searchForm.availableTags)}'--%>
        <%--    }--%>
        <%--} )--%>
    } );

    // $( ".tabbed-pane" ).each( function () {
    // $( this ).tabbedPane();
    // } );

</script>

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
                    <input id="demo" type="submit" value="Group Match" onclick="myFunction()"/>
                </div>

<%--                <script>--%>
<%--                    function myFunction(){--%>
<%--                        document.getElementById("demo").id = "match_table";--%>
<%--                    }--%>

<%--                </script>--%>
            </form:form>
        </div>
    </div>
</section>


