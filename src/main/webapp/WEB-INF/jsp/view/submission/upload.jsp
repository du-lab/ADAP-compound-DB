<%--@elvariable id="chromatographyTypeList" type="org.dulab.adapcompounddb.models.enums.ChromatographyType[]"--%>
<%--@elvariable id="fileUploadForm" type="org.dulab.adapcompounddb.site.controllers.FileUploadController.FileUploadForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script src="https://www.google.com/recaptcha/api.js">

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
                <div class="btn-toolbar justify-content-between" role="toolbar">
                    <div class="btn btn-secondary" data-toggle="collapse"
                         data-target="#metaFields">Edit Metadata Fields
                    </div>
                    <input type="submit" name="submit" value="Upload" class="btn btn-primary"/>

                </div>
            </div>
        </div>
        <div class="row row-content" style="margin-left: 15px;">
            <div class="g-recaptcha" data-sitekey="6LdY3V8hAAAAACkWkUd5G9xYtgnM9vwPvIPsQrWy"/>
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
                            <div class="row">
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
                                                <input type="file" name="files" accept=".msp,.csv,.cdf,.mzml,.mzxml,.mgf"
                                                       class="form-control-file"
                                                       multiple/>
                                                <form:errors path="files" cssClass="text-danger form-control-sm"/>
                                            </div>
                                        </div>

                                        <div class="row form-group">
                                            <div class="col-md-8 offset-md-4">
                                                <div class="custom-control custom-switch">
                                                    <input type="checkbox" class="custom-control-input"
                                                           name="mergeFiles" id="mergeFiles"
                                                           <c:if test="${fileUploadForm.mergeFiles}">checked</c:if>/>
                                                    <label class="custom-control-label" for="mergeFiles">Merge
                                                        Files</label>
                                                </div>
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
                                    </div>
                                </div>

                                <div class="col-md-4">
                                    <div class="card card-body small">
                                        <p>When option <span class="text-primary">Merge Files</span> is selected,
                                            data from multiple files are merged together.</p>
                                        <p>During merging, corresponding features from different files are determined
                                            based on values of the <span class="text-primary">Name Fields</span>. These
                                            Name Fields must be specified in the
                                            <a href="#metaFields" data-toggle="collapse">Metadata Fields</a>
                                            panel for each file type.
                                        </p>
                                    </div>
                                </div>
                            </div>


                            <div class="row row-content">
                                <div class="col">
                                    <div id="metaFields" class="collapse">
                                        <div class="card card-body">
                                            <div class="container-fluid">
                                                <div class="row form-group">
                                                    <div class="col-md-3 offset-3">MSP Files</div>
                                                    <div class="col-md-3">CSV Files</div>
                                                    <div class="col-md-3">MGF Files</div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspNameField"
                                                                cssClass="col-md-3 col-form-label">Name Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspNameField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvNameField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfNameField" cssClass="form-control"/>
                                                    </div>

                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspSynonymField"
                                                                cssClass="col-md-3 col-form-label">Synonym Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspSynonymField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvSynonymField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfSynonymField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspExternalIdField"
                                                                cssClass="col-md-3 col-form-label">ID Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspExternalIdField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvExternalIdField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfExternalIdField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspCasNoField"
                                                                cssClass="col-md-3 col-form-label">CAS ID Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspCasNoField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvCasNoField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfCasNoField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspHmdbField"
                                                                cssClass="col-md-3 col-form-label">HMDB ID Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspHmdbField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvHmdbField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfHmdbField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspKeggField"
                                                                cssClass="col-md-3 col-form-label">KEGG ID Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspKeggField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvKeggField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfKeggField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspPubChemField"
                                                                cssClass="col-md-3 col-form-label">PubChem ID Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspPubChemField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvPubChemField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfPubChemField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspPrecursorMzField"
                                                                cssClass="col-md-3 col-form-label">Precursor m/z Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspPrecursorMzField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvPrecursorMzField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfPrecursorMzField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspRetentionTimeField"
                                                                cssClass="col-md-3 col-form-label">Retention Time Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspRetentionTimeField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvRetentionTimeField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfRetentionTimeField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspRetentionIndexField"
                                                                cssClass="col-md-3 col-form-label">Retention Index Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspRetentionIndexField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvRetentionIndexField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfRetentionIndexField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspMassField"
                                                                cssClass="col-md-3 col-form-label">Neutral Mass Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspMassField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvMassField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfMassField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspFormulaField"
                                                                cssClass="col-md-3 col-form-label">Formula Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspFormulaField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvFormulaField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfFormulaField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspCanonicalSmilesField"
                                                                cssClass="col-md-3 col-form-label">Canonical Smiles Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspCanonicalSmilesField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvCanonicalSmilesField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfCanonicalSmilesField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspInChiField"
                                                                cssClass="col-md-3 col-form-label">InChI Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspInChiField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvInChiField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfInChiField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspInChiKeyField"
                                                                cssClass="col-md-3 col-form-label">InChIKey Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspInChiKeyField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvInChiKeyField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfInChiKeyField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspIsotopeField"
                                                                cssClass="col-md-3 col-form-label">Isotopic Distribution Field</form:label>
                                                    <div class="col-md-3">
                                                        <form:input path="mspIsotopeField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="csvIsotopeField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <form:input path="mgfIsotopeField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
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
    $('#fileUploadForm').submit(function () {
        $('#progressModal').modal('show');
    });
</script>