<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="loggedInUser" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%--@elvariable id="disableBtn" type="java.lang.Boolean"--%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script src="https://www.google.com/recaptcha/api.js"></script>
<script>
    function recaptchaCallback() {
        $('#searchButton').removeAttr('disabled');
    }
</script>
<div class="container">
    <%--@elvariable id="compoundSearchForm" type="org.dulab.adapcompounddb.site.controllers.forms.CompoundSearchForm"--%>
    <%--@elvariable id="errorMessage" type="java.lang.String"--%>
    <%--@elvariable id="filterOptions" type="org.dulab.adapcompounddb.site.controllers.forms.FilterOptions"--%>
    <%--@elvariable id="chromatographyTypes" type="org.dulab.adapcompounddb.models.enums.ChromatographyType[]"--%>
    <%--@elvariable id="adductvals" type="org.dulab.adapcompounddb.models.entities.Adduct[]"--%>

        <form:form modelAttribute="compoundSearchForm" method="post">
        <div class="row row-content">
            <div class="col">
                <div class="form-row">
                    <div class="col">

                        <div class="btn-toolbar justify-content-end" role="toolbar">


                            <button id="searchButton" class="btn btn-primary align-self-end" type="submit"
                                    style="height: 100%;"
                                    <c:if test="${disableBtn}">
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

                            <li class="nav-item"><a id="parametersTab" class="nav-link" data-toggle="tab" href="#parameters">
                               Parameters
                                <span id="custom" class="badge badge-info" style="display: none">
                                    Custom Parameters
                                </span>
                            </a></li>

                        </ul>
                    </div>

                    <div class="card-body tab-content">

                        <div id="search" class="tab-pane fade show active" role="tabpanel">
                            <div class="alert-danger" style="margin-bottom: 5px;">${errorMessage}</div>

                            <div class="form-group row">
                                <form:label path="chromatographyType"
                                            cssClass="col-md-4 col-form-label">Chromatography type</form:label>
<%--                                <div class="col-md-8">--%>
<%--                                    <form:select id="chromatographySelect" path="chromatographyType" cssClass="form-control">--%>
<%--                                        <form:option id="typeValue" value="" label="Please select..."/>--%>
<%--                                        <form:options items="${chromatographyTypes}" itemLabel="label"/>--%>
<%--                                    </form:select>--%>
<%--                                    <form:errors path="chromatographyType"--%>
<%--                                                 cssClass="text-danger form-control-sm"/>--%>
<%--                                </div>--%>
                                <div class="col-md-8">
                                    <div class="btn-group btn-group-toggle" data-toggle="buttons" cssClass="form-control">
                                        <label class="btn btn-outline-primary active">
                                            <form:radiobutton path="chromatographyType"
                                                              name="chromatographyType"
                                                              value="GC-MS"
                                                              label="GC-MS"
                                                              id="gcms"
                                                              />
                                        </label>
                                        <label class="btn btn-outline-primary">
                                            <form:radiobutton
                                                    path="chromatographyType"
                                                    name="chromatographyType"
                                                    value="LC-MS"
                                                    label="LC-MS"
                                                    />
                                        </label>
                                        <label class="btn btn-outline-primary">
                                            <form:radiobutton path="chromatographyType"
                                                              name="chromatographyType"
                                                              value="LC-MS/MS"
                                                              label="LC-MS/MS"
                                                              />
                                        </label>
                                    </div>
                                </div>
                            </div>



                            <div class="form-group row">
                                <form:label path="identifier"
                                            cssClass="col-md-4 col-form-label">Identifier:</form:label>
                                <div class="col-md-8">
                                    <form:input id="identifierInput" path="identifier" placeholder="Input name, CAS, HMDB, KEGG, PubChem ID, or InChIKey" cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row">
                                <form:label path="neutralMass"
                                            cssClass="col-md-4 col-form-label">Neutral Mass:</form:label>
                                <div class="col-md-8">
                                    <form:input path="neutralMass" placeholder="Input mass" type="number" step="any" cssClass="form-control"/>
                                </div>
                            </div>
                            <div class="form-group row" id="precursorMZ" >
                                <form:label path="precursorMZ"
                                            cssClass="col-md-4 col-form-label">Precursor M/Z:</form:label>
                                <div class="col-md-8">
                                    <form:input id="precusorMZInput" placeholder="Input precursor m/z" path="precursorMZ" type="number" step="any" cssClass="form-control"/>
                                </div>
                            </div>
                            <div id="adduct" class="form-group row">
                                <form:label path="adducts" cssClass="col-md-4 col-form-label">Adduct:</form:label>
                                <div class="col-md-8">

                                    <form:select path="adducts" id="adductSelect"  cssClass="form-control"  multiple="multiple" cssStyle="text-align: left;">

                                        <form:options items="${adductvals}" itemValue="Id" itemLabel="Name"></form:options>
                                    </form:select>

                                </div>
                            </div>
                            <div id="spectrum" class="form-group row">
                                <form:label path="spectrum"
                                            cssClass="col-md-4 col-form-label">Spectrum:</form:label>
                                <div class="col-md-8">
                                    <form:textarea id="spectrumInput" placeholder="Input m/z-intensity pairs"  path="spectrum"  cssClass="form-control"/>
                                </div>
                            </div>
                            <c:if test="${loggedInUser == null}">
                                <div class="g-recaptcha col-md-8 offset-md-4" data-callback="recaptchaCallback"
                                     data-sitekey="6LdY3V8hAAAAACkWkUd5G9xYtgnM9vwPvIPsQrWy" style="margin-right: 10px;"></div>
                            </c:if>
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
                            <jsp:include page="./user_search_parameters.jsp">
                                <jsp:param name="SCORE_THRESHOLD" value="${searchParameters.scoreThreshold}"/>
                                <jsp:param name="RETENTION_INDEX_TOLERANCE" value="${searchParameters.retentionIndexTolerance}"/>
                                <jsp:param name="RETENTION_INDEX_MATCH" value="${searchParameters.retentionIndexMatch}"/>
                                <jsp:param name="MZ_TOLERANCE" value="${searchParameters.mzTolerance}"/>
                                <jsp:param name="MATCHES_PER_SPECTRUM" value="${searchParameters.limit}"/>
                                <jsp:param name="MZ_TOLERANCE_TYPE" value="${searchParameters.mzToleranceType}"/>
                                <jsp:param name="SHOW_DIALOG" value="true"/>
                            </jsp:include>
                        </div>


                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <sec:csrfInput />
