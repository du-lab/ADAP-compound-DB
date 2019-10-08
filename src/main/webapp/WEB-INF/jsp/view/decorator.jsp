<?xml version="1.0" encoding="UTF-8" ?>
<%--@elvariable id="currentUser" type="org.springframework.security.core.userdetails.User"--%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<!DOCTYPE html>
<html>

<head>
    <title>ADAP Compound Knowledgebase</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="icon" type="image/png" href="<c:url value="/resources/static/favicon-32x32.png"/>" sizes="32x32"/>
    <link rel="icon" type="image/png" href="<c:url value="/resources/static/favicon-16x16.png"/>" sizes="16x16"/>
    <link rel="stylesheet" href="<c:url value="/resources/datatables.min.css"/>">

    <link rel="stylesheet" type="text/css" media="(max-width: 480px)"
          href="<c:url value="/resources/AdapCompoundDb/css/main_mobile_portrait.css"/>">
    <link rel="stylesheet" type="text/css" media="(min-width: 481px)"
          href="<c:url value="/resources/AdapCompoundDb/css/main.css"/>">

    <link rel="stylesheet" href="<c:url value="/resources/DataTables-1.10.16/css/jquery.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/Select-1.2.5/css/select.dataTables.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.theme.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.structure.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/tag-it-6ccd2de/css/jquery.tagit.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/tag-it-6ccd2de/css/tagit.ui-zendesk.css"/>">
    <link rel="stylesheet" href="<c:url value="https://fonts.googleapis.com/icon?family=Material+Icons"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/tables.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/classes.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/plots.css"/>">
    <link rel="stylesheet"
          href="<c:url value="https://fonts.googleapis.com/css?family=Crimson+Text|Proza+Libre|Lato:300,400"/>">
    <link rel="stylesheet" href="<c:url value="/resources/tagify-master/tagify.css"/>">

    <script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
    <script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/AdapCompoundDb/js/tabs.js"/>"></script>
    <script src="http://d3js.org/d3.v4.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $(document).click(function (e) {
                var side = $(".side");
                if ($(side).hasClass("menu") && e.target.id != "menu") {
                    var menu = $(e.target).closest(".side");
                    if (menu.length == 0) {
                        $(".side").removeClass("menu");
                        animateIcon("view_headline");
                    }
                }
            });

            var animateIcon = function (content) {
                $('#menu').animate({
                    'opacity': 0
                }, 100, function () {
                    $(this).html(content).animate({'opacity': 1}, 100);
                });
            };

            $("#menu").click(function () {
                if ($("#menu").html() == 'view_headline') {
                    animateIcon("clear");
                    $(".side").addClass("menu");
                } else {
                    animateIcon("view_headline");
                    $(".side").removeClass("menu");
                }
            });
        });
    </script>

</head>

<body>

<header>

    <h1>
        <i class="material-icons mobile" title="Menu" id="menu">view_headline</i>
        ADAP Spectral Knowledgebase
        <sup>Beta</sup>
    </h1>
    <c:if test="${currentUser != null}">
        <div class="user">User: ${currentUser.username} (<a href="<c:url value="/logout"/>">Log out</a>)</div>
    </c:if>
</header>

<div style="display: flex;">
    <div class="side">
        <aside>
            <nav>
                <ul>
                    <li><a href="<c:url value="/"/>"><i class="material-icons">home</i>Home</a></li>
                    <li><a href="<c:url value="/file/upload/" />"><i
                            class="material-icons">cloud_upload</i>Upload Files</a></li>
                    <li><a href="<c:url value="/allClusters/" />"><i
                            class="material-icons">equalizer</i>Spectra</a></li>
                    <li>
                        <a href="<c:url value="/study_distributions/" />"><i class="material-icons">book</i>Distributions</a>
                    </li>
                    <c:if test="${currentUser == null}">
                        <li><a href="<c:url value="/login/"/>"><i class="material-icons">person</i>Log-in / Sign-up</a>
                        </li>
                    </c:if>
                    <c:if test="${currentUser != null}">
                        <c:if test="${dulab:isAdmin(currentUser)}">
                            <li>
                                <a href="<c:url value="/admin/" />">
                                    <i class="material-icons" style="color: red;">account_circle</i>Admin
                                </a>
                            </li>
                        </c:if>
                        <li><a href="<c:url value="/account/"/>"><i class="material-icons">account_box</i>Account</a>
                        </li>
                        <li><a href="<c:url value="/logout/"/>"><i class="material-icons">transit_enterexit</i>Log
                            out</a>
                        </li>
                    </c:if>

                    <%-- <li><a class="feedback" href="<c:url value="/feedback"/>">Leave Feedback</a></li> --%>
                </ul>
            </nav>
        </aside>
        <a class="feedback" href="<c:url value="/feedback"/>"><strong>Leave Feedback</strong></a>
    </div>

    <div style="width: 100%;">
        <article>
            <decorator:body/>
        </article>
    </div>
</div>

</body>

</html>