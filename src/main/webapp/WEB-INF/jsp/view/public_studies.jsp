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
        <c:forEach items="${publicSubmissions}" var="submission">

                <tr>
                    <td>${submission.name}</td>
                    <td>${submission.id}</td>

                </tr>


        </c:forEach>
      </table>
  </h1>
</body>
</html>
