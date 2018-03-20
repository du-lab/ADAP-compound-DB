<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h2>Upload spectrum</h2>

    <c:if test="${emptyFile != null}">
        No spectra found in the file.<br/><br/>
    </c:if>

    <form method="POST" action="library" enctype="multipart/form-data">
        <input type="hidden" name="action" value="upload"/>
        <input type="file" name="file1"/><br/><br/>
        <input type="submit" value="Upload"/>
    </form>
</section>

<!-- End the middle column -->

<jsp:include page="/includes/column_right_news.jsp" />
<jsp:include page="/includes/footer.jsp" />