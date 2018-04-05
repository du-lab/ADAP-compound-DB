<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Upload file</h1>

    <p style="color: red">
        ${message}
    </p>

    <form:form method="POST" enctype="multipart/form-data">
        <label>Chromatography Type</label>

        <label for="file">File</label><br/>
        <input type="file" name="file" id="file"/><br/>
        <div align="center">
            <input type="submit" value="Upload"/>
        </div>
    </form:form>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />