<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container">
    <%--@elvariable id="filterForm" type="org.dulab.adapcompounddb.site.controllers.forms.FilterForm"--%>
    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
    <form:form modelAttribute="filterForm" method="post">
        <div class="row row-content">
            <div class="col">
                <div class="form-row">
                    <div class="col">
                        <div class="btn-toolbar justify-content-end" role="toolbar">
                            <button id="searchButton" class="btn btn-primary" type="submit">Search</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row row-content">
            <div class="col">
                <div class="card">
                    <div class="card-header card-header-tabs">
                        <ul class="nav nav-tabs nav-fill nav-justified" role="tablist">
                            <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#libraries">
                                Libraries
                            </a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#filter">
                                Filter
                            </a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#parameters">
                                Parameters
                                <span id="custom" class="badge badge-info" style="display: none">
                                    Custom Parameters
                                </span>
                            </a></li>
                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#email">
                                Email
                            </a></li>
                        </ul>
                    </div>

                    <div class="card-body tab-content">
                        <div id="libraries" class="tab-pane fade show active" role="tabpanel">
                            <div class="row">
                                <div class="col-md-8 offset-md-2">
                                    <p>Select the libraries to search against.</p>
                                    <p>All available public libraries and user's private libraries are listed here.</p>
                                </div>
                            </div>
                            <fieldset class="form-group row">
                                <form:label path="submissionIds"
                                            cssClass="col-md-4 col-form-label">Search in Libraries:</form:label>
                                <div class="col-md-8">
                                    <c:forEach items="${filterOptions.submissions}" var="submission" varStatus="status">
                                        <div class="custom-control custom-switch">
                                            <input class="custom-control-input" type="checkbox" name="submissionIds"
                                                   id="submissionIds${status.index}" value="${submission.key}"
                                                   <c:if test="${filterForm.submissionIds.contains(submission.key)}">checked</c:if>>
                                            <label class="custom-control-label"
                                                   for="submissionIds${status.index}">${submission.value}</label>
                                        </div>
                                    </c:forEach>
                                </div>
                                <input type="hidden" name="_submissionIds" value="on">
                            </fieldset>
                        </div>

                        <div id="filter" class="tab-pane fade" role="tabpanel">
                            <div class="row form-group">
                                <form:label path="species" cssClass="col-md-4 col-form-label">Species:</form:label>
                                <div class="col-md-8">
                                    <form:select path="species" cssClass="form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.speciesList}"/>
                                    </form:select>
                                </div>
                            </div>

                            <div class="row form-group">
                                <form:label path="source" cssClass="col-md-4 col-form-label">Source:</form:label>
                                <div class="col-md-8">
                                    <form:select path="source" cssClass="form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.sourceList}"/>
                                    </form:select>
                                </div>
                            </div>

                            <div class="row form-group">
                                <form:label path="disease" cssClass="col-md-4 col-form-label">Disease:</form:label>
                                <div class="col-md-8">
                                    <form:select path="disease" cssClass="form-control">
                                        <form:option value="all">All</form:option>
                                        <form:options items="${filterOptions.diseaseList}"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div id="parameters" class="tab-pane fade" role="tabpanel">
                            <p class="errors" style="color: black;">You can change the default value of each parameter on the
                                <a style="color:black;text-decoration: underline;" href="<c:url value="/account/"/>">Account</a> page.
                            </p>
                            <jsp:include page="../../view/compound/user_search_parameters.jsp">
                                <jsp:param name="SCORE_THRESHOLD" value="${searchParameters.scoreThreshold}"/>
                                <jsp:param name="RETENTION_INDEX_TOLERANCE" value="${searchParameters.retentionIndexTolerance}"/>
                                <jsp:param name="RETENTION_INDEX_MATCH" value="${searchParameters.retentionIndexMatch}"/>
                                <jsp:param name="MZ_TOLERANCE" value="${searchParameters.mzTolerance}"/>
                                <jsp:param name="MATCHES_PER_SPECTRUM" value="${searchParameters.limit}"/>
                                <jsp:param name="MZ_TOLERANCE_TYPE" value="${searchParameters.mzToleranceType}"/>
                                <jsp:param name="SHOW_DIALOG" value="true"/>
                            </jsp:include>
                        </div>

                        <div id="email" class="tab-pane fade" role="tabpanel">
                            <div class="form-group row">
                                <div class="col">
                                    <div class="form-check">
                                        <c:choose>
                                            <c:when test="${isLoggedIn}">
                                                <form:checkbox path="sendResultsToEmail"
                                                               label="Send matching results to Email"
                                                               cssClass="form-check-input"/>

                                            </c:when>
                                            <c:otherwise>
                                                <p>
                                                    Please login to enable this feature
                                                </p>

                                            </c:otherwise>

                                        </c:choose>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
</div>
<script>
    function checkForChange(limitFetched, mzToleranceFetched, mzToleranceTypeFetched,
                            retentionIndexMatchFetched, retentionIndexToleranceFetched,
                            scoreThresholdFetched) {
        let scoreThreshold = $('#scorethreshold')[0].value;
        let retentionIndexTolerance = $('#retentionIndexTolerance')[0].value;
        let mzTolerance = $('#mzTolerance')[0].value;
        let limit = $('#limit')[0].value;
        let retentionDefault = $('#retention').get(0).options[0].value;
        let retentionValue = $('#retention').get(0).value;
        let mzToleranceDefault = $('#mzToleranceType').get(0).options[0].value;
        let mzToleranceValue = $('#mzToleranceType').get(0).value;
        if (scoreThreshold != null && retentionIndexTolerance != null
            && mzTolerance != null && limit != null
            && retentionValue != null && retentionDefault != null
            && mzToleranceDefault != null && mzToleranceValue != null) {
            if (scoreThreshold == scoreThresholdFetched
                && retentionIndexTolerance == retentionIndexToleranceFetched
                && mzTolerance == mzToleranceFetched && limit == limitFetched
                && mzToleranceTypeFetched == mzToleranceValue
                && retentionIndexMatchFetched == retentionValue) {
                $("#custom").hide();
            } else {
                $("#custom").show();
            }
        }

    }

    $(document).ready(function() {
        $('#scorethreshold,#retention,#limit,#mzTolerance, #mzToleranceType,#retentionIndexTolerance').change(function () {
            checkForChange(${ searchParameters.limit }, ${ searchParameters.mzTolerance }, '${searchParameters.mzToleranceType}',
                '${searchParameters.retentionIndexMatch}', ${ searchParameters.retentionIndexTolerance }, ${ searchParameters.scoreThreshold });
        }) });
</script>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.13.2/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/saveTabSelection.js"/>"></script>
