<%--@elvariable id="distributions" type="org.dulab.adapcompounddb.models.entities.TagDistribution"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<section>

    <script src="/resources/AdapCompoundDb/js/histogram.js"></script>
    <div class="tabbed-pane" align="center">
        <span class="active">Tag Distributions</span>
    </div>

    <c:forEach items="${distributions}" var="distribution" varStatus="status">
    <div id="div${status.index}" style="display: inline-block; margin: 10px;text-align: left;">
        <script>
            var tagKey ='${distribution.tagKey}';
            var dataSet= '${distribution.tagDistribution}';
            addHistogram('div'+${status.index},tagKey,dataSet);
        </script>
    </div>
    </c:forEach>

</section>