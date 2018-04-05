<%--@elvariable id="submission" type="org.dulab.site.models.Submission"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <p>Chromatography Type: <span class="highlighted">${submission.chromatographyType.label}</span> </p>
    <p>Number of spectra: <span class="highlighted">${fn:length(submission.spectra)}</span></p>
</section>

<section>
    <h1>Contained spectra</h1>
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
                    <td>${submission.spectra[i].properties}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Submit</h1>
    <form method="POST" action="<c:url value="/file/submit"/>">
        <label>
            <span>Comment:</span>
            <textarea name="comment" rows="5" cols="80"></textarea>
        </label><br/><br/>
        <label>
            <span>&nbsp;</span>
            <input type="button" value="Discard"/>
            <input type="submit" value="Submit"/>
        </label>
    </form>
</section>


<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />