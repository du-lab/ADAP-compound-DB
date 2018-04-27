<%--@elvariable id="userPrincipal" type="org.dulab.models.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<div class="navbar-fixed">
    <!--Account dropdown structure-->
    <ul id="dropdown" class="dropdown-content">
        <li><a href="/account/">Account</a></li>
        <li><a href="#">Submissions</a></li>
        <li><a href="/logout/">Log out</a></li>
    </ul>

    <nav class="brown" role="navigation">
        <div class="nav-wrapper container">
            <a href="#" class="brand-logo">ADAP Compound Library</a>
            <ul class="right hide-on-med-and-down">
                <li><a href="#">Home</a></li>
                <li><a href="/file/upload/">File Upload</a></li>
                <c:choose>
                    <c:when test="${userPrincipal != null}">
                        <li><a href="#" class="dropdown-trigger" data-target="dropdown">
                                ${userPrincipal.username}
                            <i class="material-icons right">arrow_drop_down</i>
                        </a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/login/">Log in</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </nav>
</div>