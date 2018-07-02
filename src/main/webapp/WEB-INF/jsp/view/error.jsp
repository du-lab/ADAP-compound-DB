<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Error</h1>
    <c:if test="${errorMsg != null}">
        <p class="errors">Message: ${errorMsg}</p>
    </c:if>
    <p>
        Please contact the support team if this error occurs again:
        <a href="mailto:mailto:xiuxia.du@uncc.edu">xiuxia.du@uncc.edu</a>
    </p>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />