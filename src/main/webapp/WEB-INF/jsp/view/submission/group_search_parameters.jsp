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
                    <div class="card-header card-header-single">Search Parameters</div>
                    <div class="card-body">

                        <div class="form-row">
                            <div class="col-md-6 px-md-3">
                                <div class="form-group">
                                    <form:label path="scoreThreshold"
                                                cssClass="col-form-label">Score Threshold:</form:label>
                                    <form:input path="scoreThreshold" type="number" cssClass="form-control"/>
                                </div>

                                <div class="form-group">
                                    <form:label path="retentionIndexTolerance"
                                                cssClass="col-form-label">Retention Index Tolerance:</form:label>
                                    <form:input path="retentionIndexTolerance" type="number" cssClass="form-control"/>
                                </div>

                                <div class="form-group">
                                    <form:label path="retentionIndexMatch"
                                                cssClass="col-form-label">Retention Index Match:</form:label>
                                    <form:select path="retentionIndexMatch" cssClass="form-control">
                                        <form:option value="IGNORE_MATCH">Ignore Retention Index</form:option>
                                        <form:option value="PENALIZE_NO_MATCH_STRONG">Penalize matches without Retention Index (Strong)</form:option>
                                        <form:option value="PENALIZE_NO_MATCH_AVERAGE">Penalize matches without Retention Index (Average)</form:option>
                                        <form:option value="PENALIZE_NO_MATCH_WEAK">Penalize matches without Retention Index (Weak)</form:option>
                                        <form:option value="ALWAYS_MATCH">Always match Retention Index</form:option>
                                    </form:select>
                                </div>

                                <div class="form-group">
                                    <form:label path="mzTolerance" cssClass="col-form-label">m/z tolerance</form:label>
                                    <div class="input-group">
                                        <form:input path="mzTolerance" type="number" step="0.001" cssClass="form-control"/>
                                        <div class="input-group-append">
                                            <form:select path="mzToleranceType" cssClass="input-group-text">
                                                <form:option value="DA">Da</form:option>
                                                <form:option value="PPM">ppm</form:option>
                                            </form:select>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <form:label path="limit" cssClass="col-form-label">Matches per Spectrum</form:label>
                                    <form:input path="limit" type="number" cssClass="form-control"/>
                                </div>
                            </div>

                            <fieldset class="form-group col-md-6 px-md-3">
                                <form:label path="submissionIds"
                                            cssClass="col-form-label">Search in Libraries:</form:label>
                                <c:forEach items="${filterOptions.submissions}" var="submission" varStatus="status">
                                    <div class="custom-control custom-switch">
                                        <input class="custom-control-input" type="checkbox" name="submissionIds"
                                               id="submissionIds${status.index}" value="${submission.key}"
                                               <c:if test="${filterForm.submissionIds.contains(submission.key)}">checked</c:if>>
                                        <label class="custom-control-label"
                                               for="submissionIds${status.index}">${submission.value}</label>
                                    </div>
                                </c:forEach>
                                <input type="hidden" name="_submissionIds" value="on">
                                    <%--                            <form:checkboxes path="submissionIds" items="${filterOptions.submissions}" cssClass="form-check-input"/>--%>
                            </fieldset>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </form:form>
</div>