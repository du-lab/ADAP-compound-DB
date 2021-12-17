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
                            <button class="btn btn-primary" type="submit">Search</button>
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
                            <div class="row">
                                <div class="col-md-8 offset-md-2">
                                    <p>Leave parameters blanks to use the default values.</p>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="scoreThreshold"
                                            cssClass="col-md-4 col-form-label">Score Threshold:</form:label>
                                <div class="col-md-8">
                                    <form:input path="scoreThreshold" type="number" cssClass="form-control"/>
                                </div>
                            </div>

                            <div class="form-group row">
                                <form:label path="retentionIndexTolerance"
                                            cssClass="col-md-4 col-form-label">Retention Index Tolerance:</form:label>
                                <div class="col-md-8">
                                    <form:input path="retentionIndexTolerance" type="number" cssClass="form-control"/>
                                </div>
                            </div>

                            <div class="form-group row">
                                <form:label path="retentionIndexMatch"
                                            cssClass="col-md-4 col-form-label">Retention Index Match:</form:label>
                                <div class="col-md-8">
                                    <form:select path="retentionIndexMatch" cssClass="form-control">
                                        <form:option value="IGNORE_MATCH">Ignore Retention Index</form:option>
                                        <form:option
                                                value="PENALIZE_NO_MATCH_STRONG">Penalize matches without Retention Index (Strong)</form:option>
                                        <form:option
                                                value="PENALIZE_NO_MATCH_AVERAGE">Penalize matches without Retention Index (Average)</form:option>
                                        <form:option
                                                value="PENALIZE_NO_MATCH_WEAK">Penalize matches without Retention Index (Weak)</form:option>
                                        <form:option value="ALWAYS_MATCH">Always match Retention Index</form:option>
                                    </form:select>
                                </div>
                            </div>

                            <div class="form-group row">
                                <form:label path="mzTolerance"
                                            cssClass="col-md-4 col-form-label">m/z tolerance</form:label>
                                <div class="input-group col-md-8">
                                    <form:input path="mzTolerance" type="number" step="0.001"
                                                cssClass="form-control"/>
                                    <div class="input-group-append">
                                        <form:select path="mzToleranceType" cssClass="input-group-text">
                                            <form:option value="DA">Da</form:option>
                                            <form:option value="PPM">ppm</form:option>
                                        </form:select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group row">
                                <form:label path="limit"
                                            cssClass="col-md-4 col-form-label">Matches per Spectrum</form:label>
                                <div class="col-md-8">
                                    <form:input path="limit" type="number" cssClass="form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>