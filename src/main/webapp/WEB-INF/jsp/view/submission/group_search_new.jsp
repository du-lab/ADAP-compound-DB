<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 2/2/23
  Time: 4:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<div class="card-body small">
  <table id="distinct_query_table" class="display compact" style="width: 100%; clear:none;">
    <thead>
    <tr>
      <th>Id</th>
      <th>Spectrum name</th>
    </tr>
    </thead>
    <tbody>
    </tbody>
  </table>

  <table id="query_table" class="display compact" style="width: 100%; clear:none;">
    <thead>
    <tr>
      <th>Id</th>
      <th>Spectrum name</th>
      <th>Precursor</th>
      <th>Number of Peaks</th>
    </tr>
    </thead>
    <tbody>
    </tbody>
  </table>
</div>
<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/d3/d3.min.js"/>"></script>
<script src="<c:url value="/resources/SpeckTackle/st.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/jdev.js"/>"></script>
<%--<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumInfo.js"/>"></script>--%>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumPlot.js"/>"></script>
<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumStructure.js"/>"></script>
<script>

  $(document).ready(function () {
    $('#distinct_query_table tbody').on( 'click', 'tr', function () {
        var data = table.row( this ).data();
        console.log(data);
        $.ajax({
          type:"POST",
          url: "${pageContext.request.contextPath}/getSpectrumsByName/data.json",
          data:{
            name: data.name
          },
          success: function(result) {
            console.log(result);
          },
          error: function(xhr, status, error) {
            console.error(xhr.responseText);
          }

        });
      });

      var table = $('#distinct_query_table').DataTable({
      // dom: 'lfrtip',

      serverSide: true,
      sortable: true,
      processing: true,
      ajax: {
        type: "GET",
        url: "${pageContext.request.contextPath}/file/group_search2/data.json",
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
          "data": "name"

        }
      ]
    });





  });
</script>


