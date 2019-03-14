<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- <jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" /> --%>

<!-- Start the middle column -->

<section>
    <b>Users</b>
    <table>
        <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>E-mail</th>
        </tr>
        <c:forEach items="${users}" var="u">
            <tr>
                <td>${u.id}</td>
                <td><c:out value="${u.firstName}"/></td>
                <td><c:out value="${u.lastName}"/></td>
                <td><c:out value="${u.email}"/></td>
            </tr>
        </c:forEach>
    </table>
</section>

<!-- End the middle column -->

<%-- <jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" /> --%>
