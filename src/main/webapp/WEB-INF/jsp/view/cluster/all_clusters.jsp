<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Consensus spectra</h1>
    <div align="center">
        <table id="cluster_table" class="display" style="width: 100%;">
            <thead>
            <tr>
                <th>ID</th>
                <th title="Consensus spectrum">Spectrum</th>
                <th title="Number of spectra in a cluster">Count</th>
                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                <th title="Average, minimum, and maximum values of the statistical significance">Significance</th>
                <c:forEach items="${submissionCategoryTypes}" var="type">
                    <th>${type.label} Diversity</th>
                </c:forEach>
                <th title="Chromatography type">Type</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</section>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#cluster_table').DataTable({
            serverSide: true,
            processing: true,
            ajax: {
                url: "${pageContext.request.contextPath}/spectrum/findClusters.json",

                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                }
            },
            "columnDefs": [
                {
                    "targets": 0,
                    "orderable": true,
                    "data": "id"
                },
                {
                    "targets": 1,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        content = '<a href="${pageContext.request.contextPath}/cluster/' + row.id + '/">' +
                            row.consensusSpectrum.name +
                            '</a>';
                        return content
                    }
                },
                {
                    "targets": 2,
                    "orderable": true,
                    "data": "size"
                },
                {
                    "targets": 3,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        return row.diameter.toFixed(3);
                    }
                },
                {
                    "targets": 4,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                    	var content = '';
                        if(row.aveSignificance) {
	                    	var avgSignificance = row.aveSignificance.toFixed(3);
	                        content += '<span title="Ave: ' + row.aveSignificance;
	                        if(row.minSignificance) {
	                            content += '; Min: ' + row.minSignificance.toFixed(3);
	                        }
	                        if(row.maxSignificance) {
	                            content += '; Max: ' + row.maxSignificance.toFixed(3);
	                        }
	                        content += '}"></span>';
                            content += avgSignificance;
                        }
                        return content;
                    }
                },
                {
                    "targets": 5,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        /* var indices = row.consensusSpectrum.diversityIndices;
                        if(indices != undefined) {
	                        for(i=0; i<indices.length; i++) {
	                            if(indices[i].categoryType == 'SOURCE') {
	                                content += indices[i].diversity.toFixed(3);
	                            }
	                        }
                        }
                        return content; */
                        if(row.source != undefined) {
                            content = row.source.toFixed(3);
                        }
                        return content;
                    }
                },
                {
                    "targets": 6,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        /* var indices = row.consensusSpectrum.diversityIndices;
                        if(indices != undefined) {
		                    for(i=0; i<indices.length; i++) {
		                        if(indices[i].categoryType == 'SPECIMEN') {
		                            content += indices[i].diversity.toFixed(3);
		                        }
                            }
                        }
	                    return content; */
	                    if(row.specimen != undefined) {
	                        content = row.specimen.toFixed(3);
	                    }
	                    return content;
                    }
                },
                {
                    "targets": 7,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        /* var indices = row.consensusSpectrum.diversityIndices;
	                    if(indices != undefined) {
		                    for(i=0; i<indices.length; i++) {
		                        if(indices[i].categoryType == 'TREATMENT') {
		                            content += indices[i].diversity.toFixed(3);
		                        }
		                    }
	                    }
	                    return content; */
                        if(row.treatment != undefined) {
                            content = row.treatment.toFixed(3);
                        }
                        return content;
                    }
                },
                {
                    "targets": 8,
                    "orderable": true,
                    "render": function (data, type, row, meta) {
                    	var content = '<img' +
                        ' src="${pageContext.request.contextPath}/' + row.consensusSpectrum.chromatographyTypeIconPath + '"'
                        + ' alt="' + row.consensusSpectrum.chromatographyTypeLabel + '"'
                        + ' title="' + row.consensusSpectrum.chromatographyTypeLabel + '"'
                        + ' class="icon"/>';

                        return content;
                    }
                },
                {
                    "targets": 9,
                    "orderable": false,
                    "render": function (data, type, row, meta) {
                        var content = '<a href="${pageContext.request.contextPath}/cluster/'
                        + row.id + '/"><i class="material-icons" title="View">&#xE5D3;</i></a>';
                        return content;
                    }
                }
            ]
        });
    });
</script>