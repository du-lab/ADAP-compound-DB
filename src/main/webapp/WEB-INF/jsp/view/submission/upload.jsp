<%--@elvariable id="chromatographyTypeList" type="org.dulab.adapcompounddb.models.enums.ChromatographyType[]"--%>
<%--@elvariable id="fileUploadForm" type="org.dulab.adapcompounddb.site.controllers.FileUploadController.FileUploadForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header card-header-single">
                    Upload File
                </div>
                <div class="card-body">
                    <div class="container">
                        <%--@elvariable id="message" type="java.lang.String"--%>
                        <c:if test="${message}">
                            <div class="row">
                                <div class="col-md-8 offset-md-2">
                                    <p class="text-danger">${message}</p>
                                </div>
                            </div>
                        </c:if>
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

                        <form:form method="POST" modelAttribute="fileUploadForm" enctype="multipart/form-data">
                            <form:errors path="" cssClass="errors"/>
                            <div class="row form-group">
                                <form:label path="chromatographyType"
                                            cssClass="col-12 col-md-3 offset-md-2 col-form-label">Chromatography type</form:label>&nbsp;
                                <div class="col-12 col-md-5">
                                    <form:select path="chromatographyType" cssClass="form-control">
                                        <form:option id="typeValue" value="" label="Please select..."/>
                                        <form:options items="${chromatographyTypeList}" itemLabel="label"/>
                                    </form:select>
                                    <form:errors path="chromatographyType" cssClass="text-danger form-control-sm"/>
                                </div>
                            </div>
                            <%--                            <fieldset class="form-group">--%>
                            <%--                                <div class="row">--%>
                            <%--                                    <form:label path="fileType"--%>
                            <%--                                                cssClass="col-12 col-md-3 offset-md-2 col-form-label">File type</form:label>--%>
                            <%--                                    <div class="col-md-5">--%>
                            <%--                                            &lt;%&ndash;@elvariable id="fileTypeList" type="org.dulab.adapcompounddb.models.enums.FileType[]"&ndash;%&gt;--%>
                            <%--                                        <c:forEach items="${fileTypeList}" var="type">--%>
                            <%--                                            <div class="form-check">--%>
                            <%--                                                <form:radiobutton path="fileType" label="${type.label}" value="${type}"--%>
                            <%--                                                                  cssClass="form-check-input"/>--%>
                            <%--                                            </div>--%>
                            <%--                                        </c:forEach>--%>
                            <%--                                        <form:errors path="fileType" cssClass="text-danger form-control-sm"/>--%>
                            <%--                                    </div>--%>
                            <%--                                </div>--%>
                            <%--                            </fieldset>--%>
                            <div class="row form-group">
                                <form:label path="mergeFiles"
                                            cssClass="col-md-3 offset-md-2 col-form-label">Merge Files</form:label>
                                <div class="col-md-5">
                                    <form:checkbox path="mergeFiles" data-toggle="toggle" data-on="Yes" data-off="No"
                                                   data-size="sm"/>
                                    <form:errors path="mergeFiles" cssClass="text-danger form-control-sm"/>
                                </div>
                            </div>
                            <div class="row form-group">
                                <form:label path="files"
                                            cssClass="col-md-3 offset-md-2 col-form-label">File</form:label>&nbsp;
                                <div class="col-md-5">
                                    <input type="file" name="files" accept=".msp,.csv" class="form-control-file"
                                           multiple/>
                                    <form:errors path="files" cssClass="text-danger form-control-sm"/>
                                </div>
                            </div>

                            <div class="row form-group">
                                <div class="col">
                                    <div id="metaFields" class="collapse">
                                        <div class="card card-body">
                                            <div class="container-fluid">
                                                <div class="row form-group">
                                                    <div class="col-md-4 offset-md-4">MSP Files</div>
                                                    <div class="col-md-4">CSV Files</div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspExternalIdField"
                                                                cssClass="col-md-4 col-form-label">ID Field</form:label>
                                                    <div class="col-md-4">
                                                        <form:input path="mspExternalIdField" cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <form:input path="csvExternalIdField" cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspRetentionTimeField"
                                                                cssClass="col-md-4 col-form-label">Retention Time Field</form:label>
                                                    <div class="col-md-4">
                                                        <form:input path="mspRetentionTimeField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <form:input path="csvRetentionTimeField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                                <div class="row form-group">
                                                    <form:label path="mspMolecularWeightField"
                                                                cssClass="col-md-4 col-form-label">Neutral Mass Field</form:label>
                                                    <div class="col-md-4">
                                                        <form:input path="mspMolecularWeightField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <form:input path="csvMolecularWeightField"
                                                                    cssClass="form-control"/>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col">
                                    <div class="btn-toolbar justify-content-between" role="toolbar">
                                        <div class="btn btn-secondary" data-toggle="collapse"
                                             data-target="#metaFields">Edit Metadata Fields
                                        </div>
                                        <input type="submit" name="submit" value="Upload" class="btn btn-primary"/>
                                    </div>
                                </div>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap4-toggle/js/bootstrap4-toggle.min.js"/>"></script>
<script>
    $('#fileUploadForm').submit(function () {
        $('#progressModal').modal('show');
    });
</script>