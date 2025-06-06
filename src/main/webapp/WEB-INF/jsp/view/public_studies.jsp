<%--@elvariable id="user" type="org.dulab.adapcompounddb.models.entities.UserPrincipal"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>

<div class="container">


    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Public Studies
                </div>
                <div class="card-body tab-content small">
                    <div id="studies" class="tab-pane active" role="tabpanel">
                        <table id="submission_table" class="display nowrwap" style="width: 100%;">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Date</th>
                                <th>Title</th>
                                <th>External ID</th>
                                <th>Source</th>
                                <th>Properties</th>
                            </tr>
                            </thead>
                            <tbody>
<%--                            <c:forEach items="${publicSubmissions}" var="submission">--%>

<%--                                <tr>--%>
<%--                                    <td>${submission.id}</td>--%>
<%--                                    <td><fmt:formatDate value="${submission.dateTime}" type="DATE" pattern="yyyy-MM-dd"/></td>--%>
<%--                                        &lt;%&ndash;                                    <td>${submission.name}</td>&ndash;%&gt;--%>
<%--                                    <td><a href="${pageContext.request.contextPath}/submission/${submission.id}/">${submission.name}&nbsp</a></td>--%>
<%--                                    <td>${submission.externalId}</td>--%>
<%--                                    <td>--%>
<%--                                        <c:forEach items="${submission.tags}" var="tag" varStatus="status">--%>
<%--                                            <span id="${submission.id}color${status.index}">${tag.toString()}&nbsp;</span>--%>
<%--                                            <script>--%>
<%--                                                var spanId = '${fn:length(submission.tags)}';--%>
<%--                                                spanColor(${submission.id}, spanId);--%>
<%--                                            </script>--%>
<%--                                        </c:forEach>--%>
<%--                                    </td>--%>

<%--                                </tr>--%>

<%--                            </c:forEach>--%>
                            </tbody>
                        </table>

                    </div>

                </div>
            </div>
        </div>



        <script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
        <script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
        <script src="<c:url value="/resources/jquery-ui-1.13.2/jquery-ui.min.js"/>"></script>
        <script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
        <script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
        <script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>



        <script>

            $(document).ready(function () {
                $('#submission_table').DataTable({
                    serverSide: true,
                    processing: true,
                    sortable: true,

                    ajax: {
                        type: "GET",

                        url: "${pageContext.request.contextPath}/findStudies.json",
                        data: function (d) {
                            //column index
                            d.column = d.order[0].column;
                            d.sortDirection = d.order[0].dir;
                        }
                    },

                    "columnDefs": [
                        {
                            "targets": 0,
                            "data": 'id'
                        },
                        {
                            "targets": 1,
                            "data": "formattedDate"

                        },
                        {
                            "targets": 2,
                            "data": "name",

                                render: function (data, type, row, meta) {
                                 return `<a href="${pageContext.request.contextPath}/submission/\${row.id}/">\${row.name}&nbsp</a>`;
                            }
                        },
                        {
                            "targets": 3,
                            "data": "externalId"

                        },
                        {
                            "targets": 4,
                            "data": "source"

                        },
                        {
                            "targets":5,
                            "data" : "tags",

                            render: function(data, type, row, meta){
                                const str = data.toString();
                                // return str.replaceAll(',', ', ');
                                return str.split(',').join(', ');
                            }

                        },
                    ]
                    });

            });

        </script>