</div>

<style>
    .multiselect-container {
        width: 100% !important;
    }
</style>

<script>


    function HasFormChanged() {
        var showBadge = false;
        let num = $("#parameters input").filter(function () {
            return $.trim($(this).val()).length == 0
        }).length;

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

    function chromatographyChanged() {
        if(!$("input[name='chromatographyType']:checked").val()) {
            $('#gcms').prop("checked", true);
        }
        else if($("input[name='chromatographyType']:checked").val() === 'GC-MS') {

            $('#precursorMZInput').val('');
            $('#precursorMZ').hide();
            $('#spectrum').show();
            $('#adduct').hide();

        }
        else if($("input[name='chromatographyType']:checked").val() === 'LC-MS') {
            $('#spectrumInput').val('');
            $('#spectrum').hide();
            $('#precursorMZ').show();
            $('#adduct').show();
        }
        else {
            $('#precursorMZ').show();
            $('#spectrum').show();
            $('#adduct').hide();
        }
    }

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
        $('#adductSelect').multiselect({includeSelectAllOption:true, nonSelectedText:'Please select adduct',
        buttonWidth:'100%',
        maxHeight: 300});
        $("#adductSelect").multiselect('selectAll', false);
        chromatographyChanged();


        // HasFormChanged();
        // $('#parameters input').change(function() {
        //    HasFormChanged();
        //
        // });
        // $('#retention').change(function() {
        //     HasFormChanged();
        //
        // })
        //
        // $('#limit').change(function() {
        //    HasFormChanged();
        //
        // })

        $('#scorethreshold,#retention,#limit,#mzTolerance, #mzToleranceType,#retentionIndexTolerance').change(function() {
            checkForChange(${searchParameters.limit},${searchParameters.mzTolerance},'${searchParameters.mzToleranceType}',
                '${searchParameters.retentionIndexMatch}',${searchParameters.retentionIndexTolerance},${searchParameters.scoreThreshold});
        })

        $("input[type=radio][name='chromatographyType']").change(chromatographyChanged);



    });







</script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/1.1.1/css/bootstrap-multiselect.css">
<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.13.2/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/saveTabSelection.js"/>"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/1.1.1/js/bootstrap-multiselect.js"></script>
