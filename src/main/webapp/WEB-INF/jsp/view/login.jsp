<%--@elvariable id="logInForm" type="org.dulab.site.controllers.AuthenticationController.LogInForm"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <!--Import Google Icon Font-->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!--Import materialize.css-->
    <link type="text/css" rel="stylesheet"
          href="<c:url value="/resources/css/materialize.min.css"/>"
          media="screen,projection">
    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body class="blue-grey lighten-5">
    <nav class="brown" role="navigation">
        <div class="nav-wrapper container">
            <a id="logo-container" href="#" class="brand-logo">ADAP Compound Library</a>
            <ul class="right hide-on-med-and-down">
                <li><a href="#">Navbar Link</a></li>
            </ul>
            <ul id="nav-mobile" class="sidenav">
                <li><a href="#">Navbar Link</a></li>
            </ul>
            <a href="#" data-target="nav-mobile" class="sidenav-trigger"><i class="material-icons">menu</i></a>
        </div>
    </nav>

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
                            <form:input path="password"/>
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

    <!--JavaScript at end of body for optimized loading-->
    <script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/materialize.min.js"/>"></script>
</body>
</html>