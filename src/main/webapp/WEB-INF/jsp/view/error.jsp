<%--@elvariable id="errorMsg" type="java.lang.String"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Error
                </div>
                <div class="card-body">
                    <c:if test="${errorMsg != null}">
                        <p class="errors">Message: ${errorMsg}</p>
                    </c:if>
                    <p>
                        Please contact the support team if this error occurs again:
                        <a href="mailto:adap.helpdesk@gmail.com">adap.helpdesk@gmail.com</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>