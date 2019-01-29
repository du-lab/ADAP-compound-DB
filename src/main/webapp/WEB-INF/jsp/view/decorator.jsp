<?xml version="1.0" encoding="UTF-8" ?>
<%--@elvariable id="currentUser" type="org.springframework.security.core.userdetails.User"--%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<!DOCTYPE html>
<html>

<head>
    <title>ADAP Compound Library</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="icon" type="image/png" href="<c:url value="/resources/static/favicon-32x32.png"/>" sizes="32x32" />
    <link rel="icon" type="image/png" href="<c:url value="/resources/static/favicon-16x16.png"/>" sizes="16x16" />
    <link rel="stylesheet" href="<c:url value="/resources/datatables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/DataTables-1.10.16/css/jquery.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/Select-1.2.5/css/select.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.theme.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.structure.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/tag-it-6ccd2de/css/jquery.tagit.css"/>">
    <link rel="stylesheet" href="<c:url value="https://fonts.googleapis.com/icon?family=Material+Icons"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/tables.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/classes.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/plots.css"/>">
    <link rel="stylesheet" href="<c:url value="https://fonts.googleapis.com/css?family=Crimson+Text|Proza+Libre|Lato:300,400"/>">
</head>

<body>

<header>
    <h1>ADAP Spectral Library</h1>

    <c:if test="${currentUser != null}">
        <div class="user">User: ${currentUser.username} (<a href="<c:url value="/logout"/>">Log out</a>)</div>
    </c:if>
</header>

<div style="display: flex;">
    <div>
        <aside>
            <nav>
                <ul>
                    <li><a href="<c:url value="/"/>"><i class="material-icons color-primary">home</i>Home</a></li>
                    <li><a href="<c:url value="/file/upload/" />"><i
                            class="material-icons color-primary">cloud_upload</i>Upload Files</a></li>
                    <li><a href="<c:url value="/allClusters/" />"><i
                            class="material-icons color-primary">equalizer</i>Spectra</a></li>
                    <c:if test="${currentUser == null}">
                        <li><a href="<c:url value="/login/"/>"><i class="material-icons color-primary">person</i>Log-in
                            / Sign-up</a></li>
                    </c:if>
                    <c:if test="${currentUser != null}">
                        <c:if test="${dulab:isAdmin(currentUser)}">
                            <li>
                                <a href="<c:url value="/admin/" />">
                                    <i class="material-icons color-primary" style="color: red;">account_circle</i>Admin
                                </a>
                            </li>
                        </c:if>
                        <li><a href="<c:url value="/account/"/>"><i class="material-icons color-primary">account_box</i>Account</a>
                        </li>
                        <li><a href="<c:url value="/logout/"/>"><i class="material-icons color-primary">transit_enterexit</i>Log out</a>
                        </li>
                    </c:if>
                </ul>
            </nav>
        </aside>
    </div>

    <div style="width: 100%;">
        <article>
            <decorator:body/>
        </article>
    </div>
</div>

</body>

</html>