<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<head>
    <meta name="decorator" id="decorator" content="no_decorator" />
    <script type="text/javascript"
        src="<c:url value="/resources/jQuery-3.2.1/jquery.blockUI.js"/>"></script>
</head>

<table id="match_table" class="display responsive" style="width: 100%;">
    <thead>
        <tr>
            <th>Score</th>
            <th>Spectrum</th>
            <th>Matched Spectrum</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<script>

$("#match_table").DataTable({
    serverSide: true,
    processing: true,
    scrollX: true,
    scroller: true,
    searching: false,
    ajax: {
        url: "${pageContext.request.contextPath}/search-spectra-data",
        data: function (data) {
            data.submissionId = '${submissionId}';
            data.isScoreThreshold = $("#scoreThresholdCheck1").prop('checked');
            data.scoreThreshold = $("#scoreThreshold").val();
            data.mzTolerance = $("#mzTolerance").val();
            data.isMassTolerance = $("#massToleranceCheck1").prop('checked');
            data.massTolerance = $("#massTolerance").val();
            data.isRetTimeTolerance = $("#retTimeToleranceCheck1").prop('checked');
            data.retTimeTolerance = $("#retTimeTolerance").val();
            data.tags = $('#tags').val();
        }
    },
    "columnDefs": [
        {
            "targets": 0,
            "orderable": false,
            "data": "score"
        },
        {
            "targets": 1,
            "orderable": false,
            "data": "querySpectrumName"
        },
        {
            "targets": 2,
            "orderable": false,
            "data": "matchSpectrumName"
        }
    ]
});

</script>