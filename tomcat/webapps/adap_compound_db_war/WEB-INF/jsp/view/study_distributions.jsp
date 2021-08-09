<%--@elvariable id="distributions" type="org.dulab.adapcompounddb.models.entities.TagDistribution"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="row">
    <div class="col">
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