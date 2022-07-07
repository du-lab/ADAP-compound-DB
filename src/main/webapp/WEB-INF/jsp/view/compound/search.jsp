<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <%--@elvariable id="compoundSearchForm" type="org.dulab.adapcompounddb.site.controllers.forms.CompoundSearchForm"--%>
    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
    <form:form modelAttribute="compoundSearchForm" method="post">
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
                            <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#search">
                                Individual Search
                            </a></li>

                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#parameters">
                                Parameters
                            </a></li>

                        </ul>
                    </div>

                    <div class="card-body tab-content">
                        <div id="search" class="tab-pane fade show active" role="tabpanel">
                            <div class="row form-group">
                                <div class="col-md-4">
                                    <form:label path="chromatographyType"
                                                cssClass="col-form-label">Chromatography type</form:label>&nbsp;
                                </div>
                                <div class="col-md-8">
                                    <form:select path="chromatographyType" cssClass="form-control">
                                        <form:option id="typeValue" value="" label="Please select..."/>
                                        <form:options items="${chromatographyTypeList}" itemLabel="label"/>
                                    </form:select>
                                    <form:errors path="chromatographyType"
                                                 cssClass="text-danger form-control-sm"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="identifier"
                                            cssClass="col-md-4 col-form-label">Identifier:</form:label>
                                <div class="col-md-8">
                                    <form:input path="identifier"  cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="neutralMass"
                                            cssClass="col-md-4 col-form-label">Neutral Mass:</form:label>
                                <div class="col-md-8">
                                    <form:input path="neutralMass" type="number"  step="0.001" cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="precursorMZ"
                                            cssClass="col-md-4 col-form-label">Precursor M/Z:</form:label>
                                <div class="col-md-8">
                                    <form:input path="precursorMZ" type="number" step="0.001" cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="spectrum"
                                            cssClass="col-md-4 col-form-label">Spectrum:</form:label>
                                <div class="col-md-8">
                                    <form:textarea path="spectrum"  cssClass="form-control"/>
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