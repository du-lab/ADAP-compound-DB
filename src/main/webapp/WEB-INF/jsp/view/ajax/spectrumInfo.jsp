<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="spectrum" type="org.dulab.adapcompounddb.models.entities.Spectrum"--%>

<div class="container">
    <div class="row">
        <div class="col">
            <table class="table table-sm">
                <thead>
                </thead>
                <tbody>
                <tr>
                    <th scope="row">Name</th>
                    <td>${spectrum.shortName}</td>
                </tr>
                <c:if test="${spectrum.formula != null}">
                    <tr>
                        <th scope="row">Formula</th>
                        <td>${spectrum.formula}</td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.identifiers != null && spectrum.identifiers.size() > 0}">
                    <tr>
                        <th scope="row">Identifiers</th>
                        <td>
                            <c:forEach items="${spectrum.identifiers}" var="identifier">
                                ${identifier.value} (${identifier.key}),
                            </c:forEach>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.synonyms != null && spectrum.synonyms.size() > 0}">
                    <tr>
                        <th scope="row">Synonyms</th>
                        <td>
                            <c:forEach items="${spectrum.synonyms}" var="synonym">
                                ${synonym.name},
                            </c:forEach>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.precursor != null}">
                    <tr>
                        <th scope="row">Precursor m/z</th>
                        <td>${spectrum.precursor}</td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.precursorType != null}">
                    <tr>
                        <th scope="row">Precursor type</th>
                        <td>${spectrum.precursorType}</td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.mass != null}">
                    <tr>
                        <th scope="row">Neutral mass</th>
                        <td>${spectrum.mass}</td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.retentionTime != null}">
                    <tr>
                        <th scope="row">Retention time</th>
                        <td>${spectrum.retentionTime}</td>
                    </tr>
                </c:if>
                <c:if test="${spectrum.retentionIndex != null}">
                    <tr>
                        <th scope="row">Retention index</th>
                        <td>${spectrum.retentionIndex}</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>