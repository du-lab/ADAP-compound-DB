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
                <jsp:include page="/WEB-INF/jsp/shared/cluster_table.jsp">
                    <jsp:param name="ajax_url" value="${pageContext.request.contextPath}/rest/individual_search/json"/>
                </jsp:include>
<%--                &lt;%&ndash;                <div style="display: flex; justify-content: space-evenly">&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <div style="display: inline-block; vertical-align: middle" class="frame">&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        <jsp:include page="/WEB-INF/jsp/shared/filter.jsp">&ndash;%&gt;--%>
<%--                &lt;%&ndash;                            <jsp:param name="table_id" value="#match_table"/>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                            <jsp:param name="filterOptions" value="filterOptions"/>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        </jsp:include>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    </div>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <div style="display: inline-block; width: 500px" class="frame">&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        <jsp:include page="/WEB-INF/jsp/shared/column_visibility.jsp">&ndash;%&gt;--%>
<%--                &lt;%&ndash;                            <jsp:param name="table_id" value="#match_table"/>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        </jsp:include>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    </div>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                </div>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                <div>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    Click to hide/show columns:&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="0" checked/><strong>Match Spectrum</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="1" checked/><strong>Count</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="2" checked/><strong>Score</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="3" checked/><strong>In-study P-value</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="4" checked/><strong>Maximum Diversity</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="5" checked/><strong>Cross-study P-value</strong></label>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="6" class="checkboxHide"/><strong>Cross-study P-value&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        (disease)</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="7" class="checkboxHide"/><strong>Cross-study P-value&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        (species)</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="8" class="checkboxHide"/><strong>Cross-study P-value&ndash;%&gt;--%>
<%--                &lt;%&ndash;                        (sample source)</strong></label> -&ndash;%&gt;--%>
<%--                &lt;%&ndash;                    <label><input type="checkbox" data-column="9" checked/><strong>Type</strong></label>&ndash;%&gt;--%>
<%--                &lt;%&ndash;                </div>&ndash;%&gt;--%>

<%--                <table id="match_table" class="display responsive" style="width: 100%;">--%>
<%--                    <thead>--%>
<%--                    <tr>--%>
<%--                        <th>Id</th>--%>
<%--                        <th title="Match spectra">Match Spectrum</th>--%>
<%--                        <th title="Number of studies" class="Count">Count</th>--%>
<%--                        <th title="Minimum matching score between all spectra in a cluster">Score</th>--%>
<%--                        <th title="P-value of the In-study ANOVA test">In-study P-value</th>--%>
<%--                        <th title="Gini-Simpson Index">Maximum Diversity</th>--%>
<%--                        <th title="P-value of the Cross-study Goodness-of-fit test">Cross-study P-value</th>--%>
<%--                        <th title="P-value of disease">Cross-study P-value (disease)</th>--%>
<%--                        <th title="P-value of species">Cross-study P-value (species)</th>--%>
<%--                        <th title="P-value of sample source">Cross-study P-value (sample source)</th>--%>
<%--                        <th title="Chromatography type">Type</th>--%>
<%--                        <th></th>--%>
<%--                    </tr>--%>
<%--                    </thead>--%>
<%--                    <tbody>--%>
<%--                    </tbody>--%>
<%--                </table>--%>
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
    $(document).ready(function () {

        <%--let table = $('#match_table').DataTable({--%>
        <%--    // order: [[2, 'desc']],--%>
        <%--    select: {style: 'single'},--%>
        <%--    processing: true,--%>
        <%--    responsive: true,--%>
        <%--    scrollX: true,--%>
        <%--    scroller: true,--%>
        <%--    serverSide: true,--%>
        <%--    ajax: {--%>
        <%--        url: "${pageContext.request.contextPath}/rest/individual_search/json",--%>
        <%--        data: function (d) {--%>
        <%--            d.column = d.order[0].column;--%>
        <%--            d.sortDirection = d.order[0].dir;--%>
        <%--            d.search = d.search["value"];--%>
        <%--            // d.species = $('#species_filter');--%>
        <%--            // d.source = $('#source_filter');--%>
        <%--            // d.disease = $('#disease_filter');--%>
        <%--        }--%>
        <%--    },--%>
        <%--    "aoColumnDefs": [--%>
        <%--        {--%>
        <%--            "targets": 0,--%>
        <%--            "bSortable": false,--%>
        <%--            "searchable": false,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                return meta.settings.oAjaxData.start + meta.row + 1;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 1,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                content = '<a href="${pageContext.request.contextPath}/cluster/' + row.id + '/">' +--%>
        <%--                    row.consensusSpectrumName +--%>
        <%--                    '</a>';--%>
        <%--                return content--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 2,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "data": "size"--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 3,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                return row.diameter.toFixed(3) * 1000;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 4,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '';--%>
        <%--                if (row.aveSignificance != null) {--%>
        <%--                    var avgSignificance = row.aveSignificance.toFixed(3);--%>
        <%--                    content += '<span title="{Average: ' + row.aveSignificance;--%>
        <%--                    if (row.minSignificance) {--%>
        <%--                        content += '; Min: ' + row.minSignificance.toFixed(3);--%>
        <%--                    }--%>
        <%--                    if (row.maxSignificance) {--%>
        <%--                        content += '; Max: ' + row.maxSignificance.toFixed(3);--%>
        <%--                    }--%>
        <%--                    content += '}">' + avgSignificance + '</span>';--%>
        <%--                }--%>
        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 5,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '';--%>
        <%--                if (row.maxDiversity != undefined) {--%>
        <%--                    content = row.maxDiversity.toFixed(3);--%>
        <%--                }--%>
        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 6,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '';--%>
        <%--                if (row.minPValue) {--%>
        <%--                    content = row.minPValue.toFixed(3);--%>
        <%--                }--%>
        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 7,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": false,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '';--%>
        <%--                if (row.diseasePValue) {--%>
        <%--                    content = row.diseasePValue.toFixed(3);--%>
        <%--                }--%>
        <%--                return content;--%>
        <%--            },--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 8,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": false,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '';--%>
        <%--                if (row.speciesPValue) {--%>
        <%--                    content = row.speciesPValue.toFixed(3);--%>
        <%--                }--%>
        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 9,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": false,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '';--%>
        <%--                if (row.sampleSourcePValue) {--%>
        <%--                    content = row.sampleSourcePValue.toFixed(3);--%>
        <%--                }--%>
        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 10,--%>
        <%--            "bSortable": true,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '<img' +--%>
        <%--                    ' src="${pageContext.request.contextPath}/' + row.chromatographyTypeIconPath + '"'--%>
        <%--                    + ' alt="' + row.chromatographyTypeLabel + '"'--%>
        <%--                    + ' title="' + row.chromatographyTypeLabel + '"'--%>
        <%--                    + ' class="icon"/>';--%>

        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {--%>
        <%--            "targets": 11,--%>
        <%--            "bSortable": false,--%>
        <%--            "bVisible": true,--%>
        <%--            "render": function (data, type, row, meta) {--%>
        <%--                var content = '<a href="${pageContext.request.contextPath}/cluster/'--%>
        <%--                    + row.id + '/"><i class="material-icons" title="View">&#xE5D3;</i></a>';--%>
        <%--                return content;--%>
        <%--            }--%>
        <%--        },--%>
        <%--        {"className": "dt-center", "targets": "_all"}--%>
        <%--    ]--%>
        <%--});--%>

        // table.on('select', function (e, dt, type, indexes) {
        //     var row = table.row(indexes).node();
        //     var spectrum = JSON.parse($(row).attr('data-spectrum'));
        //     plot.update(spectrum);
        // });

        // table.rows(':eq(0)').select();


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
        // //checkbox control data column display
        // $("input:checkbox").click(function () {
        //
        //         // table
        //         var table = $('#match_table').dataTable();
        //
        //         // column
        //         var colNum = $(this).attr('data-column');
        //
        //         // Define
        //         // var bVis = table.fnSettings().aoColumns[colNum].bVisible;
        //         var bVis = $(this).prop('checked');
        //
        //         // Toggle
        //         table.fnSetColumnVis(colNum, bVis);
        //     }
        // );
        //
        // // initialize checkbox mark to unchecked for column not showing at the beginning
        // $("input:checkbox").ready(function () {
        //     $(".checkboxHide").prop("checked", false);
        //
        //
        //     //hiding column index from 6 to 8 initially
        //     for (i = 6; i < 9; i++) {
        //
        //         // table
        //         var table = $('#match_table').dataTable();
        //
        //         // Define
        //         if (table.fnSettings() != null) {
        //             var bVis = $(this).prop('checked');
        //         }
        //         // Toggle
        //         table.fnSetColumnVis(i, bVis);
        //     }
        // });
    })
    ;
</script>

<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/twospectraplot.js"/>"></script>
<script>
    var plot = new TwoSpectraPlot('plot', JSON.parse('${dulab:spectrumToJson(querySpectrum)}'))
</script>
