<%--@elvariable id="chromatographyTypeList" type="org.dulab.adapcompounddb.models.ChromatographyType[]"--%>
<%--@elvariable id="fileUploadForm" type="org.dulab.adapcompounddb.site.controllers.FileUploadController.FileUploadForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<section>
    <h1>Upload file</h1>

    <div align="center">
        <div align="left" style="display: inline-block;">
            <p class="errors">${message}</p>
            <c:if test="${validationErrors != null}">
                <div class="errors">
                    <ul>
                        <c:forEach items="${validationErrors}" var="error">
                            <li><c:out value="${error.message}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <form:form method="POST" modelAttribute="fileUploadForm" enctype="multipart/form-data">
                <p>
                    <form:errors path="" cssClass="errors"/><br/>
                </p>
                <p>
                    <form:label path="chromatographyType">Chromatography type:</form:label>&nbsp;
                    <form:errors path="chromatographyType" cssClass="errors"/><br/>
                    <form:select path="chromatographyType">
                        <form:option value="" label="Please select..."/>
                        <form:options items="${chromatographyTypeList}" itemLabel="label"/>
                    </form:select><br/>
                </p>
                <p>
                    <form:label path="fileType">File type:</form:label>&nbsp;
                    <form:errors path="fileType" cssClass="errors"/><br/>
                    <form:radiobuttons path="fileType" items="${fileTypeList}" itemLabel="label"/><br/>
                </p>
                <p>
                    <form:label path="files">File:</form:label>&nbsp;
                    <form:errors path="files" cssClass="errors"/><br/>
                    <input type="file" name="files" accept=".msp" multiple/><br/>
                </p>

                <div align="center">
                    <input type="submit" value="Upload"/>
                </div>
            </form:form>
        </div>
    </div>
</section>