<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</article>

<head>

    <!-- Bootstrap core CSS -->
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/resources/css/sticky-footer-navbar.css" rel="stylesheet">

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
            <a class="navbar-brand" href="#">ADAP Compound Library</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li> <a href="<c:url value="/" />">Home</a></li>
                <li><a href="<c:url value="/file/upload/" />">Upload Sample</a></li>
                <li><a href="#about">About</a></li>
                <li><a href="#contact">Contact</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Account Owner Name <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${userPrincipal != null}">
                            <li><a href="/account/">Account</a></li>
                            <li><a href="/logout/">Log out</a></li>
                        </c:if>
                        <c:if test="${userPrincipal == null}">
                            <li><a href="/login/">Log-in / Sign-up</a></li>
                        </c:if>
                    </ul>
                </li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<!-- Begin page content -->


<footer class="footer">
    <div class="container">
        <p class="text-muted">Product of Du - Lab, 500 Laureate Way, Kannapolis, NC, USA  </p>
    </div>
</footer>


</body>
</html>