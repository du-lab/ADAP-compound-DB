<%--
  Created by IntelliJ IDEA.
  User: architkhullar
  Date: 6/14/18
  Time: 1:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />

<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />

<html>
<head>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
    <script src="/resources/js/bootstrap.min.js"></script>
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>
                    Oops!</h1>
                <h2>
                    404 Not Found</h2>
                <div class="error-details">
                    Sorry, an error has occured, <c:out value = "${errorMsg}"/> Requested page not found!
                </div>
                <div class="error-actions">
                    <a href="<c:url value="/" />" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-home"></span>
                        Take Me Home </a><a href="#" class="btn btn-default btn-lg"><span class="glyphicon glyphicon-envelope"></span> Contact Support </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<div>
    <section>

    </section>>
</div>
