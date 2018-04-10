<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Account</h1>
    <p>Username: <span class="highlighted">${user.username}</span></p>
    <p>E-mail: <span class="highlighted">${user.email}</span></p>
</section>

<section>
    <h1>Submissions</h1>
    <div align="center">
        <table>
            <tr>
                <th>ID</th>
                <th>Date</th>
                <th>Time</th>
                <th>Name</th>
                <th>Description</th>
            </tr>
            <c:forEach items="${user.submissions}" var="submission">
                <tr>
                    <td>${submission.id}</td>
                    <td><fmt:formatDate value="${submission.dateTime}" type="DATE" dateStyle="FULL"/></td>
                    <td><fmt:formatDate value="${submission.dateTime}" type="TIME"/></td>
                    <td>
                        <a href="/file/view/${submission.id}">${submission.name}</a>
                    </td>
                    <td>${dulab:abbreviateString(submission.description, 80)}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />