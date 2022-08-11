<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="loggedInUser" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>

<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<script>
    function recaptchaCallback() {
        $('#searchButton').removeAttr('disabled');
    }
</script>
<div class="container">
    <%--@elvariable id="compoundSearchForm" type="org.dulab.adapcompounddb.site.controllers.forms.CompoundSearchForm"--%>
    <%--@elvariable id="errorMessage" type="java.lang.String"--%>
    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
    <form:form modelAttribute="compoundSearchForm" method="post">
        <div class="row row-content">
            <div class="col">
                <div class="form-row">
                    <div class="col">

                        <div class="btn-toolbar justify-content-end" role="toolbar">
                            <c:if test="${loggedInUser == null}">
                                <div class="g-recaptcha" data-callback="recaptchaCallback"
                                     data-sitekey="6LdY3V8hAAAAACkWkUd5G9xYtgnM9vwPvIPsQrWy" style="margin-right: 10px;"></div>
                            </c:if>

                            <button id="searchButton" class="btn btn-primary align-self-center" type="submit"
                                    style="height: 100%;"
                                    <c:if test="${loggedInUser == null}">
                                <c:out value="disabled='disabled'"/>
                                    </c:if>>
                                    Search
                            </button>
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

                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#libraries">
                                Libraries
                            </a></li>

                            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#parameters">
                               Parameters
                                <span id="custom" class="badge badge-info" style="display: none;">Custom Parameters</span>



                            </a></li>

                        </ul>
                    </div>

                    <div class="card-body tab-content">

                        <div id="search" class="tab-pane fade show active" role="tabpanel">
                            <div class="alert-danger" style="margin-bottom: 5px;">${errorMessage}</div>

                            <div class="form-group row">
                                <form:label path="identifier"
                                            cssClass="col-md-4 col-form-label">Identifier:</form:label>
                                <div class="col-md-8">
                                    <form:input path="identifier" placeholder="Input name, CAS, HMDB, KEGG, PubChem ID, or InChIKey" cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="neutralMass"
                                            cssClass="col-md-4 col-form-label">Neutral Mass:</form:label>
                                <div class="col-md-8">
                                    <form:input path="neutralMass" type="number" step="any" cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="precursorMZ"
                                            cssClass="col-md-4 col-form-label">Precursor M/Z:</form:label>
                                <div class="col-md-8">
                                    <form:input path="precursorMZ" type="number" step="any" cssClass="form-control"/>
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
                        <div id="libraries" class="tab-pane fade" role="tabpanel">
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
                                                   <c:if test="${compoundSearchForm.getSubmissionIds().contains(submission.key)}">checked</c:if>>
                                            <label class="custom-control-label"
                                                   for="submissionIds${status.index}">${submission.value}</label>
                                        </div>
                                    </c:forEach>
                                </div>
                                <input type="hidden" name="_submissionIds" value="on">
                            </fieldset>
                        </div>



                        <div id="parameters" class="tab-pane fade" role="tabpanel">
                            <div class="row">
                                <div class="col-md-8 offset-md-2">
                                    <p>Leave parameters blanks to use the default values.</p>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label id="scoreThreshold" path="scoreThreshold"
                                            cssClass="col-md-4 col-form-label">Score Threshold (1 - 5000):</form:label>
                                <div class="col-md-8">
                                    <form:input path="scoreThreshold" type="number" step="1" cssClass="form-control"/>
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
                                    <form:select id="retention" path="retentionIndexMatch" cssClass="form-control">
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
                                    <form:input id="limit" path="limit" type="number" cssClass="form-control"/>
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


    function HasFormChanged() {
        var showBadge = false;
        let num = $("#parameters input").filter(function () {
            return $.trim($(this).val()).length == 0
        }).length;
        console.log(num);
        if(num < 3) showBadge = true;
        var retention = $('#retention').get(0);


        var isDirty = !retention.options[retention.selectedIndex].defaultSelected;
        //console.log(isDirty);
        if(isDirty) showBadge = true;

        console.log($('#limit'));
        var text = $('#limit').get(0).defaultValue;

        if(text != $('#limit').get(0).value)
            showBadge = true;

        if(showBadge)
            $("#custom").show();
        else
            $("#custom").hide();


    }
    $(document).ready(function() {
        HasFormChanged();
        $('#parameters input').change(function() {
           HasFormChanged();

        });
        $('#retention').change(function() {
            HasFormChanged();

        })

        $('#limit').change(function() {
           HasFormChanged();

        })



    });





</script>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>

