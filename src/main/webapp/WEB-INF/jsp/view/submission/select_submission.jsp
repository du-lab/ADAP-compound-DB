<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <div class="row">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Matched submissions
                </div>
                <div class="card-body">
                    <div class="small">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Species</th>
                                <th>Sample source</th>
                                <th>Disease</th>
                                <th>MS Instrument</th>
                                <th>Chromatography</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%--@elvariable id="submissions" type="java.util.List<org.dulab.adapcompounddb.models.entities.Submission>"--%>
                            <c:forEach items="${submissions}" var="submission">
                                <tr onclick="window.location='<c:url value="/submission/${submission.id}/"/>'" style="cursor: pointer">
                                    <td>${submission.name}</td>
                                    <td>${submission.getTagValue("species (common)")}</td>
                                    <td>${submission.getTagValue("sample source")}</td>
                                    <td>${submission.getTagValue("disease")}</td>
                                    <td>${submission.getTagValue("ms instrument name")}</td>
                                    <td>${submission.getTagValue("chromatography system")}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <%--                <c:forEach items="${submissions}" var="submission">--%>

            <%--                    <li class="list-group-item">--%>
            <%--                        <a href="<c:url value="/submission/${submission.id}/"/>">${submission.name}</a>--%>
            <%--                    </li>--%>
            <%--                </c:forEach>--%>
            </ul>
        </div>
    </div>
</div>