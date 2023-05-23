<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--@elvariable id="cookieForm" type="java.util.Map<java.lang.String, java.lang.Object>"--%>
<%--@elvariable id="metadataForm" type="org.dulab.adapcompounddb.site.controllers.forms.MetadataForm"--%>
<%--@elvariable id="spectrumProperties" type="java.util.List<java.util.List<java.lang.String>>"--%>
<%--@elvariable id="fileTypes" type="java.util.List<dulab.adapcompounddb.models.enums.FileType>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<c:set var="bgColors" value="${['bg-info', 'bg-success', 'bg-warning']}"/>
<style>
    .field-container {
        display: flex;
        justify-content: space-between;
        margin: 20px;
        user-select: none;
        font-size: 12px;
        min-height: 350px;
    }

    .left-container {
        width: 50%;
        padding: 10px 30px 10px 10px;
        border-right: 1px solid #54241040;
    }

    .right-container {
        width: 50%;
        padding: 10px;
    }

    .draggable {
        height: 25px;
        cursor: pointer;
        margin: 5px;
        padding: 2px 10px;
        border-radius: 5px;
        /*width: fit-content;*/
        box-shadow: rgba(0, 0, 0, 0.02) 0px 1px 3px 0px, rgba(27, 31, 35, 0.15) 0px 0px 0px 1px;
    }

    .drop-container {
        /*border: 1px solid #dadada;*/
        display: flex;
        /*margin-bottom: 10px;*/
        justify-content: space-between;
        align-items: center;
        padding: 2px;
    }

    .droppable {
        height: 40px;
        text-decoration: none;
        /*align-items: center;*/
        /*justify-content: center;*/
        /*display: flex;*/
        text-align: center;
        background: none;
        width: 100% !important;
        padding-left: 15px;
        /* border: 1px solid #54241040; */
        border-bottom: 1px solid #54241040;
    }

    .field-type-container {
        display: flex;
        flex-wrap: wrap;
        padding: 10px 5px;
        min-height: 90px;
        width: 100%;
    }

    .separator {
        border-bottom: 1px solid #54241040;
        width: 94%;
        margin: auto;
    }
</style>
<jsp:include page="../../shared/fieldMapping.jsp"/>
<div class="container">
    <%--    <div class="row row-content align-center">--%>
    <%--        <h2>Edit Metadata</h2>--%>
    <%--    </div>--%>
    <form:form method="POST" modelAttribute="metadataForm" enctype="multipart/form-data">
    <div class="row row-content">
        <div class="col">
            <div>
                <button id="uploadBtn" name="submit" class="btn btn-primary align-right" type="submit"
                        style="height: 100%; float: right;">
                        <%--                        <c:if test="${loggedInUser == null && !integTest}">--%>
                        <%--                            <c:out value="disabled='disabled'"/>--%>
                        <%--                        </c:if>>--%>
                    Add Metadata
                </button>
            </div>
        </div>
    </div>
    <div class="row row-content">
            <%--@elvariable id="showMSP" type="java.lang.Boolean"--%>
            <%--@elvariable id="showCSV" type="java.lang.Boolean"--%>
            <%--@elvariable id="showMGF" type="java.lang.Boolean"--%>
        <div class="col">
            <div id="metaFields">
                <div>
                    <div class="card-header card-header-single"> Add Metadata</div>
                    <div class="container-fluid card card-body">

                        <div class="row form-group">
                            <div class="col-md-9 offset-md-3">
                                <div class="card card-body small">
                                    <span>When option <span class="text-primary">Merge Files</span> is selected,
                                        data from multiple files are merged together. During merging, corresponding
                                        features from different files are determined
                                        based on values of the <span class="text-primary">Name Fields</span>.</span>
                                </div>
                            </div>
                        </div>

                        <div class="row form-group mb-5">
                            <div class="col-md-3 offset-md-3">
                                <div class="custom-control custom-switch">
                                    <input type="checkbox" class="custom-control-input"
                                           name="mergeFiles" id="mergeFiles"
                                           <c:if test="${metadataForm.mergeFiles}">checked</c:if>/>
                                    <label class="custom-control-label" for="mergeFiles">Merge
                                        Files</label>
                                </div>
                            </div>
                        </div>
                        <div style="color:#844d36;">Field Mapping</div>
                        <c:forEach items="${spectrumProperties}" var="propertyList" varStatus="propertyListLoop">
                            <div class="field-container field-container_${propertyListLoop.index}">
                                <div class="left-container">
                                    <div style="display: flex;flex-direction: column;">
                                        <div style="color:#844d36;margin-bottom: 5px">Detected Fields in
                                            the ${fileTypes[propertyListLoop.index]} Files
                                        </div>
                                        <div class="left left_${propertyListLoop.index}">
                                            <c:forEach items="${propertyList}" varStatus="fieldsLoop" var="field">
                                                <div id="droppable${fieldsLoop.index}" class="drop-container">
                                                    <div style="min-width: 30%;max-width:120px;">${field}:</div>
                                                    <div id="droppable_${propertyListLoop.index}"
                                                         class="droppable droppable_${propertyListLoop.index}"
                                                         style="width:70%;max-width: 70%;" data-droppable="${field}"
                                                         data-filetype="${fileTypes[propertyListLoop.index]}_${field}">
                                                        <div id="draggable_${propertyListLoop.index}_-1_-2"
                                                             class="draggable bg-secondary"
                                                             draggable="false" data-inputid="Don't Read">
                                                            Don't Read
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                                <div class="right-container">
                                    <div style="display: flex;flex-direction: column;">
                                        <div style="color:#844d36;margin-bottom: 5px">Read As</div>
                                        <div class="right right_${propertyListLoop.index}"
                                             id="right_${propertyListLoop.index}"
                                             style="display: flex;flex-wrap: wrap;height: fit-content;">
                                            <c:forEach items="${mappingFields}" varStatus="fieldTypeLoop"
                                                       var="fields">
                                                <div class="field-type-container field_type_${fieldTypeLoop.index}">
                                                    <c:forEach items="${fields}" varStatus="fieldLoop" var="field">
                                                        <div id="draggable_${propertyListLoop.index}_${fieldTypeLoop.index}_${fieldLoop.index}"
                                                             class="draggable ${bgColors[fieldTypeLoop.index]}"
                                                             draggable="true"
                                                             data-inputid="${fn:toLowerCase(fileTypes[propertyListLoop.index])}${field.id}">
                                                                ${field.labelText}
                                                            <input type="hidden"
                                                                   id="${fn:toLowerCase(fileTypes[propertyListLoop.index])}${field.id}"
                                                                   name="${fn:toLowerCase(fileTypes[propertyListLoop.index])}${field.id}"
                                                                   value="">
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                                <c:if test="${fieldTypeLoop.index != mappingFields.size() - 1}">
                                                    <div class="separator"></div>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </form:form>

</body>
</html>


