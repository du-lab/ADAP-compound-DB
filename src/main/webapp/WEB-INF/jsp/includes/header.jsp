<%--@elvariable id="currentUser" type="org.springframework.security.core.userdetails.User"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>ADAP Compound Library</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/datatables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/js/DataTables/DataTables-1.10.16/css/jquery.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/js/DataTables/Select-1.2.5/css/select.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/plots.css"/>">
    <link rel="stylesheet" href="<c:url value="https://fonts.googleapis.com/icon?family=Material+Icons"/>">

</head>
<body>
<header>
    <h1>ADAP Compound Library</h1>
    <h2>Some information</h2>

    <c:if test="${currentUser != null}">
        <div class="user">User: ${currentUser.username} (<a href="<c:url value="/logout"/>">Log out</a>)</div>
    </c:if>
</header>