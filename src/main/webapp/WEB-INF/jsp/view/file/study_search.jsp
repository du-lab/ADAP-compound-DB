<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section>

<div style="text-align: center">
    <table id="match_table" class="display responsive" style="width: 100%; clear:none;">
        <thead>
        <tr>
            <th title="Match submission Name">Match Submission Name</th>
            <th title="Match Score">Matching Score</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${match_submissions}" var="match_submission">
            <tr>
                <td><c:out value="${match_submission.submissionName}" /></td>
                <td><c:out value="${match_submission.score}" /></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

</section>