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
<script>
    $(document).ready(function() {
        $(".draggable").on("dragstart", function(event) {
            console.log("dragstart")
            event.originalEvent.dataTransfer.setData("text/plain", event.target.id);
        });

        $(".left, .right").on("dragover", function(event) {
            console.log("dragover")
            event.preventDefault();
        });

        $(".left .droppable").on("drop", function(event) {
            event.preventDefault();
            console.log("dropped left")
            let data = event.originalEvent.dataTransfer.getData("text/plain");
            let element = document.getElementById(data);
            let existingElement = $(this).children().first();
            console.log(element.id)
            if (existingElement.length) {
                if (existingElement.innerText == "Don't Read") {
                    existingElement.remove();
                } else {
                    $(".right").append(existingElement);
                }
            }
            if (element.id != "draggableDontRead") {
                $(this).append(element);
                addDoubleClickEventListener(element);
            } else {
                console.log(element)
                let clonedElement = $(element).clone();
                clonedElement.id="draggableDontRead";
                clonedElement.innerText = "Don't Read";
                clonedElement.addClass("draggable");
                clonedElement.on("dragstart", function(event) {
                    console.log("dragstart")
                    event.originalEvent.dataTransfer.setData("text/plain", event.target.id);
                });
                $(this).append(clonedElement);
                addDoubleClickEventListener(clonedElement);
            }
        });

        $(".right").on("drop", function(event) {
            event.preventDefault();
            console.log("drop right left")
            let data = event.originalEvent.dataTransfer.getData("text/plain");
            let element = document.getElementById(data);
            console.log(element)
            if (element.innerText == "Don't Read") {
                console.log("if")
                element.remove();
            } else {
                console.log("else")
                $(".right").append(element);
            }

        });

        function addDoubleClickEventListener(element) {
            $(element).on("dblclick", function() {
                console.log(element.innerText)
                if (element.id == "draggableDontRead") {
                    $(this).remove();
                } else {
                    let parentContainer = $(this).closest(".field-container");
                    $(this).detach();
                    $(this).unbind("dblclick")
                    parentContainer.find(".right").append($(this));
                }
            });
        }
    });
</script>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<style>
    .field-container {
        display: flex;
        justify-content: space-between;
        margin: 20px;
        user-select: none;
        font-size:12px;
    }
    .left {
        width: 50%;
        padding: 10px;
    }
    .right {
        width:50%;
        padding: 10px;
    }
    .draggable {
        height: 25px;
        cursor: pointer;
        margin: 5px;
        padding: 2px 10px;
        border-radius: 5px;
        width: fit-content;
        box-shadow: rgba(0, 0, 0, 0.02) 0px 1px 3px 0px, rgba(27, 31, 35, 0.15) 0px 0px 0px 1px;
    }
    .drop-container {
        border: 1px solid #dadada;
        display: flex;
        margin-bottom: 10px;
        justify-content: space-between;
        align-items: center;
        padding: 5px;
    }
    .droppable {
        height: 30px;
        text-decoration: none;
        align-items: center;
        background: none;
        display: flex;
        padding-left: 15px;

        /*justify-content: center;*/
    }
</style>
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
                        <c:forEach items="${spectrumProperties}" var="propertyList" varStatus="loop">
                            <jsp:include page="../../shared/csv_field_mapper.jsp"/>
                            <div class="field-container">
                                <div class="left">
                                    <div style="display: flex;flex-direction: column;">
                                        <div style="color:#844d36;margin-bottom: 5px">Detected Fields in the ${fileTypes[loop.index]} Files</div>
                                        <div>
                                            <c:forEach items="${propertyList}" varStatus="loop" var="field">
                                                <div id="droppable${loop.index}" class="drop-container">
                                                    <div style="min-width: 30%;">${field}:</div>
                                                    <div class="droppable" style="width:70%;max-width: 70%;">
<%--                                                        <div id="draggableDontRead${loop.index}" class="draggable" draggable="true">--%>
<%--                                                            Don't Read--%>
<%--                                                        </div>--%>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                                <div class="right">
                                    <div style="display: flex;flex-direction: column;">
                                        <div style="color:#844d36;margin-bottom: 5px">Read As</div>
                                        <div style="display: flex;flex-wrap: wrap;height: fit-content;">
                                            <c:forEach items="${csvMappingFields}" varStatus="loop" var="field">
                                                <div id="draggable${loop.index}" class="draggable" draggable="true">
                                                        ${field}
                                                </div>
                                            </c:forEach>
                                            <div id="draggableDontRead" class="draggable" draggable="true">
                                                Don't Read
                                            </div>
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


