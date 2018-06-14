<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ADAP Compound Library</title>

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css"/>">

    <!-- Custom styles for this template -->
    <link rel="stylesheet" href="<c:url value="/resources/css/navbar-fixed-top.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/datatables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/js/DataTables/DataTables-1.10.16/css/jquery.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/js/DataTables/Select-1.2.5/css/select.dataTables.min.css"/>">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>
<body>

<header>

    <c:if test="${userPrincipal != null}">
        <div class="user">User: ${userPrincipal} (<a href="<c:url value="/logout"/>">Log out</a>)</div>
    </c:if>
</header>


