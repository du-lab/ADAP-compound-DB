<%--@elvariable id="logInForm" type="org.dulab.site.controllers.LogInForm"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />

<!-- Start the middle column -->

<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- Bootstrap core CSS -->
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/bootstrap-theme.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/resources/css/signin.css" rel="stylesheet">
    <%--<link href="/resources/css/login.css" rel="stylesheet">--%>
</head>
<body>
<section>
    <div align="center">

    </div>
</section>

    <section>
    <div align="center">
        <div align="left" class="subsection">
            <c:if test="${loginFailed}">
                <b class="errors">The username and password you entered are not correct. Please try again.</b><br/>
            </c:if><c:if test="${validationErrors != null}"><div class="errors">
                <ul>
                    <c:forEach items="${validationErrors}" var="error">
                        <li><c:out value="${error.message}"/></li>
                    </c:forEach>
                </ul>
            </div></c:if>


            <div class="container" align="center">
                <form:form method="POST" modelAttribute="logInForm" cssClass="form-signin" >
                    <h2 class="form-signin">Please sign in</h2></br>
                    <h4>You must log in to submit new mass spectra to the library</h4></br>
                    <form:label cssClass="sr-only" path="username">Username:</form:label>
                    <form:input path="username" cssClass="form-control" placeholder="Username"/>
                    <form:errors path="username" cssClass="errors"/>
                    <form:label cssClass="sr-only" path="password">Password:</form:label>
                    <form:password path="password" cssClass="form-control" placeholder="Password"/>
                    <form:errors path="password" cssClass="errors"/>
                    <div>
                        <button class="btn btn-lg btn-primary btn-primary" type="submit">Sign in</button>
                    </div>
                </form:form>

                <h5 class="form-signin"> If you are not registered yet, please do it now:</h5>
                <a href="<c:url value="/signup"/>" class="btn btn-lg btn-primary">Register</a>
            </div>

    </section>
</body>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />