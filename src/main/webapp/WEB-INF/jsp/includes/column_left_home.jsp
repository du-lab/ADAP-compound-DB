<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside>
    <nav>
        <ul>
            <li><a href="<c:url value="/" />">Home</a></li>
            <li><a href="<c:url value="/file/upload/" />">Upload File</a></li>
            <li><a href="/admin/">Admin</a></li>
            <c:if test="${userPrincipal == null}">
                <li><a href="/login/">Log-in / Sign-up</a></li>
            </c:if>
            <c:if test="${userPrincipal != null}">
                <li><a href="/account/">Account</a></li>
                <li><a href="/logout/">Log out</a></li>
            </c:if>
        </ul>
    </nav>
</aside>

<article>