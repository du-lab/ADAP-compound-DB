<%--@elvariable id="chromatographyTypeList" type="org.dulab.adapcompounddb.models.enums.ChromatographyType[]"--%>
<%--@elvariable id="fileUploadForm" type="org.dulab.adapcompounddb.site.controllers.FileUploadController.FileUploadForm"--%>
<%--@elvariable id="loggedInUser" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%--@elvariable id="integTest" type="java.lang.Boolean"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<style>
    .checkbox-grid{

        margin-right: 10px;
        padding: 5px;
        list-style-type: none;
    }
    /*.checkbox-grid li{*/
    /*    display: block;*/
    /*    float: left;*/
    /*    width: 25%;*/
    /*}*/
    input[type=checkbox] {
        margin-right: 5px;
    }
</style>

<script src="https://www.google.com/recaptcha/api.js">

</script>

<script>
    function recaptchaCallback() {
        $('#uploadBtn').removeAttr('disabled');
    }
</script>


<div id="progressModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Upload File</h4>
                <button type="button" class="close" data-dismiss="modal">
                    &times;
                </button>
            </div>
            <div class="modal-body">
                <p>Uploading large files may take a while. Please wait.</p>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <form:form method="POST" modelAttribute="fileUploadForm" enctype="multipart/form-data">

        <div class="row row-content">
            <div class="col">
                <div>
                    <button id="uploadBtn" name="submit" class="btn btn-primary align-right" type="submit"
                            style="height: 100%; float: right;"
                            <c:if test="${loggedInUser == null && !integTest}">
                                <c:out value="disabled='disabled'"/>
                            </c:if>>
                        Upload
                    </button>

                </div>
            </div>
        </div>

        <%--@elvariable id="message" type="java.lang.String"--%>
        <div class="row">
            <div class="col-md-8">
                <p class="text-danger">${message}</p>
            </div>
        </div>

        <div class="row row-content">
            <div class="col">
                <div class="card">
                    <div class="card-header card-header-single">
                        Upload File
                    </div>
                    <div class="card-body">
                        <div class="container">

                                <%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.ConstraintViolation>"--%>
                            <c:if test="${validationErrors}">
                                <div class="row">
                                    <div class="col-md-8 offset-md-2">
                                        <ul class="text-danger">
                                            <c:forEach items="${validationErrors}" var="error">
                                                <li><c:out value="${error.message}"/></li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                            </c:if>

                            <form:errors path="" cssClass="errors"/>
                            <div class="row row-content">
                                <div class="col-md-8">
                                    <div class="container-fluid">
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

                                        <div class="row form-group">
                                            <div class="col-md-4">
                                                <form:label path="files"
                                                            cssClass="col-form-label">Files (max 256MB)</form:label>&nbsp;
                                            </div>
                                            <div class="col-md-8">
                                                <input type="file" name="files"
                                                       accept=".msp,.msl,.csv,.cdf,.mzml,.mzxml,.mgf"
                                                       class="form-control-file" multiple/>
                                                <form:errors path="files" cssClass="text-danger form-control-sm"/>
                                            </div>
                                        </div>


                                        <div class="row form-group">
                                            <div class="col-md-8 offset-md-4">
                                                <div class="custom-control custom-switch">
                                                    <input type="checkbox" class="custom-control-input"
                                                           name="roundMzValues" id="roundMzValues"
                                                           <c:if test="${fileUploadForm.roundMzValues}">checked</c:if>/>
                                                    <label class="custom-control-label" for="roundMzValues">
                                                        Round m/z values in Spectra
                                                    </label>
                                                </div>
                                            </div>
                                        </div>

                                        <c:if test="${loggedInUser == null && !integTest}">
                                            <div id="submit" class="g-recaptcha col-md-8 offset-md-4" data-callback="recaptchaCallback"
                                                 data-sitekey="6LdY3V8hAAAAACkWkUd5G9xYtgnM9vwPvIPsQrWy"></div>
                                        </c:if>

                                    </div>
                                </div>

                            </div>

                            <div id = "metaFields" class="row row-content">
                                <div class="col">
                                    <div class="row row-content">Read metadata</div>
                                    <div  class="row row-content">

                                        <ul class="card card-body small checkbox-grid">
                                            <li><form:checkbox  name="nameField" path="editNameField"/><label for="nameField">Name</label></li>
                                            <li><form:checkbox  name="synonymField" path="editSynonymField"/><label for="synonymField">Synonym</label></li>
                                            <li><form:checkbox  name="idField" path="editExternalIdField"/><label for="idField">ID</label></li>
                                            <li><form:checkbox  name="casIdField" path="editCasNoField"/><label for="casIdField">Cas ID</label></li>
                                            <li><form:checkbox  name="hmdbIdField" path="editHmdbField"/><label for="hmdbIdField">HMDB ID</label></li>
                                            <li><form:checkbox  name="keggIdField" path="editKeggField"/><label for="keggIdField">KEGG ID</label></li>
                                            <li><form:checkbox  name="pubChemIdField" path="editPubChemField"/><label for="pubChemIdField">Pub Chem ID</label></li>
                                        </ul>
                                        <ul class="card card-body small col-md-4 checkbox-grid">
                                            <li><form:checkbox  name="precursorMzField" path="editPrecursorMzField"/><label for="precursorMzField">Precursor Mz</label></li>
                                            <li><form:checkbox  name="editRetentionTimeField" path="editRetentionTimeField"/><label for="editRetentionTimeField">Retention Time</label></li>
                                            <li><form:checkbox  name="editRetentionIndexField" path="editRetentionIndexField"/><label for="editRetentionIndexField">Retention Index</label></li>
                                            <li><form:checkbox  name="editMassField" path="editMassField"/><label for="editMassField">Mass</label></li>
                                        </ul>
                                        <ul class="card card-body small col-md-4 checkbox-grid">
                                            <li><form:checkbox  name="editFormulaField" path="editFormulaField"/><label for="editFormulaField">Formula</label></li>
                                            <li><form:checkbox  name="editCanonicalSmilesField" path="editCanonicalSmilesField"/><label for="editCanonicalSmilesField">Canonical Smiles</label></li>
                                            <li><form:checkbox  name="editInChiField" path="editInChiField"/><label for="editInChiField">InChi</label></li>
                                            <li><form:checkbox  name="editInChiKeyField" path="editInChiKeyField"/><label for="editInChiKeyField">InChIKey</label></li>
                                            <li><form:checkbox  name="editIsotopeField" path="editIsotopeField"/><label for="editIsotopeField">Isotopic Distribution</label></li>


                                        </ul>

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

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<%--<script src="<c:url value="/resources/npm/node_modules/bootstrap4-toggle/js/bootstrap4-toggle.min.js"/>"></script>--%>

<script>
    $('#editMetadata').change(function () {
        if($('#editMetadata')[0].checked) {
            $('#metaFields').show();
        }
        else {
            $('#metaFields').hide();
        }
    });
</script>
<script>
    $('#fileUploadForm').submit(function () {
        $('#progressModal').modal('show');
    });
</script>