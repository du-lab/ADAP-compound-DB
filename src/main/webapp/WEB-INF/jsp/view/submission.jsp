<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />

<!-- Start the middle column -->

<section>
    <h1>File content</h1>
    <h2>${fileName}</h2>
    Number of spectra: ${fn:length(spectrumList)}
</section>

<section>
    <h1>Contained spectra</h1>
    <div style="overflow: auto; max-height: 400px">
        <table>
            <tr>
                <th>Export</th>
                <th>Name</th>
            </tr>
            <c:forEach var="i" begin="0" end="${spectrumList.size() - 1}">
                <tr>
                    <td><input type="checkbox" name="export" value="${spectrumList[i]}" checked/></td>
                    <td>
                        <a href="<c:url value="/spectrum">
                            <c:param name="spectrumIndex" value="${i}" />
                        </c:url>">${spectrumList[i].getProperty("Name").orElse("UNKNOWN")}</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>

<section>
    <h1>Submit</h1>
    <form method="POST" action="/library/submission">
        <label>
            <span>Comment:</span>
            <textarea name="comment" rows="5" cols="80"></textarea>
        </label><br/><br/>
        <label>
            <span>&nbsp;</span>
            <input type="button" value="Discard"/>
            <input type="submit" value="Submit"/>
        </label>
    </form>
</section>


<!-- End the middle column -->

<jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />