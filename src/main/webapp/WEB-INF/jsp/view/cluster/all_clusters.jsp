<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Clusters</h1>
    <div align="center">
        <table id="cluster_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>ID</th>
                <th title="Consensus spectrum">Consensus</th>
                <th title="Number of spectra in a cluster">Count</th>
                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                <th title="Average, minimum, and maximum values of the statistical significance">Significance</th>
                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <th>${type.label} Diversity</th>
                </c:forEach>
                <th title="Chromatography type">Type</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${clusters}" var="cluster">
                <tr>
                    <td>${cluster.id}</td>
                    <td><a href="${pageContext.request.contextPath}/cluster/${cluster.id}/">${cluster.consensusSpectrum.name}</a></td>
                    <td>${cluster.size}</td>
                    <td>${dulab:toIntegerScore(cluster.diameter)}</td>
                    <td title="Ave: ${cluster.aveSignificance}; Min: ${cluster.minSignificance}; Max: ${cluster.maxSignificance}">
                        <c:if test="${cluster.aveSignificance != null}">
                            <fmt:formatNumber type="number" maxFractionDigits="2"
                                              value="${cluster.aveSignificance}"/><br/>
                        </c:if>
                    </td>

                    <c:forEach items="${submissionCategoryTypes}" var="type">
                        <td>
                            <c:forEach items="${cluster.diversityIndices}" var="diversityIndex">
                                <c:if test="${diversityIndex.id.categoryType == type}">
                                    <fmt:formatNumber type="number" maxFractionDigits="3"
                                                      value="${diversityIndex.diversity}"/>
                                </c:if>
                            </c:forEach>
                        </td>
                    </c:forEach>

                    <td><img src="${pageContext.request.contextPath}/${cluster.consensusSpectrum.chromatographyType.iconPath}"
                             alt="${cluster.consensusSpectrum.chromatographyType.name()}"
                             title="${cluster.consensusSpectrum.chromatographyType.label}"
                             class="icon"/></td>
                    <td>
                        <!--more horiz-->
                        <a href="${pageContext.request.contextPath}/cluster/${cluster.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#cluster_table').DataTable();
    });
</script>