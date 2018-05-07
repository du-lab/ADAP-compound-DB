<%--@elvariable id="numSpectra" type="java.lang.Long"--%>
<%--@elvariable id="numClusters" type="java.lang.Long"--%>
<%--@elvariable id="clusters" type="java.util.List<org.dulab.models.entities.SpectrumCluster>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Library Information</h1>
    <table>
        <tr>
            <td>Number of submitted spectra</td>
            <td>${numSpectra}</td>
        </tr>
        <tr>
            <td>Number of consensus spectra</td>
            <td>${numClusters}</td>
        </tr>
    </table>
</section>

<section>
    <h1>Admin Tools</h1>
    <table>
        <tr>
            <td><a href="calculatescores/" class="button">Calculate Matching Scores...</a></td>
            <td>Calculates matching scores for all spectra in the library</td>
        </tr>
        <tr>
            <td><a href="cluster/" class="button">Cluster spectra...</a></td>
            <td>Cluster spectra into clusters</td>
        </tr>
    </table>
</section>

<section>
    <h1>Clusters</h1>
    <table>
        <tr>
            <th>Name</th>
            <th>Num Spectra</th>
            <th>Matching Score</th>
            <th></th>
        </tr>
        <c:forEach items="${clusters}" var="cluster">
            <tr>
                <td>${cluster}</td>
                <td>${cluster.size}</td>
                <td>${dulab:toIntegerScore(cluster.diameter)}</td>
                <td>
                    <!--more horiz-->
                    <a href="/cluster/${cluster.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                </td>
            </tr>
        </c:forEach>
    </table>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />