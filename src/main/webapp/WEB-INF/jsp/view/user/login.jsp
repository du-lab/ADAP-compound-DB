<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Log-in</h1>
    <p style="color: red">
        ${message}
    </p>
    <form method="post" action="<c:url value="/user/login"/>">
        <label>Username</label>
        <input type="text" name="username"/>
        <br/>
        <label>Password</label>
        <input type="password" name="password"/>
        <br/>
        <input type="submit" value="Log in"/>
    </form>
    <p>
        <a href="<c:url value="/user/add"/>">Register</a>
    </p>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />