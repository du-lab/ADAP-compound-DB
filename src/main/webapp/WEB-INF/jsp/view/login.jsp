<%--@elvariable id="logInForm" type="org.dulab.site.controllers.AuthenticationController.LogInForm"--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

    <div class="row  blue-grey lighten-5">
        <div class="col s12 m4 l3 blue-grey">
            asjghajkg
        </div>

        <div class="col s12 m6 l6 offset-m1 offset-l1">
            <div class="card-panel large">
                <form:form method="post" modelAttribute="logInForm">
                    <div class="card-content">
                        <span class="card-title">Log-in</span>
                        <div class="input-field">
                            <form:label path="username" cssClass="validate">Username</form:label>
                            <form:input path="username"/>
                            <form:errors path="username" cssClass="errors"/>
                        </div>
                        <div class="input-field">
                            <form:label path="password" cssClass="validate">Password</form:label>
                            <form:password path="password"/>
                            <form:errors path="password" cssClass="errors"/>
                        </div>
                    </div>
                    <div class="card-action">
                        <input type="submit" class="btn" value="Log in"/>
                        <a href="/signup/" class="right">Register as a new user...</a>
                    </div>
                </form:form>
            </div>
        </div>
    </div>

<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>