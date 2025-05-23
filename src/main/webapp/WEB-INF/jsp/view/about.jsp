<%--@elvariable id="distributions" type="org.dulab.adapcompounddb.models.entities.TagDistribution"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card" style="margin-bottom: 10px;">
                <div class="card-header card-header-single">
                    Version
                </div>

                <div class="card-body">
                    ${appVersion}: Added versioning
                </div>
            </div>
            <div class="card" style="margin-bottom: 10px;">
                <div class="card-header card-header-single">
                    ADAP-KDB Documentation
                </div>

                <div class="card-body">

                    <ul class="list-group">
                        <li class="list-group-item">
                            <div class="media">
                                <div class="mr-3">
                                    <div class="badge badge-primary">PAPER</div>
                                </div>
                                <div class="media-body">
                                    <a href="https://pubs.acs.org/doi/10.1021/acs.analchem.1c00355" target="_blank">
                                        ADAP-KDB: A Spectral Knowledgebase for Tracking and Prioritizing Unknown GC–MS Spectra in
                                        the NIH’s Metabolomics Data Repository</a>,
                                    Aleksandr Smirnov, Yunfei Liao, Eoin Fahy, Shankar Subramaniam, and Xiuxia Du,
                                    <em>Analytical Chemistry</em> 2021 93 (36), 12213-12220
                                </div>
                            </div>
                        </li>

                        <li class="list-group-item">
                            <div class="media">
                                <div class="mr-3">
                                    <div class="badge badge-primary">TUTORIAL</div>
                                </div>
                                <div class="media-body">
                                    <a href="<c:url value="/resources/AdapCompoundDb/misc/golm_tutorial.docx"/>">
                                        Matching to a custom/inhouse/public library Short Tutorial</a>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="card">
                <div class="card-header card-header-single">
                    Tag Distributions
                </div>
                <div class="card-body">
                    <script src="<c:url value="/resources/d3/d3.min.js"/>"></script>
                    <script src="${pageContext.request.contextPath}/resources/AdapCompoundDb/js/histogram.js"></script>
                    <c:forEach items="${distributions}" var="distribution" varStatus="status">
                        <div id="div${status.index}" style="display: inline-block; margin: 10px;text-align: left;"
                             class="font-weight-lighter">
                            <script>
                                addHistogram('div' +${status.index}, '${distribution.label}', '${distribution.distribution}');
                            </script>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>