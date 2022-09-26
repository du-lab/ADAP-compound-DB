<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script src="<c:url value="/resources/AdapCompoundDb/js/tagsColor.js"/>"></script>
<div class="container">
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Consensus Spectra
                </div>
                <section class="no-background">
                    <div align="center">
                        <a id="consensusPage" href="${pageContext.request.contextPath}/allClusters/"
                           class="btn btn-primary">View All Consensus Spectra</a>
                        <a id="publicSubmissionPage" href="${pageContext.request.contextPath}/publicSubmission/"
                           class="btn btn-primary">View All Public Studies</a>
                    </div>
                </section>
            </div>
        </div>
    </div>
    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Public Libraries
                </div>
                <div class="card-body small">
                    <table id="public_libraries" class="display" style="width: 100%;">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Size</th>
                        </tr>
                        </thead>
                        <tbody>

                        <%--@elvariable id="libraries" type="java.util.List<org.dulab.adapcompounddb.models.entities.Submission>"--%>
                        <c:forEach items="${libraries}" var="study" varStatus="loop">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/submission/${study.id}/">${study.name}</a>
                                        <%--                        <small>${dulab:abbreviate(study.description, 80)}</small>--%>
                                </td>
                                <td>${dulab:abbreviate(study.description, 80)}</td>
                                <td>${study.size}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/dialogs.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script>

    $(document).ready(function () {
        $('#public_libraries').DataTable({
            responsive: true,
            scrollX: true,
            scroller: true,
            sortable: true
            /*,
                {
                    "className": "dt-center", "targets": "_all"
                }*/
        });

    });
</script>