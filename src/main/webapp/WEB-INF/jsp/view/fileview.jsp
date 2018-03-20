<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h2>File content</h2>

    Number of spectra: ${fn:length(spectrumList)}<br/><br/>

    <div style="overflow: auto; height: 400px">
        <table>
            <tr>
                <th>Export</th>
                <th>Name</th>
            </tr>
            <c:forEach items="${spectrumList}" var="spectrum">
                <tr>
                    <td><input type="checkbox" name="export" value="${spectrum}" checked/></td>
                    <td>${spectrum.getProperty("Name").orElse("UNKNOWN")}</td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <div id="chartDiv"></div>

</section>

<script>
    var chartData = {
        type: 'bar',
        title: {text: 'My First Chart'},
        legend: {},
        series: [
            {values: [35, 42, 67, 89]},
            {values: [28, 40, 39, 36]}
        ]
    };
    zingchart.render({
        id: 'chartDiv',
        data: chartData,
        height: 400,
        width: 600
    });
</script>

<!-- End the middle column -->

<jsp:include page="/includes/column_right_news.jsp" />
<jsp:include page="/includes/footer.jsp" />