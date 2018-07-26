<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside>
    <nav>
        <ul>
            <li><a href="<c:url value="/"/>"><i class="material-icons">home</i>Home</a></li>
            <li><a href="<c:url value="/file/upload/" />"><i class="material-icons">cloud_upload</i>Upload File</a></li>
            <li><a href="<c:url value="/admin/" />"><i class="material-icons" style="color: red;">account_circle</i>Admin</a></li>
            <c:if test="${currentUser == null}">
                <li><a href="<c:url value="/login/"/>"><i class="material-icons">person</i>Log-in / Sign-up</a></li>
            </c:if>
            <c:if test="${currentUser != null}">
                <li><a href="<c:url value="/account/"/>"><i class="material-icons">account_box</i>Account</a></li>
                <li><a href="<c:url value="/logout/"/>"><i class="material-icons">transit_enterexit</i>Log out</a></li>
            </c:if>
        </ul>
    </nav>
</aside>

<article>