<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<p style="padding: 10px">
    Click to hide/show columns:
    <label><input type="checkbox" data-column="2" class="show-hide"/><strong>Count</strong></label> -
    <label><input type="checkbox" data-column="3" class="show-hide"/><strong>Score</strong></label> -
    <label><input type="checkbox" data-column="4" class="show-hide"/><strong>In-study P-value</strong></label> -
    <label><input type="checkbox" data-column="5" class="show-hide"/><strong>Maximum Diversity</strong></label> -
    <label><input type="checkbox" data-column="6" class="show-hide"/><strong>Cross-study P-value</strong></label> -
    <label><input type="checkbox" data-column="7" class="show-hide"/><strong>Cross-study P-value
        (disease)</strong></label> -
    <label><input type="checkbox" data-column="8" class="show-hide"/><strong>Cross-study P-value
        (species)</strong></label> -
    <label><input type="checkbox" data-column="9" class="show-hide"/><strong>Cross-study P-value (sample
        source)</strong></label> -
    <label><input type="checkbox" data-column="10" class="show-hide"/><strong>Type</strong></label>
</p>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script>
    function columnVisibilityInit() {

        let checkBoxes = $("input:checkbox.show-hide").click(function () {
                let table = $('${param.table_id}').dataTable();
                let colNum = $(this).attr('data-column');
                let bVis = $(this).prop('checked');
                table.fnSetColumnVis(colNum, bVis);
            }
        );

        // initialize checkbox mark to unchecked for column not showing at the beginning
        checkBoxes.each(function () {
            let colNum = $(this).attr('data-column');
            if (colNum == null)
                return;

            let table = $('${param.table_id}').dataTable();
            let bVis = table.fnSettings().aoColumns[colNum].bVisible;
            $(this).prop("checked", bVis);
        });
    }
</script>