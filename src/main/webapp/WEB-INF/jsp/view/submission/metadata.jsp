<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%--@elvariable id="cookieForm" type="java.util.Map<java.lang.String, java.lang.Object>"--%>
<%--@elvariable id="metadataForm" type="org.dulab.adapcompounddb.site.controllers.forms.MetadataForm"--%>
<%--@elvariable id="spectrumProperties" type="java.util.List<java.util.List<org.dulab.adapcompounddb.models.entities.SpectrumProperty>>"--%>
<%--@elvariable id="fileTypes" type="java.util.List<dulab.adapcompounddb.models.enums.FileType>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>

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
                    <div class="card-header card-header-single"> Edit Metadata</div>
                    <div class="container-fluid card card-body">
                        <div class="row form-group">
                            <div class="col-md-3 offset-3">
                                <div class="custom-control custom-switch">
                                    <input type="checkbox" class="custom-control-input"
                                           name="mergeFiles" id="mergeFiles"
                                           <c:if test="${metadataForm.mergeFiles}">checked</c:if>/>
                                    <label class="custom-control-label" for="mergeFiles">Merge
                                        Files</label>
                                </div>
                            </div>
                        </div>
                        <div class="row form-group">
                            <c:forEach items="${fileTypes}" var="fileType" varStatus="loop">
                                <div class="${loop.index == 0 ? 'col-md-3 offset-3' : 'col-md-3'}">
                                        ${fileType} Files
                                </div>
                            </c:forEach>
                        </div>
                        <c:forEach items="${fieldList}" var="field">
                            <div class="row form-group">
                                <form:label path="msp${field.id}" cssClass="col-md-3 col-form-label">${field.labelText}</form:label>
                                <c:forEach items="${fileTypes}" var="fileType" varStatus="loop">
                                    <div class="col-md-3">
                                        <form:select path="${fn:toLowerCase(fileType)}${field.id}" cssClass="form-control">
                                            <form:option value="" label=""></form:option>
                                            <c:forEach items="${spectrumProperties[loop.index]}" var="property">
                                                <c:set var="fieldName" value="${fn:toLowerCase(fileType)}${field.id}"/>
                                                <form:option
                                                        value="${property.getName()}" label="${property.getName()}"
                                                        selected="${property.getName() == cookieForm[fieldName] ? 'selected' : ''}"/>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                </c:forEach>

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
