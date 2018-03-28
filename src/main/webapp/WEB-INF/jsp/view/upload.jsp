<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Upload file</h1>

    <c:if test="${emptyFile != null}">
        No spectra found in the file.<br/><br/>
    </c:if>

    <form method="POST" action="/file/upload" enctype="multipart/form-data">
        <input type="hidden" name="action" value="upload"/>
        <label>
            <span>File</span>
            <input type="file" name="file1"/>
        </label>
        <label>
            <span>&nbsp;</span>
            <input type="submit" value="Upload"/>
        </label>
    </form>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />