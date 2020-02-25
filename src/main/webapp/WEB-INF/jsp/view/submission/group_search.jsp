<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <div>
        <progress id="group_search_progress" value="0" max="100" style="width: 100%; height: 1.4em;"></progress>
    </div>
    <div class="tabbed-pane" style="text-align: center">
        <span data-tab="files">Group Search Results</span>
    </div>

    <div style="text-align: center">
        <table id="match_table" class="display responsive" style="width: 100%; clear:none;">
            <thead>
            <tr>
                <th>Id</th>
                <th>Query Spectrum</th>
                <th title="Match spectra">Match Spectrum</th>
                <th title="Number of studies" class="Count">Studies</th>
                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                <th title="Average P-value of ANOVA tests">Average P-value</th>
                <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
                <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
                <th title="Chromatography type">Type</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>

        <%--@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"--%>
        <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
        <form:form modelAttribute="filterForm" method="post">
            <div class="table-dropdown">
                <form:label path="species">Species:</form:label>
                <form:select path="species">
                    <form:option value="all">All</form:option>
                    <form:options items="${filterOptions.speciesList}"/>
                </form:select>
            </div>
            <div class="table-dropdown">
                <form:label path="source">Species:</form:label>
                <form:select path="source">
                    <form:option value="all">All</form:option>
                    <form:options items="${filterOptions.sourceList}"/>
                </form:select>
            </div>
            <div class="table-dropdown">
                <form:label path="disease">Species:</form:label>
                <form:select path="disease">
                    <form:option value="all">All</form:option>
                    <form:options items="${filterOptions.diseaseList}"/>
                </form:select>
            </div>
        </form:form>
    </div>
</section>

<%--<div style="text-align: center">--%>
<%--    <a class="button" onclick="$('#filterForm').submit()">Search</a>--%>
<%--</div>--%>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/tag-it-6ccd2de/js/tag-it.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/groupSearchProgressBar.js"/>"></script>

<script>
    $(document).ready(function () {

        let table = $('#match_table').DataTable({
            dom: 'l<"#filter">frtip',
            serverSide: true,
            processing: true,
            responsive: true,
            scrollX: true,
            // scroller: true,
            ajax: {
                url: "${pageContext.request.contextPath}/file/group_search/data.json",

                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                }
            },
            "aoColumnDefs": [
                {
                    "targets": 0,
                    "bSortable": false,
                    "searchable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return meta.settings.oAjaxData.start + meta.row + 1;
                    }
                },
                {
                    "targets": 1,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return row.querySpectrumName;
                    }
                },
                {
                    "targets": 2,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        let content = '';
                        if (row.consensusSpectrumName != null) {
                            content = '<a href="${pageContext.request.contextPath}/cluster/'
                                + row.clusterId + '/">' + row.consensusSpectrumName + '</a>';
                        }
                        return content;
                    }
                },
                {
                    "targets": 3,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return (row.size != null) ? row.size : '';
                    }
                },
                {
                    "targets": 4,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return (row.score != null) ? row.score.toFixed(3) * 1000 : '';
                    }
                },
                {
                    "targets": 5,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.aveSignificance != null) {
                            return row.aveSignificance.toFixed(3);
                        } else {
                            return '';
                        }
                    }
                },
                {
                    "targets": 6,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.minSignificance != null) {
                            return row.minSignificance.toFixed(3);
                        } else {
                            return '';
                        }
                    }
                },
                {
                    "targets": 7,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        if (row.maxSignificance != null) {
                            return row.maxSignificance.toFixed(3);
                        } else {
                            return '';
                        }
                    }
                },
                {
                    "targets": 8,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        let content = '';
                        if (row.consensusSpectrumName != null) {
                            content = '<img' +
                                ' src="${pageContext.request.contextPath}/' + row.chromatographyTypePath + '"'
                                + ' alt="' + row.chromatographyTypeLabel + '"'
                                + ' title="' + row.chromatographyTypeLabel + '"'
                                + ' class="icon"/>';
                        }
                        return content;
                    }
                },
                {
                    "targets": 9,
                    "bSortable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        let content = '';
                        if (row.querySpectrumId != 0) {
                            content = '<a href="${pageContext.request.contextPath}/spectrum/'
                                + row.querySpectrumId + '/search/" class="button"> Search</a>';
                        } else {
                            content = '<a href="${pageContext.request.contextPath}/file/'
                                + row.fileIndex + '/' + row.spectrumIndex + '/search/" class="button"> Search</a>';
                        }
                        return content;
                    }
                },
                // {"className": "dt-center", "targets": "_all"}
            ]
        });

        // $('#scoreThreshold').prop('disabled', !$('#scoreThresholdCheck1').prop('checked'));
        // $('#mzTolerance').prop('disabled', !$('#scoreThresholdCheck1').prop('checked'));
        // $('#massTolerance').prop('disabled', !$('#massToleranceCheck1').prop('checked'));
        // $('#retTimeTolerance').prop('disabled', !$('#retTimeToleranceCheck1').prop('checked'));
        // $('#accordion').accordion();

        // refresh the datatable every 1 second
        setInterval(function () {
            table.ajax.reload(null, false);
        }, 1000);

        let filterForm = $('#filterForm');
        filterForm.appendTo('#filter');

        $('#species, #source, #disease').change(function () {
            filterForm.submit();
        });


        <c:if test="${pageContext.request.method == 'GET'}">filterForm.submit();</c:if>

        new GroupSearchProgressBar(window.location.href + 'progress', 'group_search_progress', 1000).start();

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
        //         var bVis = $(this).prop('checked');
        //
        //         // Toggle
        //         table.fnSetColumnVis(colNum, bVis);
        //     }
        // );
        //
        // // initialize checkbox mark to unchecked for column not showing at the beginning
        // $("input:checkbox").ready(function () {
        //     $("input:checkbox").each(function () {
        //         var table = $('#match_table').dataTable();
        //         var colNum = $(this).attr('data-column');
        //         if (colNum != null) {
        //             var bVis = table.fnSettings().aoColumns[colNum].bVisible;
        //             $(this).prop("checked", bVis);
        //         }
        //     })
        // });


    });
    // let groupSearchProgressBar = new groupSearchProgressBar('progress', 'group_search_progress', 1000);
    // groupSearchProgressBar.start();
</script>

<%--<section>--%>
<%--    <h1>Query Parameters</h1>--%>
<%--    <div align="center">--%>
<%--        <div align="left" class="subsection">--%>
<%--            <p class="errors">${searchResultMessage}</p>--%>
<%--            <c:if test="${validationErrors != null}">--%>
<%--                <div class="errors">--%>
<%--                    <ul>--%>
<%--                        <c:forEach items="${validationErrors}" var="error">--%>
<%--                            <li><c:out value="${error.message}"/></li>--%>
<%--                        </c:forEach>--%>
<%--                    </ul>--%>
<%--                </div>--%>
<%--            </c:if>--%>
<%--            <form:form method="post" modelAttribute="searchForm">--%>
<%--                <form:errors path="" cssClass="errors"/>--%>
<%--                <div id="accordion">--%>
<%--                    <h3>Search Parameters</h3>--%>
<%--                    <div>--%>
<%--                        <label>--%>
<%--                            <form:checkbox path="scoreThresholdCheck" onchange="--%>
<%--                                    $('#scoreThreshold').prop('disabled', !this.checked);--%>
<%--                                    $('#mzTolerance').prop('disabled', !this.checked);"/>--%>
<%--                            Spectral Similarity--%>
<%--                        </label><br/>--%>
<%--                        <form:label path="scoreThreshold">Matching Score Threshold</form:label><br/>--%>
<%--                        <form:input path="scoreThreshold"/><br/>--%>
<%--                        <form:errors path="scoreThreshold" cssClass="errors"/><br/>--%>

<%--                        <form:label path="mzTolerance">Product Ion M/z tolerance</form:label><br/>--%>
<%--                        <form:input path="mzTolerance"/><br/>--%>
<%--                        <form:errors path="mzTolerance" cssClass="errors"/><br/>--%>

<%--                            &lt;%&ndash;                        <c:if test="${querySpectrum.chromatographyType != 'GAS'}">&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            <label><form:checkbox path="massToleranceCheck"&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                                                  onchange="$('#massTolerance').prop('disabled', !this.checked);"/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                                Precursor Ion M/z Tolerance&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            </label><br/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            <form:input path="massTolerance"/><br/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            <form:errors path="massTolerance" cssClass="errors"/><br/>&ndash;%&gt;--%>

<%--                            &lt;%&ndash;                            <label><form:checkbox path="retTimeToleranceCheck"&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                                                  onchange="$('#retTimeTolerance').prop('disabled', !this.checked);"/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                                Retention Time Tolerance&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            </label><br/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            <form:input path="retTimeTolerance"/><br/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                            <form:errors path="retTimeTolerance" cssClass="errors"/><br/>&ndash;%&gt;--%>
<%--                            &lt;%&ndash;                        </c:if>&ndash;%&gt;--%>
<%--                    </div>--%>
<%--                    <h3>Equipment Selector</h3>--%>
<%--                    <div>--%>
<%--                        <form:input path="tags"/><br/>--%>
<%--                        <form:errors path="tags" cssClass="errors"/><br/>--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--                <div align="center">--%>
<%--                    <input id="demo" type="submit" value="Group Match"/>--%>
<%--                </div>--%>
<%--            </form:form>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</section>--%>


