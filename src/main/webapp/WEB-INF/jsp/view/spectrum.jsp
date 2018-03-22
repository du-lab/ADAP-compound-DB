<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section>
    <a href="<c:url value="/library/submission"/>">Back to submission</a><br/><br/>
    <h1>Spectrum</h1>
    <h2>${name}</h2>
    <br/>
    <h2>Properties</h2>
    <table>
        <tr>
            <th>Name</th>
            <th>Value</th>
        </tr>
        <c:forEach var="e" items="${properties}">
            <tr>
                <td>${e.key}</td>
                <td>${e.value}</td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <div id="chartDiv"></div>
    <br/>
    <a href="<c:url value="/library/submission"/>">Back to submission</a>
</section>

<script>
    var chartData = {
        type: 'bar',
        title: {text: 'Spectrum Peaks'},
        scaleX: {
            label: {text: 'M/z'},
            zooming: true
        },
        scaleY: {
            values: '0:100:20'
            // format: '%v%'
        },
        series: [
            {values: JSON.parse('${jsonPeaks}')}
        ],
        plot: {
            tooltip: {
                text: 'm/z: %k<br>int: %v',
                decimals: 4,
                fontColor: 'black',
                backgroundColor: 'white',
                borderWidth: 1,
                borderColor: 'grey'
            }
        }
    };
    zingchart.render({
        id: 'chartDiv',
        data: chartData,
        height: 400,
        width: 600
    });
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>