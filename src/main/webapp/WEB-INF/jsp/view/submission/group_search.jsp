<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dulab" uri="http://www.dulab.org/jsp/tld/dulab" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--@elvariable id="submissionId" type="java.lang.Long"--%>
<%--@elvariable id="submission" type="org.dulab.adapcompounddb.models.entities.Submission"--%>
<%--@elvariable id="spectrumIds" type="java.util.List<java.lang.Long>"--%>

<head>
    <link rel="stylesheet" href="<c:url value="/resources/AdapCompoundDb/css/group_search.css"/>">
</head>
<div id="progressModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Export File</h4>
                <button type="button" class="close" data-dismiss="modal">
                    &times;
                </button>
            </div>
            <div class="modal-body">
                <p>Exporting file. Please wait. This may take a while</p>
            </div>
        </div>
    </div>
</div>
<div class="container-fluid">
    <div class="row row-content">
        <div class="col">
            <div id="errorDiv" class="alert-danger"  style="margin-bottom: 5px;">
                <c:out value="${sessionScope.group_search_error}"/>
            </div>
        </div>
    </div>
    <div class="row row-content">
        <div class="col">
            <div class="btn-toolbar justify-content-end" role="toolbar">
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
                        Export
                    </button>
                    <div class="dropdown-menu">
                        <a class="dropdown-item exportLink"
                           href="${pageContext.request.contextPath}/export/session/${dulab:groupSearchResultsAttributeName()}/simple_csv"
                           title="Exports the top match for each query feature">
                            Simple export...
                        </a>
                        <a class="dropdown-item exportLink"
                           href="${pageContext.request.contextPath}/export/session/${dulab:groupSearchResultsAttributeName()}/advanced_csv"
                           title="Exports all matches for each query feature">
                            Advanced export...
                        </a>
                    </div>
                </div>
                <div class="progress flex-grow-1 align-self-center mx-2">
                    <div id="progressBar" class="progress-bar" role="progressbar" aria-valuenow="0"
                         aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <a class="btn btn-secondary mr-2" id="stopSearchBtn">Stop Search</a>
                <a class="btn btn-primary mr-2" href="<c:url value="parameters"/>">Search Parameters</a>
            </div>
        </div>
    </div>

    <div  style="display:flex; flex-wrap:wrap;">
        <div class="my-filter">
            <div class= "items">
                <span class="material-icons" style="margin-bottom: 6px;margin-right: 3px; color:#844d36; width:25px; height:25px">filter_list</span>
                <h5>Filters: </h5>
            </div>
            <div class="custom-control custom-switch items" >
                <input type="checkbox" class="custom-control-input" id="matchesOnly" checked/>
                <label class="custom-control-label" for="matchesOnly" style="margin-bottom: 0;">Show only results with matches</label>
            </div>
            <div class= "items"  >
                <label for="ontologyLevel" >Ontology level</label>
                <select id="ontologyLevel" class="form-control" style="width:auto;"></select>
            </div>
            <div class = "items">
                <label for="scoreThreshold" title="Results with score above the threshold will be shown" >Score Threshold</label>
                <input type="number" step="any" id="scoreThreshold" placeholder="0-1000" class="form-control item-textbox"/>
            </div>
            <div class = "items">
                <label for="massError" title="Results with mass error below given value will be shown" >Mass Error Tolerance (Da)</label>
                <input type="number" step="any" id="massError" class="form-control item-textbox"/>
            </div>
            <div class = "items">
                <label for="retTimeError" title="Results with retention time error below given value will be shown" >Retention Time Error Tolerance (min)</label>
                <input type="number" step="any" id="retTimeError" class="form-control item-textbox"/>
            </div>

            <div class = "items">
                <label for="matchName" style="margin-right:10px;">Match Name </label>
                <input type="text" id="matchName" class="form-control match-input item-textbox"/>
            </div>

                <button class="btn btn-secondary" type="button" id="resetFilterBtn">Clear All Filters</button>
                <%--                <button class="btn btn-primary" type="button" id="applyFilterBtn">Filter</button>--%>

        </div>
    </div>


    <div class = "card" style="margin-left: 5px;margin-right: 5px;">
        <div class="card-header card-header-single">
            <button class="btn btn-sm btn-link" type="button" data-toggle="collapse" data-target="#query-plot-match-panel" aria-expanded="false" aria-controls="query-plot-match-panel">
                <span class="material-icons" style="color: white;width: 16px;height: 16px;">arrow_drop_down</span>
            </button>
            Match Details
        </div>
        <div class ="card-body collapse" id = "query-plot-match-panel">
            <div class="row row-content " id="query_plot_match_row" style="height: auto">
                <div class="col-4" id = "query_content">
                    <div class="card" style="height: auto">
                        <div id = "queryPanel" >
                            <div class = "card header card-header-single query-plot-match-title">
                                Query
                            </div>
                            <div class="card-body card-body-compact small overflow-auto" style="height: auto">
                                <div id="queryInfo"></div>
                            </div>
                            <div  id="queryColumn">
                                <div >
                                    <%--                        <div class="card-header card-header-single">Query Structure</div>--%>
                                    <div class="overflow-auto" style="height: auto">
                                        <div id="queryStructure" class="d-flex justify-content-center h-100"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-4" id = "plot_content">
                    <div class="card" style="height: auto">
                        <div id="plotPanel">
                            <div class = "card header card-header-single query-plot-match-title">
                                Plot
                            </div>
                            <%--                <div class="card-body small overflow-auto" style="height: 300px">--%>
                            <div id = "bar_under_plot" class="card-body card-body-compact small overflow-auto" style="height: 280px">
                                <div id="plot"  style="min-height: 280px"></div>
                            </div>
                        </div>
                        <%--                </div>--%>
                    </div>
                </div>
                <div class="col-4" id = "match_content">
                    <div class="card" style="height: auto">
                        <div id = "matchPanel">
                            <div class = "card header card-header-single query-plot-match-title">
                                Match
                            </div>
                            <div class="card-body card-body-compact small overflow-auto" style="height: auto">
                                <div id="matchInfo"></div>
                            </div>
                            <div  id="matchColumn">
                                <div class="overflow-auto" style="height: auto">
                                    <div id="matchStructure" class="d-flex justify-content-center h-100"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                 </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col query_signals">
            <div class="card">
                <div class="card-header card-header-single">
                    Query Signals
                </div>

                <div class="card-body small container " style="overflow: scroll">
                    <div class = "distinct_query_container">
                        <table  id = "distinct_query_table" class="display compact " style="width: 100%; clear:none;">
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Query</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>
        <div class="col query_spectra">
            <div class="card">
                <div class="card-header card-header-single">
                    Query Spectra
                </div>
                <div class="card-body small container " style="overflow: scroll">

                    <div class ="query_container">
                        <table id="query_table" class="display compact query_table" style="width: 100%; clear:none;">
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Spectrum name</th>
                                <th>External Id</th>
                                <th>Precursor Mzs</th>
                                <th>Retention Time</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="col matches">
            <div class="card">
                <div class="card-header card-header-single">
                    Matches
                </div>
                <div class="card-body small container " style="overflow: scroll">

                    <div class = "match_container">
                        <table id="match_table" class="display compact table-right" style="width: 100%; clear:none;">
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Query</th>
                                <th title="Match spectra">Match</th>
                                <th title="Molecular weight">Molecular weight</th>
                                <th title="Number of studies" class="Count">Sources</th>
                                <th title="Minimum matching score between all spectra in a cluster">Score</th>
                                <th title="Difference between query and library neutral masses">Mass Error (mDa)</th>
                                <th title="Difference between query and library neutral masses">Mass Error (PPM)</th>
                                <th title="Difference between query and library retention times">Ret Time Error</th>
                                <th title="Difference between query and library retention indices">Ret Index error</th>
                                <th title="Isotopic similarity">Iso Similarity</th>
                                <th title="Average P-value of ANOVA tests">Average P-value</th>
                                <th title="Minimum P-value of ANOVA tests">Minimum P-value</th>
                                <th title="Maximum P-value of ANOVA tests">Maximum P-value</th>
                                <th title="Ontology level">Ontology Level</th>
                                <th title="Spectrum type">Type</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>




    <script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
    <script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
    <script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
    <script src="<c:url value="/resources/DataTables/DataTables-1.10.23/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/resources/DataTables/Select-1.3.1/js/dataTables.select.min.js"/>"></script>
    <script src="<c:url value="/resources/npm/node_modules/d3/d3.min.js"/>"></script>
    <script src="<c:url value="/resources/SpeckTackle/st.js"/>"></script>
    <script src="<c:url value="/resources/AdapCompoundDb/js/jdev.js"/>"></script>
    <%--<script src="<c:url value="/resources/AdapCompoundDb/js/spectrumInfo.js"/>"></script>--%>
    <script src="<c:url value="/resources/AdapCompoundDb/js/spectrumPlot.js"/>"></script>
    <script src="<c:url value="/resources/AdapCompoundDb/js/spectrumStructure.js"/>"></script>
    <script>
  var isSavedResultPage = '<c:out value="${submissionId}" />' ? true : false;
  $(document).ready(function () {
        var drawFirstTime = true;
        var isExportDone = true;
        //filter parameters
        var url;
        var showMatchesOnly = 1;
        var ontologyLevel = "";
        var scoreThreshold;
        var massError;
        var reTimeError;
        var matchName ="";
        var selectedRowIndex = null; // save the index of the selected row

        // //hide the match details by default
        // $('#query_plot_match_row').hide();
        // $('.query_container').hide();
        // $('.match_container').hide();

        function getColumnStr(data){
          var result = [];
          for (let i = 0; i < data.order.length; i++) {
            result += data.order[i].column + "-" + data.order[i].dir + ",";
          }
          return result;
        }

      $("#stopSearchBtn").click(function (event) {
          event.preventDefault();
          $.ajax({
              url: "/group_search/stop",
              method: "GET",
              success: function (data) {
                  console.log("Search stopped");
              },
              error: function (xhr, status, error) {
                  console.error("Error while stopping search:", error);
              }
          });
      });
        //update the ontology level options
        $.ajax({
          url: "${pageContext.request.contextPath}/getOntologyLevels",
          type: "GET",
          data: {
            submissionId: '${submissionId}',
            isSavedResultPage: isSavedResultPage
          },
          success: function(response) {
            var ontologyLevelOptions = $("#ontologyLevel");
            // ontologyLevelOptions.find('option').not(':selected').remove();
            ontologyLevelOptions.empty();
            ontologyLevelOptions.append($('<option></option>'));
            $.each(response, function(index, item) {
              ontologyLevelOptions.append($('<option></option>').val(item).text(item));
            });
          },
          error: function(xhr) {
            console.log("Error:", xhr);
          }
        });

        if (isSavedResultPage)
          url = "${pageContext.request.contextPath}/file/group_search_matches/${submissionId}/data.json";
        else
          url = "${pageContext.request.contextPath}/distinct_spectra/data.json";

        function initializeTable(){
          var distinct_query_table = $('#distinct_query_table').DataTable({
            // dom: 'lfrtip',

            serverSide: true,
            sortable: true,
            order: [[0, 'desc']],
            processing: true,
            responsive: true,
            info: false,
            select: {style: 'single', info: false},
            searching:false,
            scrollX: true,

            ajax: {
              url:  url,
              data: function (data) {
                data.columnStr = getColumnStr(data);
                data.search = data.search["value"];
                //filter values
                data.matchFilter = showMatchesOnly;
                data.ontologyLevel = ontologyLevel;
                data.scoreThreshold= scoreThreshold;
                data.massError= massError;
                data.retTimeError=reTimeError;
                data.matchName=matchName;
                console.log(data);
              }

            },
            error: function(xhr, error) {
              console.log(xhr);
              console.log("Error: ", error);
            },
            columns: [
              {
                data: function(row, type,val, meta) {
                  return meta.row + 1;
                }
              },
              {
                data: "querySpectrumName"
              }
            ],
            "drawCallback": function() {
              $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
              if(distinct_query_table.data().count() ===0){
                // $('#query_table').DataTable().clear().draw();
                // $('#match_table').DataTable().clear().draw();
                $('.query_container').hide()
                $('.match_container').hide()
              }
              else
              {
                if(drawFirstTime) {
                  //select first row by default
                  distinct_query_table.row(':eq(0)').nodes().to$().trigger('click');
                  drawFirstTime=false;
                }
              }
            },
            rowId: 'position'
          });
        }
        initializeTable();

        // distinct_query_table.on('select', function ( e, dt, type, indexes ) {
        //     let query = distinct_query_table.rows().data()[indexes];
        $('#distinct_query_table tbody').on( 'click', 'tr', function () {
          //remove any previous selected row
          $('#distinct_query_table').DataTable().$('tr.selected').removeClass('selected');
          $(this).addClass('selected');

          // save the index of the selected row
          selectedRowIndex = $('#distinct_query_table').DataTable().row(this).index();

          var query = $('#distinct_query_table').DataTable().row( this ).data();

          console.log("SELECTED ROW:", query);
          //reset table data each time new row is clicked

          $('#query_table').DataTable().destroy();

          //show query table, match table with default matches.
          if(isSavedResultPage){
            var query_table = $('#query_table').DataTable({
              serverSide: true,
              sortable: true,
              processing: true,
              order: [[0, 'desc']],
              responsive: true,
              info: false,
              select: {style: 'single', info: false},
              searching:false,
              scrollX: true,
              rowId: 'spectrumId',
              ajax: {
                type:"GET",
                url: "${pageContext.request.contextPath}/getSpectraForSavedResultPage",
                data: function (data) {
                  data.columnStr = data.columnStr = getColumnStr(data);
                  data.search = data.search["value"];
                  data.querySpectrumName = query.querySpectrumName;
                  data.matchFilter = showMatchesOnly;
                  data.ontologyLevel = ontologyLevel;
                  data.scoreThreshold= scoreThreshold;
                  data.massError= massError;
                  data.retTimeError=reTimeError;
                  data.matchName=matchName;

                  console.log("AJAX QUERY TABLE", data);
                }
              },
              success: function(response){
                //console.log(response);
              },

              destroy: true,

              columns: [
                {
                  data: function(row, type,val, meta) {
                    return meta.row + 1;
                  }
                },
                {
                  data: "querySpectrumName"
                },
                {
                  data: "queryExternalId"
                },
                {
                  data: function(row, type,val, meta) {
                    if (row.queryPrecursorMzs == null)
                      return null;
                    roundedArray = row.queryPrecursorMzs.map(function(e){
                      return Number(e.toFixed(3));
                    });

                    return roundedArray;
                  }
                },
                {
                  data: row => row.queryRetTime != null ? row.queryRetTime.toFixed(3) : ''
                }
              ]
              ,
              "initComplete": function(){
                //alert('Data loaded successfully');
                //default choose the first row in query table
                $('.query_container').show();
                $('#query_table').DataTable().rows(0).select();
              }

            });
          }
          else{
            //TODO: simiplify this funtion?
            //display spectrum for selected query
            $.ajax({
              type:"POST",
              url: "${pageContext.request.contextPath}/getSpectrumsByName",
              contentType:'application/json',
              dataType:"json",
              data:  JSON.stringify(query),
              success: function(result) {
                //create a new datatable
                console.log(result);

                var query_table = $('#query_table').DataTable({
                  serverSide: true,
                  sortable: true,
                  processing: true,
                  order: [[0, 'desc']],
                  responsive: true,
                  info: false,
                  select: {style: 'single', info: false},
                  searching:false,
                  scrollX: true,
                  rowId: 'position',
                  ajax: {
                    type: "GET",
                    url: "${pageContext.request.contextPath}/spectra/data.json",
                    data: function (data) {
                      //column index
                      data.columnStr = data.columnStr = getColumnStr(data);
                      console.log(data);
                      data.search = data.search["value"];
                    }

                  },
                  destroy: true,

                  columns: [
                    {
                      data: function(row, type,val, meta) {
                        return meta.row + 1;
                      }
                    },
                    {
                      data: "querySpectrumName"
                    },
                    {
                      data: "queryExternalId"
                    },
                    {
                      data: function(row, type,val, meta) {
                        if(row.queryPrecursorMzs == null)
                          return ''
                        roundedMzs = row.queryPrecursorMzs.map(function(e){
                          return Number(e.toFixed(3));
                        });

                        return roundedMzs;
                      }
                    },
                    {
                      data: row => row.queryRetTime != null ? row.queryRetTime.toFixed(3) : ''
                    }
                  ],

                  "initComplete": function(){
                    //alert('Data loaded successfully');
                    //default choose the first row in query table
                    $('.query_container').show();
                    $('#query_table').DataTable().rows(0).select();
                  }
                });

              },
              error: function(xhr, error) {
                console.log(xhr);
                console.log("Error: ", error);
              }
            });
          }
          $('.query_container').show();

        });
        $('#query_table').DataTable().on('select', function (e, dt, type, indexes) {
          var  match = true;
          var spectrumData = $('#query_table').DataTable().rows( ).data()[indexes];
          console.log("QUERY TABLE DATA: " ,spectrumData);

          //reset table data each time new row is clicked
          $('#match_table').DataTable().destroy();
          // $('#match_table').show();

          if(isSavedResultPage){
            let match_table = $('#match_table').DataTable({

              serverSide: true,
              order: [[0, 'desc']],
              processing: true,
              responsive: true,
              info: false,
              scrollX: true,
              searching: false,
              select: {style: 'single', info: false},
              scroller: true,
              rowId: 'spectrumId',
              ajax: {
                url: "${pageContext.request.contextPath}/findMatchesSavedResultPage/data.json",
                data: function (data) {

                  data.columnStr = data.columnStr = getColumnStr(data);

                  data.search = data.search["value"];
                  data.spectrumId = spectrumData.querySpectrumId;
                  data.matchId = spectrumData.spectrumId;
                  console.log(data);
                  <%--console.log(${spectrumIds});--%>
                  <%--data.spectrumIds = ${spectrumIds}--%>
                },
                dataSrc: function (d) {
                  // Hide columns with no data
                  match_table.column(3).visible(d.data.map(row => row['mass']).join(''));
                  match_table.column(4).visible(d.data.map(row => row['size']).join(''));
                  // table.column(5).visible(d.data.map(row => row['score']).join(''));
                  // table.column(6).visible(d.data.map(row => row['massError']).join(''));
                  // table.column(7).visible(d.data.map(row => row['massErrorPPM']).join(''));
                  // table.column(8).visible(d.data.map(row => row['retTimeError']).join(''));
                  // table.column(9).visible(d.data.map(row => row['retIndexError']).join(''));
                  match_table.column(11).visible(d.data.map(row => row['aveSignificance']).join(''));
                  match_table.column(12).visible(d.data.map(row => row['minSignificance']).join(''));
                  match_table.column(13).visible(d.data.map(row => row['maxSignificance']).join(''));
                  return d.data;
                },
                error: function (xhr, error, code) {
                  logging.logToServer('<c:url value="/js-log"/>', `\${xhr.status} - \${error} - \${code}`);
                }
              },
              fnCreatedRow: function (row, data, dataIndex) {

                $(row).attr('data-position', dataIndex);
                $(row).attr('data-matchId', data.spectrumId);
                $(row).attr('data-queryHRef', data.queryHRef);
                $(row).attr('data-queryId', data.querySpectrumId);
                $(row).attr('data-queryFileIndex', data.queryFileIndex);
                $(row).attr('data-querySpectrumIndex', data.querySpectrumIndex);
              },
              columns: [
                {data: function(row, type,val, meta) {
                    return meta.row + 1;
                  }},
                {
                  data: function (row) {
                    const href = (row.querySpectrumId !== 0)
                        ? `<c:url value="/spectrum/\${row.querySpectrumId}/"/>`
                        : `<c:url value="/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/"/>`;
                    return `<a href="\${href}">\${row.querySpectrumName}</a>`;
                  },
                  visible: false

                },
                {
                  data: row => {
                    let string = '';
                    if(row.name == null){
                      match=false;
                      $('.match_container').hide();
                      $('#query_plot_match_row').hide();
                      return string;

                    }
                    else if (row.name != null)
                      string += `<a href="<c:url value="/\${row.href}" />">\${row.name}</a>`;
                    if (row.errorMessage != null)
                      string += `<span class="badge badge-danger" title="\${row.errorMessage}">ERROR</span>`
                    return string;
                  }
                },
                {data: row => (row.mass != null) ? row.mass.toFixed(3) : ''},
                {data: row => (row.size != null) ? row.size : ''},
                {data: row => (row.score != null) ? row.score.toFixed(3) * 1000 : ''},
                {data: row => (row.massError != null) ? (1000 * row.massError).toFixed(3) : ''},
                {data: row => (row.massErrorPPM != null) ? row.massErrorPPM.toFixed(3) : ''},
                {data: row => (row.retTimeError != null) ? row.retTimeError.toFixed(3) : ''},
                {data: row => (row.retIndexError != null) ? row.retIndexError.toFixed(1) : ''},
                {data: row => (row.isotopicSimilarity != null) ? row.isotopicSimilarity.toFixed(3) * 1000 : ''},
                {data: row => (row.aveSignificance != null) ? row.aveSignificance.toFixed(3) : ''},
                {data: row => (row.minSignificance != null) ? row.minSignificance.toFixed(3) : ''},
                {data: row => (row.maxSignificance != null) ? row.maxSignificance.toFixed(3) : ''},
                {data: 'ontologyLevel'},
                {
                  data: row => (row.chromatographyTypeLabel != null)
                      ? `<span class="badge badge-secondary">\${row.chromatographyTypeLabel}</span>` : ''
                },
                {
                  data: function (row) {
                    const href = (row.querySpectrumId !== 0)
                        ? `<c:url value="/spectrum/\${row.querySpectrumId}/search/"/>`
                        : `<c:url value="/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/search/"/>`;
                    return `<a href="\${href}"><i class="material-icons" title="Search spectrum">&#xE8B6;</i></a>`;
                  }
                }
              ]
              ,"initComplete": function() {
                //alert('Data loaded successfully');

                //adjust columns of all tables displayed on the page.
                $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
                //default choose the first row in query table
                if(match) {
                  $('.match_container').show();
                  $('#match_table').DataTable().rows(0).select();
                  $('#query_plot_match_row').show();
                }
              }
            });
          }
          //TODO: simiplify this funtion
          else{
            $.ajax({
              type:"POST",
              url: "${pageContext.request.contextPath}/getMatches",
              contentType:'application/json',
              dataType:"json",
              data:  JSON.stringify(spectrumData),
              success: function(result) {

                console.log("**MATCHES: " ,result);

                let match_table = $('#match_table').DataTable({

                  serverSide: true,
                  order: [[0, 'desc']],
                  processing: true,
                  responsive: true,
                  info: false,
                  scrollX: true,
                  destroy: true,
                  searching: false,
                  select: {style: 'single', info: false},
                  scroller: true,
                  rowId: 'position',
                  ajax: {
                    url: "${pageContext.request.contextPath}/file/group_search/data.json",
                    data: function (data) {

                      data.columnStr = data.columnStr = getColumnStr(data);
                      console.log(data);
                      data.search = data.search["value"];

                      <%--console.log(${spectrumIds});--%>
                      <%--data.spectrumIds = ${spectrumIds}--%>
                    },
                    dataSrc: function (d) {
                      // Hide columns with no data
                      match_table.column(3).visible(d.data.map(row => row['mass']).join(''));
                      match_table.column(4).visible(d.data.map(row => row['size']).join(''));
                      // table.column(5).visible(d.data.map(row => row['score']).join(''));
                      // table.column(6).visible(d.data.map(row => row['massError']).join(''));
                      // table.column(7).visible(d.data.map(row => row['massErrorPPM']).join(''));
                      // table.column(8).visible(d.data.map(row => row['retTimeError']).join(''));
                      // table.column(9).visible(d.data.map(row => row['retIndexError']).join(''));
                      match_table.column(11).visible(d.data.map(row => row['aveSignificance']).join(''));
                      match_table.column(12).visible(d.data.map(row => row['minSignificance']).join(''));
                      match_table.column(13).visible(d.data.map(row => row['maxSignificance']).join(''));
                      return d.data;
                    },
                    error: function (xhr, error, code) {
                      logging.logToServer('<c:url value="/js-log"/>', `\${xhr.status} - \${error} - \${code}`);
                    }
                  },
                  fnCreatedRow: function (row, data, dataIndex) {

                    $(row).attr('data-position', dataIndex);
                    $(row).attr('data-matchId', data.spectrumId);
                    $(row).attr('data-queryHRef', data.queryHRef);
                    $(row).attr('data-queryId', data.querySpectrumId);
                    $(row).attr('data-queryFileIndex', data.queryFileIndex);
                    $(row).attr('data-querySpectrumIndex', data.querySpectrumIndex);
                  },
                  columns: [
                    {data: function(row, type,val, meta) {
                        return meta.row + 1;
                      }},
                    {
                      data: function (row) {
                        const href = (row.querySpectrumId !== 0)
                            ? `<c:url value="/spectrum/\${row.querySpectrumId}/"/>`
                            : `<c:url value="/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/"/>`;
                        return `<a href="\${href}">\${row.querySpectrumName}</a>`;
                      },
                      visible: false
                    },
                    {
                      data: row => {
                        let string = '';
                        //if there's no match
                        if(row.name == null){
                          match=false;
                          $('.match_container').hide();
                          $('#query_plot_match_row').hide();
                          return string;
                        }
                        else if (row.name != null){
                          string += `<a href="<c:url value="/\${row.href}" />">\${row.name}</a>`;
                        }
                        if (row.errorMessage != null)
                          string += `<span class="badge badge-danger" title="\${row.errorMessage}">ERROR</span>`

                        return string;
                      }
                    },
                    {data: row => (row.mass != null) ? row.mass.toFixed(3) : ''},
                    {data: row => (row.size != null) ? row.size : ''},
                    {data: row => (row.score != null) ? row.score.toFixed(3) * 1000 : ''},
                    {data: row => (row.massError != null) ? (1000 * row.massError).toFixed(3) : ''},
                    {data: row => (row.massErrorPPM != null) ? row.massErrorPPM.toFixed(3) : ''},
                    {data: row => (row.retTimeError != null) ? row.retTimeError.toFixed(3) : ''},
                    {data: row => (row.retIndexError != null) ? row.retIndexError.toFixed(1) : ''},
                    {data: row => (row.isotopicSimilarity != null) ? row.isotopicSimilarity.toFixed(3) * 1000 : ''},
                    {data: row => (row.aveSignificance != null) ? row.aveSignificance.toFixed(3) : ''},
                    {data: row => (row.minSignificance != null) ? row.minSignificance.toFixed(3) : ''},
                    {data: row => (row.maxSignificance != null) ? row.maxSignificance.toFixed(3) : ''},
                    {data: 'ontologyLevel'},
                    {
                      data: row => (row.chromatographyTypeLabel != null)
                          ? `<span class="badge badge-secondary">\${row.chromatographyTypeLabel}</span>` : ''
                    },
                    {
                      data: function (row) {
                        const href = (row.querySpectrumId !== 0)
                            ? `<c:url value="/spectrum/\${row.querySpectrumId}/search/"/>`
                            : `<c:url value="/file/\${row.queryFileIndex}/\${row.querySpectrumIndex}/search/"/>`;
                        return `<a href="\${href}"><i class="material-icons" title="Search spectrum">&#xE8B6;</i></a>`;
                      }
                    }
                  ],
                  "initComplete": function(){
                    //alert('Data loaded successfully');
                    //adjust column
                    $.fn.dataTable.tables({visible: true, api: true}).columns.adjust();
                    //default choose the first row in query table
                    if(match) {
                      $('.match_container').show();
                      $('#match_table').DataTable().rows(0).select();
                      $('#query_plot_match_row').show();
                    }
                  }
                });

              },
              error: function(xhr, error) {
                console.log(xhr);
                console.log("Error: ", error);
              }
            });
          }
          //$('.match_container').show();
        });

        //console.log(table);
        let previousQueryUrl = null;
        let previousMatchUrl = null;

        $('#match_table').DataTable().on('select', function (e, dt, type, indexes) {

          let row = $('#match_table').DataTable().row(indexes).node();
          let position = $(row).attr('data-position');
          let queryHRef = $(row).attr('data-queryHRef');
          let queryId = $(row).attr('data-queryId');
          let queryFileIndex = $(row).attr('data-queryFileIndex');
          let querySpectrumIndex = $(row).attr('data-querySpectrumIndex');
          let matchId = $(row).attr('data-matchId');

          <%--let queryUrl = `${pageContext.request.contextPath}/file/\${queryFileIndex}/\${querySpectrumIndex}/search/`;--%>
          let queryUrl = `${pageContext.request.contextPath}\${queryHRef}search/`;
          let matchUrl = `${pageContext.request.contextPath}/spectrum/\${matchId}/search/`;

          if (queryUrl === previousQueryUrl && matchUrl === previousMatchUrl)
            return;

          $.ajax({
            url: (isSavedResultPage) ? (`${pageContext.request.contextPath}/ajax/spectrum/info?spectrumId=\${queryId}`)
                : (`${pageContext.request.contextPath}/ajax/spectrum/info?spectrumId=\${queryId}&fileIndex=\${queryFileIndex}&spectrumIndex=\${querySpectrumIndex}`),
            success: d => $('#queryInfo').html(d)
          })

          $.ajax({
            url: `${pageContext.request.contextPath}/ajax/spectrum/info?spectrumId=\${matchId}`,
            success: d => $('#matchInfo').html(d)
          })

          // $('#queryInfo').spectrumInfo(queryUrl + 'info.json');
          // $('#matchInfo').spectrumInfo(matchUrl + 'info.json');
          $('#query-plot-match-panel').collapse('show');
          $('#plot').spectrumPlot(position, queryUrl + 'positive/peaks.json', matchUrl + 'negative/peaks.json',
              function(complete){
                if(complete) {
                  //reset styles
                  $('#query_content').css('padding-right', '')
                  $('#match_content').css('padding-left', '')
                  $('#query_content').removeClass('col').addClass('col-4')
                  $('#match_content').removeClass('col').addClass('col-4')
                }
                else
                {
                  $('#plot_content').hide();
                  $('#query_content').css('padding-right', '0px')
                  $('#query_content').addClass('col').removeClass('col-4')
                  $('#match_content').addClass('col').removeClass('col-4')
                  $('#match_content').css('padding-left', '0px')
                }
              }
          );
          $('#queryStructure').spectrumStructure(queryUrl + 'structure.json', function (x) {
            $('#queryColumn').attr('hidden', !x);
          });
          $('#matchStructure').spectrumStructure(matchUrl + 'structure.json', function (x) {
            $('#matchColumn').attr('hidden', !x);
          });
          previousQueryUrl = queryUrl;
          previousMatchUrl = matchUrl;

          // show the query, plot and match div
          $('#query_plot_match_row').show();

        });


        // refresh the datatable and progress bar every 1 second
        var previousResponse;

        setInterval(function () {
          checkExportStatus();
          if (!isSavedResultPage){
          //update the ontology level options
          $.ajax({
            url: "${pageContext.request.contextPath}/getOntologyLevels",
            type: "GET",
            data: {
              submissionId: '${submissionId}',
              isSavedResultPage: isSavedResultPage
            },
            success: function(response) {
              var responseString = JSON.stringify(response);
              //don't update the options if they're the same
              if(responseString !== previousResponse) {
                var ontologyLevelOptions = $("#ontologyLevel");
                var selectedOption = ontologyLevelOptions.find(':selected').val();
                ontologyLevelOptions.empty();
                ontologyLevelOptions.append($('<option></option>'));
                $.each(response, function (index, item) {
                  ontologyLevelOptions.append($('<option></option>').val(item).text(item));
                });
                ontologyLevelOptions.val(selectedOption);
                previousResponse = responseString;
              }
            },
            error: function(xhr) {
              console.log("Error:", xhr);
            }
          });

            $.ajax({
              url: `${pageContext.request.contextPath}/ajax/group_search/error`,
              success: d => $('#errorDiv').html(d)
            });
            // if($('#progressBar').attr('aria-valuenow') < 100)
            $('#distinct_query_table').DataTable().ajax.reload(function(){
                if (selectedRowIndex !== null) {
                    var selectedRow = $('#distinct_query_table').DataTable().row(selectedRowIndex).node();
                    $(selectedRow).addClass('selected');
                }
            },false);

            $.getJSON(window.location.origin + window.location.pathname + 'progress', function (x) {
              const width = x + '%';
              const progressBar = $('#progressBar')
              .css('width', width)
              .attr('aria-valuenow', x)
              .html(width);
              if (0 < x && x < 100)
                progressBar.addClass('progress-bar-striped progress-bar-animated');
              else {
                progressBar.removeClass('progress-bar-striped progress-bar-animated');
              }
            });
          }
    }, 1000);
        <%--        <c:if test="${pageContext.request.method == 'GET'}">$('#filterForm').submit();--%>
        <%--        </c:if>--%>

        $('#resetFilterBtn').click(function(){
          $('#matchesOnly').prop('checked', false);
          $('#ontologyLevel').val(null);
          $('#scoreThreshold').val('');
          $('#massError').val('');
          $('#retTimeError').val('');
          $('#matchName').val(null);

          showMatchesOnly =$('#matchesOnly').is(":checked") ? 1 : 0;
          ontologyLevel = $('#ontologyLevel').val();
          scoreThreshold = $('#scoreThreshold').val();
          massError = $('#massError').val();
          reTimeError = $('#retTimeError').val()
          matchName = $('#matchName').val();

          $('.query_container').hide();
          $('.match_container').hide();
          $('#distinct_query_table').DataTable().destroy();
          drawFirstTime=true;
          initializeTable();
        })

        //update filter parameters when user change the filter
        $(' #matchesOnly, #ontologyLevel').change(function(){
          drawFirstTime = true;
          updateFilterParams();
        })
        $('.item-textbox, #matchName').on('input', function(){
          drawFirstTime = true;
          updateFilterParams();
        })
        $('.exportLink').click(function() {
          isExportDone = false;
          $('#progressModal').modal('show');
        });
        function updateFilterParams(){
          showMatchesOnly =$('#matchesOnly').is(":checked") ? 1 : 0;
          ontologyLevel = $('#ontologyLevel').val();

          if(!$('#scoreThreshold')[0].checkValidity() || !$('#massError')[0].checkValidity()
              ||!$('#retTimeError')[0].checkValidity()) {
            alert("Please enter number only");
            return;
          }
          else{
            scoreThresholdInput = $('#scoreThreshold').val();
            massErrorInput = $('#massError').val();
            retTimeErrorInput = $('#retTimeError').val()

            //check for empty input
            scoreThreshold = scoreThresholdInput ==="" ? null : parseFloat(scoreThresholdInput)/1000;
            massError = massErrorInput==="" ? null : parseFloat($('#massError').val());
            reTimeError = retTimeErrorInput ==="" ? null : parseFloat($('#retTimeError').val());
          }

          matchName = $('#matchName').val();

          $('.query_container').hide();
          $('.match_container').hide();
          $('#distinct_query_table').DataTable().destroy();
          initializeTable();
        }
        function checkExportStatus(){
          if(!isExportDone){
            //update export status
            $.ajax({
              url: `${pageContext.request.contextPath}/export/check_status`,
              success: function(response){
                console.log("EXPORT STATUS: ", response);
                if(response === "DONE") {
                  $('#progressModal').modal('hide');
                  isExportDone = true;
                }
              },
              error: function(error){
                console.log("ERROR: ", error);
                $('#progressModal').find('.modal-body > p').text('There was an error while exporting.');

              }
            });
          }
        }



      });
    </script>

