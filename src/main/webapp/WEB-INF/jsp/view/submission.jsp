<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h2>File content</h2>

    Number of spectra: ${fn:length(spectrumList)}<br/><br/>

    <div style="overflow: auto; height: 400px">
        <table>
            <tr>
                <th>Export</th>
                <th>Name</th>
            </tr>
            <c:forEach var="i" begin="0" end="${spectrumList.size()}">
                <tr>
                    <td><input type="checkbox" name="export" value="${spectrumList[i]}" checked/></td>
                    <td>
                        <a href="<c:url value="/library/submission/spectrum">
                            <c:param name="spectrumIndex" value="${i}" />
                        </c:url>">${spectrumList[i].getProperty("Name").orElse("UNKNOWN")}</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>



</section>



<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />