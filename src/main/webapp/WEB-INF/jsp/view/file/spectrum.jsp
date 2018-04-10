<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section class="transparent">
    <div align="right">
        <a href="<c:url value="${header.referer}"/>" class="button">Back to file</a>
    </div>
</section>

<section>
    <h1>Spectrum properties</h1>
    <div align="center">
        <table>
            <c:forEach var="e" items="${properties}">
                <tr>
                    <td>${e.name}</td>
                    <td>${e.value}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Spectrum peaks</h1>
    <div id="chartDiv" align="center"></div>
</section>

<section class="transparent">
    <div align="right">
        <a href="<c:url value="${header.referer}"/>" class="button">Back to file</a>
    </div>
</section>

<%--<script src="https://cdn.zingchart.com/zingchart.min.js"></script>--%>
<script src="<c:url value="/resources/js/zingchart/zingchart.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/spectrum.js"/>"></script>
<script>
    addPlot("chartDiv", '${jsonPeaks}');
</script>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp"/>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>