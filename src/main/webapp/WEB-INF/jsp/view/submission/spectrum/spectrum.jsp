<%--@elvariable id="spectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="container">

    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-end" role="toolbar">
                <a id="searchButton" href="<c:url value="search/"/>" type="button" class="btn btn-primary">Search</a>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-tabs">
                    <ul class="nav nav-tabs nav-fill nav-justified" role="tablist">
                        <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#properties">
                            Spectrum Properties</a></li>
                        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#peaks">Peaks</a></li>
                    </ul>
                </div>
                <div class="card-body tab-content">
                    <div id="properties" class="tab-pane active" role="tabpanel">
                        <div class="container-fluid small">
                            <div class="row row-content">
                                <div class="col-md-6">
                                    <h4>Standard Properties</h4>
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item py-1">
                                            <strong>Full Name:</strong>&nbsp;${spectrum.name}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Chromatography
                                                Type:</strong>&nbsp;${spectrum.chromatographyType.label}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>File:</strong>&nbsp;${(spectrum.file != null) ? spectrum.file.name : ''}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Submission:</strong>&nbsp;
                                            <c:if test="${spectrum.file != null && spectrum.file.submission != null}">
                                                <c:set var="submissionUrl">${pageContext.request.contextPath.concat('/submission/').concat(spectrum.file.submission.id).concat('/')}</c:set>
                                                <a href="${(spectrum.file.submission.id != 0) ? submissionUrl : '/file'}">
                                                        ${spectrum.file.submission.name}
                                                </a>
                                            </c:if>
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Cluster:</strong>&nbsp;
                                            <c:if test="${spectrum.cluster != null}">
                                                <a href="${pageContext.request.contextPath}/cluster/${spectrum.cluster.id}/">${spectrum.cluster}</a>
                                            </c:if>
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>External ID:</strong>&nbsp;${spectrum.externalId}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Formula:</strong>&nbsp;${spectrum.formula}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Precursor m/z:</strong>&nbsp;${spectrum.precursor}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Retention time:</strong>&nbsp;${spectrum.retentionTime}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Molecular mass:</strong>&nbsp;${spectrum.mass}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>Canonical SMILES:</strong>&nbsp;${spectrum.canonicalSmiles}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>InChi:</strong>&nbsp;${spectrum.inChi}
                                        </li>
                                        <li class="list-group-item py-1">
                                            <strong>InChiKey:</strong>&nbsp;${spectrum.inChiKey}
                                        </li>
                                    </ul>
                                </div>

                                <div class="col-md-6">
                                    <h4>Molecular Structure</h4>
                                    <div style = "text-align:center;">${dulab:smilesToImage(spectrum.canonicalSmiles)}</div>
                                    <h4>Other Properties</h4>
                                    <ul class="list-group list-group-flush">
                                        <c:forEach items="${spectrum.properties}" var="property">
                                            <li class="list-group-item py-1">
                                                <strong>${property.name}:</strong>&nbsp;
                                                <span style="word-break: break-all">${property.value}</span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="peaks" class="tab-pane" role="tabpanel">
                        <div class="container-fluid">
                            <div class="row">
                                <div class="col-md-6">
                                    <div id="plot" style="max-width: 100%; height: auto" class="plot"></div>
                                </div>

                                <div class="col-md-6">
                                    <table id="peak_table" class="display compact" style="width: 100%;">
                                        <thead>
                                        <tr>
                                            <th>M/z</th>
                                            <th>Intensity</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${dulab:peaksToJson(spectrum.peaks)}" var="peak">
                                            <tr>
                                                <td>
                                                    <fmt:formatNumber maxFractionDigits="4"
                                                                      groupingUsed="false">${peak[0]}</fmt:formatNumber>
                                                </td>
                                                <td>
                                                    <fmt:formatNumber maxFractionDigits="4"
                                                                      groupingUsed="false">${peak[1]}</fmt:formatNumber>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%--    <div class="tabbed-pane">--%>
    <%--        <span class="active" data-tab="spectrum">Spectrum Properties</span>--%>
    <%--        <span data-tab="peaks">Peaks</span>--%>
    <%--    </div>--%>

    <%--    <div id="spectrum" align="center">--%>
    <%--        <table id="property_table" class="display responsive" style="clear: none; max-width: 1000px;">--%>
    <%--            <thead>--%>
    <%--            <tr>--%>
    <%--                <th>Property</th>--%>
    <%--                <th>Value</th>--%>
    <%--            </tr>--%>
    <%--            </thead>--%>
    <%--            <tbody>--%>
    <%--            <tr>--%>
    <%--                <td><strong>Full Name:</strong></td>--%>
    <%--                <td>${spectrum.name}</td>--%>
    <%--            </tr>--%>
    <%--            <tr>--%>
    <%--                <td><strong>Chromatography:</strong></td>--%>
    <%--                <td>${spectrum.chromatographyType.label}</td>--%>
    <%--            </tr>--%>
    <%--            <c:if test="${spectrum.file != null}">--%>
    <%--                <tr>--%>
    <%--                    <td><strong>File:</strong></td>--%>
    <%--                    <td>${spectrum.file.name}</td>--%>
    <%--                </tr>--%>
    <%--            </c:if>--%>
    <%--            <c:if test="${spectrum.file!= null && spectrum.file.submission != null}">--%>
    <%--                <tr>--%>
    <%--                    <td><strong>Submission:</strong></td>--%>
    <%--                    <td><a href="${submissionUrl}">${spectrum.file.submission.name}</a></td>--%>
    <%--                </tr>--%>
    <%--            </c:if>--%>
    <%--            <c:if test="${spectrum.cluster != null}">--%>
    <%--                <tr>--%>
    <%--                    <td><strong>Cluster:</strong></td>--%>
    <%--                    <td><a href="/cluster/${spectrum.cluster.id}/">${spectrum.cluster}</a></td>--%>
    <%--                </tr>--%>
    <%--            </c:if>--%>
    <%--            <c:forEach items="${spectrum.properties}" var="property">--%>
    <%--                <tr>--%>
    <%--                    <td><strong>${property.name}:</strong></td>--%>
    <%--                    <td>${property.value}</td>--%>
    <%--                </tr>--%>
    <%--            </c:forEach>--%>
    <%--            </tbody>--%>
    <%--        </table>--%>
    <%--    </div>--%>

    <%--    <div id="peaks" align="center">--%>
    <%--        <div id="plot" style="" class="plot"></div>--%>
    <%--        <div style="display: inline-block; max-width: 100%; vertical-align: top;">--%>
    <%--            <table id="peak_table" class="display" style="width: 100%;">--%>
    <%--                <thead>--%>
    <%--                <tr>--%>
    <%--                    <th>M/z</th>--%>
    <%--                    <th>Intensity</th>--%>
    <%--                </tr>--%>
    <%--                </thead>--%>
    <%--                <tbody>--%>
    <%--                <c:forEach items="${dulab:peaksToJson(spectrum.peaks)}" var="peak">--%>
    <%--                    <tr>--%>
    <%--                        <td>--%>
    <%--                            <fmt:formatNumber maxFractionDigits="4" groupingUsed="false">${peak[0]}</fmt:formatNumber>--%>
    <%--                        </td>--%>
    <%--                        <td>--%>
    <%--                            <fmt:formatNumber maxFractionDigits="4" groupingUsed="false">${peak[1]}</fmt:formatNumber>--%>
    <%--                        </td>--%>
    <%--                    </tr>--%>
    <%--                </c:forEach>--%>
    <%--                </tbody>--%>
    <%--            </table>--%>
    <%--        </div>--%>
    <%--    </div>--%>
