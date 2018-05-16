<%--@elvariable id="submissionCategories" type="java.util.Map<org.dulab.adapcompounddb.models.entities.SubmissionCategory,java.lang.Integer>"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>Submission Categories</h1>
    <div align="center">
        <div>
            <table>
                <tr>
                    <th>Category</th>
                    <th>Submissions</th>
                    <th>Created By</th>
                    <th>View</th>
                </tr>
                <c:forEach items="${submissionCategories}" var="mapEntry">
                    <tr>
                        <td>${mapEntry.key.name}<br/>
                            <small>${dulab:abbreviate(mapEntry.key.description, 80)}</small>
                        </td>
                        <td>${mapEntry.value}</td>
                        <td>${mapEntry.key.user}</td>
                        <!--more horiz-->
                        <td><a href="${mapEntry.key.id}/"><i class="material-icons" title="View">&#xE5D3;</i></a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <a href="add/" class="button">Add new category...</a>
    </div>
</section>

<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />