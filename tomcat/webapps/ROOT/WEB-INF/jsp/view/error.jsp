<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section>
    <h1>Error</h1>
    <div>
        <c:if test="${errorMsg != null}">
            <p class="errors">Message: ${errorMsg}</p>
        </c:if>
        <p>
            Please contact the support team if this error occurs again:
            <a href="mailto:dulab.binf@gmail.com">dulab.binf@gmail.com</a>
        </p>
    </div>
</section>