</div>
<%--<div align="center">--%>
<%--    <a href="search/" class="button">Search</a>--%>
<%--</div>--%>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>

<%--<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>--%>
<%--<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>--%>
<script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrum_plot.js"/>"></script>
<%--<script type="text/javascript" src="<c:url value="/resources/AdapCompoundDb/js/tabs.js"/>"></script>--%>
<script>

    $(".tabbed-pane").each(function () {
        $(this).tabbedPane();
    });
    $(document).ready(function () {
        $('#property_table').DataTable({
            info: false,
            ordering: false,
            paging: false,
            searching: false,
            responsive: true,
            scrollX: true,
            scroller: true
        });

        $('#peak_table').DataTable({
            responsive: true,
            scrollX: true,
            scroller: true,
            "fnInitComplete": function (oSettings, json) {
                $('#peaks').addClass("hide");
            }
        });

        SpectrumPlot('plot', ${dulab:spectrumToJson(spectrum)});
    })

    // Adjust column widths when a table becomes visible
    $(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
        $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
    });
</script>
<%--<style>--%>
<%--    .selection {--%>
<%--        fill: #ADD8E6;--%>
<%--        stroke: #ADD8E6;--%>
<%--        fill-opacity: 0.3;--%>
<%--        stroke-opacity: 0.7;--%>
<%--        stroke-width: 2;--%>
<%--        stroke-dasharray: 5, 5;--%>
<%--    }--%>
<%--</style>--%>