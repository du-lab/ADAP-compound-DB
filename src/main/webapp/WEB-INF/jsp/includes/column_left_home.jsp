<%--@elvariable id="currentUser" type="org.springframework.security.core.userdetails.User"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<aside>
    <nav>
        <ul>
            <li><a href="<c:url value="/"/>"><i class="material-icons color-primary">home</i>Home</a></li>
            <li><a href="<c:url value="/file/upload/" />"><i class="material-icons color-primary">cloud_upload</i>Upload File</a></li>
            <c:if test="${currentUser == null}">
                <li><a href="<c:url value="/login/"/>"><i class="material-icons color-primary">person</i>Log-in / Sign-up</a></li>
            </c:if>
            <c:if test="${currentUser != null}">
                <c:if test="${dulab:isAdmin(currentUser)}">
                    <li><a href="<c:url value="/admin/" />"><i class="material-icons color-primary" style="color: red;">account_circle</i>Admin</a></li>
                </c:if>
                <li><a href="<c:url value="/account/"/>"><i class="material-icons color-primary">account_box</i>Account</a></li>
                <li><a href="<c:url value="/logout/"/>"><i class="material-icons color-primary">transit_enterexit</i>Log out</a></li>
            </c:if>
        </ul>
    </nav>
</aside>

<article>