<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ADAP Compound Library</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <!-- Bootstrap core CSS -->
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/resources/css/sticky-footer-navbar.css" rel="stylesheet">

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css"/>">

    <!-- Custom styles for this template -->
    <link rel="stylesheet" href="<c:url value="/resources/css/navbar-fixed-top.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/plots.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/datatables.min.css"/>">
    <link rel="stylesheet"
          href="<c:url value="/resources/js/DataTables/DataTables-1.10.16/css/jquery.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/js/DataTables/Select-1.2.5/css/select.dataTables.min.css"/>">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">


</head>
<body>

<!-- Fixed navbar -->
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value="/" />">ADAP Compound Library</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li> <a href="<c:url value="/" />">Home</a></li>
                <li><a href="<c:url value="/file/upload/" />">Upload Sample</a></li>
                <li><a href="<c:url value="/admin/" />">Admin</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">My Account<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${userPrincipal != null}">
                            <li><a href="/account/">Account</a></li>
                            <li><a href="/logout/">Log out</a></li>
                        </c:if>
                        <c:if test="${userPrincipal == null}">
                            <li><a href="/login/">Log-in / Sign-up</a></li>
                        </c:if><a href="/data/"></a></ul>
                <li><a href="#">About Us</a></li>
                <li><a href="#">Contact Us</a></li>
            </ul>
            </li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<header>

    <c:if test="${userPrincipal != null}">
        <div class="user">User: ${userPrincipal} (<a href="<c:url value="/logout"/>">Log out</a>)</div>
    </c:if>
</header>


