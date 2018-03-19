<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h2>Upload spectrum</h2>

    <form method="POST" action="library" enctype="multipart/form-data">
        <input type="hidden" name="action" value="upload"/>
        <input type="file" name="file1"/><br/><br/>
        <input type="submit" value="Submit"/>
    </form>
</section>

<!-- End the middle column -->

<jsp:include page="/includes/column_right_news.jsp" />
<jsp:include page="/includes/footer.jsp" />