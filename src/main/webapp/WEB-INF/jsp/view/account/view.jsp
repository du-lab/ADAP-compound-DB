<%--@elvariable id="submissionList" type="java.util.List<org.dulab.adapcompounddb.models.entities.Submission>"--%>
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
                <th>Date / Time</th>
                <th>Name</th>
                <th>Properties</th>
                <th>View / Delete</th>
            </tr>
            <c:forEach items="${submissionList}" var="submission">
                <tr>
                    <td>${submission.id}</td>
                    <td><fmt:formatDate value="${submission.dateTime}" type="DATE" dateStyle="FULL"/><br/>
                        <small><fmt:formatDate value="${submission.dateTime}" type="TIME"/></small></td>
                    <td>
                        <a href="/submission/${submission.id}/">${submission.name}</a><br/>
                        <small>${dulab:abbreviate(submission.description, 80)}</small>
                    </td>
                    <td>
                        <small>${submission.chromatographyType.label}<br/>
                                ${submission.sampleSourceType.label}</small>
                    </td>
                    <td>
                        <!-- more horiz -->
                        <a href="/submission/${submission.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a>

                        <!-- delete -->
                        <a href="/submission/${submission.id}/delete/"><i class="material-icons" title="Delete">&#xE872;</i></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />