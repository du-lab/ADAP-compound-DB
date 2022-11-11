<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<!DOCTYPE html>
<html>

<head>
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-163158069-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];

        function gtag() {
            dataLayer.push(arguments);
        }

        gtag('js', new Date());
        gtag('config', 'UA-163158069-1');
    </script>


    <title><decorator:title default="ADAP-KDB Compound Knowledgebase"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="icon" type="image/png" href="<c:url value="/resources/static/favicon-32x32.png"/>" sizes="32x32"/>
    <link rel="icon" type="image/png" href="<c:url value="/resources/static/favicon-16x16.png"/>" sizes="16x16"/>

    <link rel="stylesheet" href="<c:url value="/resources/custom-bootstrap.css"/>">
    <link rel="stylesheet"
          href="<c:url value="/resources/npm/node_modules/bootstrap4-toggle/css/bootstrap4-toggle.min.css"/>">
    <link rel="stylesheet"
          href="<c:url value="/resources/DataTables/DataTables-1.10.23/css/jquery.dataTables.min.css"/>">
    <link rel="stylesheet" type="text/css" media="(max-width: 480px)"
          href="<c:url value="/resources/AdapCompoundDb/css/main_mobile_portrait.css"/>">
    <link rel="stylesheet" type="text/css" media="(min-width: 481px)"
          href="<c:url value="/resources/AdapCompoundDb/css/main.css"/>">

    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.theme.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.structure.min.css"/>">
    <link rel="stylesheet" href="<c:url value="https://fonts.googleapis.com/icon?family=Material+Icons"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/tables.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/classes.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/plots.css"/>">
    <link rel="stylesheet"
          href="<c:url value="https://fonts.googleapis.com/css?family=Crimson+Text|Proza+Libre|Lato:300,400"/>">
    <link rel="stylesheet" href="<c:url value="/resources/tagify-master/tagify.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/SpeckTackle/st.css"/>">
    <decorator:head/>
</head>

<body>

<div class="cookie-banner" style="display: none">
    <div class="container">
        <div class="row row-content">
            <div class="col">
                By using this website, you agree to our
                <a href="${pageContext.request.contextPath}/resources/cookie-policy.txt" target="_blank">cookie
                    policy</a>.
            </div>
            <button id="closeBannerButton" class="btn btn-secondary">&times;</button>
        </div>
    </div>
</div>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script>
    if (localStorage.getItem("cookieSeen") !== "shown") {
        $(".cookie-banner").delay(2000).fadeIn();
        localStorage.setItem("cookieSeen", "shown")
    }

    $("#closeBannerButton").click(function () {
        $(".cookie-banner").fadeOut();
    })
</script>

<header  style="z-index: 100; width: 100%; position: sticky; top: 0;">
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="small text-warning text-center">
                    We are actively working on improving the ADAP-KDB website and adding new features and updates every Sunday, 1pm - 11pm GMT.
                    You may experience interruptions in the website work during that period.
                </div>
            </div>
        </div>
        <div class="row row-header">
            <div class="col-12 col-lg-8">
                <h1 class="text-nowrap">
                    <i class="material-icons mobile" title="Menu" id="menu">view_headline</i>
                    ADAP-KDB Spectral Knowledgebase
                    <sup><small class="badge badge-pill badge-light">Beta</small></sup>
                </h1>
            </div>
            <div class="col-12 col-lg-4">
                <%--@elvariable id="currentUser" type="org.springframework.security.core.userdetails.User"--%>
                <c:if test="${currentUser != null}">
                    <div class="user">User: ${currentUser.username} (<a href="<c:url value="/logout"/>">Log out</a>)
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</header>

<%--<div style="display: flex;">--%>
<div class="wrapper">
    <%--    <div class="side">--%>
    <%--        <aside>--%>
    <nav id="sidebar">
        <div class="container">
            <div class="row row-menu mb-5">
                <div class="col-12">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value="/"/>">
                                <i class="material-icons align-middle">home</i>
                                <span class="align-middle">Home</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a id="manualSearchPage" class="nav-link" href="<c:url value="/`compound/search/"/>">
                                <i class="material-icons align-middle">search</i>
                                <span class="align-middle">Search</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a id="uploadPage" class="nav-link" href="<c:url value="/file/upload/" />">
                                <i class="material-icons align-middle">cloud_upload</i>
                                <span class="align-middle">Upload Files</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a id="searchPage" class="nav-link"
<%--                               <c:if test="${sessionScope[dulab:groupSearchResultsAttributeName()] == null}">hidden</c:if>--%>
                               <c:if test = "${currentUser == null}">hidden</c:if>
                               href="<c:url value="/group_search/"/>">
                                <i class="material-icons align-middle">search</i>
                                <span class="align-middle">Group Search Results</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a id="libraryPage" class="nav-link" href="<c:url value="/libraries/" />">
                                <i class="material-icons align-middle">equalizer</i>
                                <span class="align-middle">Libraries</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value="/study_distributions/" />">
                                <i class="material-icons align-middle">book</i>
                                <span class="align-middle">Distributions</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value="/downloads/"/>">
                                <i class="material-icons align-middle">download</i>
                                <span class="align-middle">Downloads</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value="/about/"/>">
                                <i class="material-icons align-middle">help</i>
                                <span class="align-middle">Documentation</span>
                            </a>
                        </li>

                        <c:if test="${currentUser == null}">
                            <li class="nav-item">
                                <a id="loginPage" class="nav-link" href="<c:url value="/login/"/>">
                                    <i class="material-icons align-middle">person</i>
                                    <span class="align-middle">Log-in / Sign-up</span>
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${currentUser != null}">
                            <c:if test="${dulab:isAdmin(currentUser)}">
                                <li class="nav-item">
                                    <a class="nav-link" href="<c:url value="/admin/" />">
                                        <i class="material-icons align-middle" style="color: red;">account_circle</i>
                                        <span class="align-middle">Admin</span>
                                    </a>
                                </li>
                            </c:if>
                            <li class="nav-item">
                                <a id="accountPage" class="nav-link" href="<c:url value="/account/"/>">
                                    <i class="material-icons align-middle">account_box</i>
                                    <span class="align-middle">Account</span>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a id="logoutPage" class="nav-link" href="<c:url value="/logout/"/>">
                                    <i class="material-icons align-middle">transit_enterexit</i>
                                    <span class="align-middle">Log out</span>
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
            <div class="row mt-5">
                <div class="col-12">
                    <a class="feedback" href="https://forms.gle/zYPXt463DC1WjJMy8" target="_blank">
                        <strong>Leave Feedback</strong>
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <article style="margin: 0 auto; width: 100%">
        <decorator:body/>
    </article>
</div>

</body>

</html>