<%--@elvariable id="distribution" type="org.dulab.adapcompounddb.models.entities.TagDistribution"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<section>
    <script src="/resources/AdapCompoundDb/js/histogram.js"></script>
    <div class="tabbed-pane">
        <span class="active">Tag Distributions</span>
    </div>

        <script>
            var tagKey ="${distribution.tagKey}";
            var dataSet= '${distribution.tagDistribution}';
            addHistogram(tagKey,dataSet)
        </script>


</section>