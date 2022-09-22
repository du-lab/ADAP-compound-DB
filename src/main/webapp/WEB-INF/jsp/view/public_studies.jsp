<%--
  Created by IntelliJ IDEA.
  User: tnguy271
  Date: 9/22/22
  Time: 11:16 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Public Submission page</title>
</head>
<body>
  <h1>

      <table>

      </table>
      <div id="submissions" class="tab-pane" role="tabpanel">
      <div class="row row-content">
          <div class="col small">
              <table id="submission_table" class="display compact" style="width: 100%;">
                  <thead>
                  <tr>
                      <th>ID</th>
                      <th>Date / Time</th>
                      <th>Name</th>
                      <th>External ID</th>
                      <th>Properties</th>

                      <th></th>
                  </tr>
                  <c:forEach items="${publicSubmissions}" var="submission">

                  <tr>
                      <td>${submission.id}</td>
                      <td>${submission.name}</td>
                      <td>${submission.tags.tagKey}</td>
                      <td>${submission.tags.tagValue}</td>


                  </tr>


                  </c:forEach>
              </table>
          </div>
      </div>
      </div>
  </h1>
</body>
</html>
