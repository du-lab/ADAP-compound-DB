<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section>
    <h1>Consensus spectra</h1>

    <div>
        Click to hide/show columns:
        <%--        TODO: use name in data-column. E.g. data-column="count"--%>
        <label><input type="checkbox" data-column="2" checked/><strong>Count</strong></label> -
        <label><input type="checkbox" data-column="3" checked/><strong>Score</strong></label> -
        <label><input type="checkbox" data-column="4" checked/><strong>In-study P-value</strong></label> -
        <label><input type="checkbox" data-column="5" checked/><strong>Maximum Diversity</strong></label> -
        <label><input type="checkbox" data-column="6" checked/><strong>Cross-study P-value</strong></label> -
        <label><input type="checkbox" data-column="7" class="checkboxHide"/><strong>Cross-study P-value
            (disease)</strong></label> -
        <label><input type="checkbox" data-column="8" class="checkboxHide"/><strong>Cross-study P-value
            (species)</strong></label> -
        <label><input type="checkbox" data-column="9" class="checkboxHide"/><strong>Cross-study P-value (sample
            source)</strong></label> -
        <label> <input type="checkbox" data-column="10" checked/><strong>Type</strong></label>
    </div>

    <div align="center">
        <table id="cluster_table" class="display responsive" style="width: 100%;">
            <thead>
            <tr>
                <th>ID</th>
                <th title="Consensus spectrum">Spectrum</th>
                <th title="Number of studies">Count</th>
                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                <th title="P-value of the In-study ANOVA test">In-study P-value</th>
                <th title="Gini-Simpson Index">Maximum Diversity</th>
                <th title="P-value of the Cross-study Goodness-of-fit test">Cross-study P-value</th>
                <th title="P-value of disease">Cross-study P-value (disease)</th>
                <th title="P-value of species">Cross-study P-value (species)</th>
                <th title="P-value of sample source">Cross-study P-value (sample source)</th>
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
<script src="<c:url value="/resources/Select-1.2.5/js/dataTables.select.min.js"/>"></script>
<script>
    $( document ).ready( function () {
        $( '#cluster_table' ).DataTable( {
            serverSide: true,
            processing: true,
            responsive: true,
            scrollX: true,
            scroller: true,
            ajax: {
                url: "${pageContext.request.contextPath}/spectrum/findClusters.json",

                data: function (data) {
                    data.column = data.order[0].column;
                    data.sortDirection = data.order[0].dir;
                    data.search = data.search["value"];
                }
            },
            "aoColumnDefs": [
                {
                    //TODO: use column header if possible
                    "targets": 0,
                    "bSortable": false,
                    "searchable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return meta.settings.oAjaxData.start + meta.row + 1;
                    }
                },
                {
                    "targets": 1,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        content = '<a href="${pageContext.request.contextPath}/cluster/' + row.id + '/">' +
                            row.consensusSpectrumName +
                            '</a>';
                        return content
                    }
                },
                {
                    "targets": 2,
                    "bSortable": true,
                    "bVisible": true,
                    "data": "size"
                },
                {
                    "targets": 3,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        return row.diameter.toFixed( 3 ) * 1000;
                    }
                },
                {
                    "targets": 4,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.aveSignificance) {
                            var avgSignificance = row.aveSignificance.toFixed( 3 );
                            content += '<span title="{Average: ' + row.aveSignificance;
                            if (row.minSignificance) {
                                content += '; Min: ' + row.minSignificance.toFixed( 3 );
                            }
                            if (row.maxSignificance) {
                                content += '; Max: ' + row.maxSignificance.toFixed( 3 );
                            }
                            content += '}">' + avgSignificance + '</span>';
                        }
                        return content;
                    }
                },
                {
                    "targets": 5,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.maxDiversity != undefined) {
                            content = row.maxDiversity.toFixed( 3 );
                        }
                        return content;
                    }
                },
                {
                    "targets": 6,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.minPValue) {
                            content = row.minPValue.toFixed( 3 );
                            console.log( "content" );
                        }
                        return content;
                    }
                },
                {
                    "targets": 7,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.diseasePValue) {
                            content = row.diseasePValue.toFixed( 3 );
                            console.log( "content" );
                        }
                        return content;
                    },
                    "bVisible": false
                },
                {
                    "targets": 8,
                    "bSortable": true,
                    "bVisible": false,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.speciesPValue) {
                            content = row.speciesPValue.toFixed( 3 );
                            console.log( "content" );
                        }
                        return content;
                    }
                },
                {
                    "targets": 9,
                    "bSortable": true,
                    "bVisible": false,
                    "render": function (data, type, row, meta) {
                        var content = '';
                        if (row.sampleSourcePValue) {
                            content = row.sampleSourcePValue.toFixed( 3 );
                            console.log( "content" );
                        }
                        return content;
                    }
                },
                {
                    "targets": 10,
                    "bSortable": true,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '<img' +
                            ' src="${pageContext.request.contextPath}/' + row.chromatographyTypeIconPath + '"'
                            + ' alt="' + row.chromatographyTypeLabel + '"'
                            + ' title="' + row.chromatographyTypeLabel + '"'
                            + ' class="icon"/>';

                        return content;
                    }
                },
                {
                    "targets": 11,
                    "bSortable": false,
                    "bVisible": true,
                    "render": function (data, type, row, meta) {
                        var content = '<a href="${pageContext.request.contextPath}/cluster/'
                            + row.id + '/"><i class="material-icons" title="View">&#xE5D3;</i></a>';
                        return content;
                    }
                },
                {"className": "dt-center", "targets": "_all"}
            ]
        } );

        //checkbox control data column display
        $( "input:checkbox" ).click( function () {

                // table
                var table = $( '#cluster_table' ).dataTable();

                // column
                var colNum = $( this ).attr( 'data-column' );

                // Define
                //TODO; Use aoColumnDefs[] with column names
                // var bVis = table.fnSettings().aoColumns['Count'].bVisible;
                var bVis = $( this ).prop( 'checked' );

                // Toggle
                //TODO: Set show/hide property based on $(this).prop("checked")
                table.fnSetColumnVis( colNum, bVis ? true : false );


            }
        );

        // initialize checkbox mark to unchecked for column not showing at the beginning
        $( "input:checkbox" ).ready( function () {
            $( ".checkboxHide" ).prop( "checked", false );
        } );

    } );
</script>