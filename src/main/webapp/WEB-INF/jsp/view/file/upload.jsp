<%--@elvariable id="chromatographyTypeList" type="org.dulab.adapcompounddb.models.ChromatographyType[]"--%>
<%--@elvariable id="fileUploadForm" type="org.dulab.adapcompounddb.site.controllers.FileUploadController.FileUploadForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Upload file</h1>

    <p class="errors">${message}</p>
    <c:if test="${validationErrors != null}"><div class="errors">
        <ul>
            <c:forEach items="${validationErrors}" var="error">
                <li><c:out value="${error.message}"/></li>
            </c:forEach>
        </ul>
    </div></c:if>

    <form:form method="POST" modelAttribute="fileUploadForm" enctype="multipart/form-data">
        <form:errors path="" cssClass="errors"/><br/>

        <form:label path="chromatographyType">Chromatography type:</form:label><br/>
        <form:select path="chromatographyType">
            <form:option value="" label="Please select..."/>
            <form:options items="${chromatographyTypeList}" itemLabel="label"/>
        </form:select><br/>
        <form:errors path="chromatographyType" cssClass="errors"/><br/>

        <form:label path="fileType">File type:</form:label><br/>
        <form:radiobuttons path="fileType" items="${fileTypeList}" itemLabel="label"/><br/>
        <form:errors path="fileType" cssClass="errors"/><br/>

        <form:label path="file">File:</form:label><br/>
        <input type="file" name="file"/><br/>
        <form:errors path="file" cssClass="errors"/>
        <div align="center">
            <input type="submit" value="Upload"/>
        </div>
    </form:form>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />