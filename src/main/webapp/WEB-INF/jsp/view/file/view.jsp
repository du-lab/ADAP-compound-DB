<%--@elvariable id="submission" type="org.dulab.site.models.Submission"--%>
<%--@elvariable id="form" type="org.dulab.site.submission.FileViewController.Form"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>File content</h1>
    <div align="right" style="float: right">
        <p><a href="raw/download" class="button" target="_blank">Download file</a></p>
        <p><a href="raw/view" class="button" target="_blank">View file</a></p>
    </div>
    <p>Filename: <span class="highlighted">${submission.filename}</span></p>
    <p>File Type: <span class="highlighted">${submission.fileType.label}</span></p>
    <p>Chromatography Type: <span class="highlighted">${submission.chromatographyType.label}</span></p>
    <p>Number of spectra: <span class="highlighted">${fn:length(submission.spectra)}</span></p>
</section>

<section>
    <h1>Mass spectra</h1>
    <p>
        Click on name to view the mass spectrum
    </p>
    <div align="center" style="overflow: auto; max-height: 400px">
        <table>
            <tr>
                <th>No</th>
                <th>Name</th>
                <th>Properties</th>
            </tr>
            <c:forEach var="i" begin="0" end="${submission.spectra.size() - 1}">
                <tr>
                    <td>${i + 1}</td>
                    <td>
                        <a href="<c:url value="/file/${i}"/>">${submission.spectra[i]}</a>
                    </td>
                    <td>${dulab:abbreviateString(submission.spectra[i].properties, 80)}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Submit</h1>
    <div align="center">
        <div align="left" style="width: 600px">
            <p>
                Please provide name and detailed description of the data when you submit mass spectra to the library.
                This information will be used for finding unknown compounds.
            </p>
        </div>

        <div align="left" class="subsection">
            <c:if test="${validationErrors != null}">
                <div class="errors">
                    <ul>
                        <c:forEach items="${validationErrors}" var="error">
                            <li><c:out value="${error.message}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <form:form method="POST" modelAttribute="form">
                <form:errors path="" cssClass="errors"/><br/>

                <form:label path="name">Name:</form:label><br/>
                <form:input path="name"/><br/>
                <form:errors path="name" cssClass="errors"/><br/>

                <form:label path="description">Description:</form:label><br/>
                <form:textarea path="description" rows="12" cols="80"/><br/>
                <form:errors path="description"/><br/>

                <div align="center">
                    <input type="submit" value="Submit"/>
                </div>
        </form:form>
        </div>
    </div>
</section>


<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />