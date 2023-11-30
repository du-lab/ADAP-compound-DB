/**
 * @requires jQuery, D3, SpeckTackle
 */

jQuery.fn.spectrumPlot = function (id, restURL1, restURL2, queryPeakMzs, libraryPeakMzs, onComplete) {
    let div = $(this);

    // let oldId = div.attr('data-id')
    // if (id === oldId) return;

    // Remove all child elements
    div.text(null);
    while (div.firstChild)
        div.firstChild.remove();

    div.attr('data-id', id);

    let mzs = {
        queryPeakMzs: queryPeakMzs,
        libraryPeakMzs: libraryPeakMzs
    };

    $.when($.ajax({type: "POST", dataType: 'json', contentType: 'application/json',url: restURL1, data: JSON.stringify(mzs)}),
        $.ajax({type: "POST", dataType: 'json', contentType: 'application/json', url: restURL2, data: JSON.stringify(mzs)}))
        .then(function (resp1, resp2) {


            //render graph if there's both peaks
            if(resp1[0].peaks.length != 0 && resp2[0].peaks.length !=0) {
                $('#plot_content').show();
                chart = st.chart.ms()
                    .legend(true)
                    .labels(true)

                    // .xlabel('m/z')
                    // .ylabel('Intensity')
                    .margins([10, 60, 40, 100]);

                // div.css('width', '70%');
                // div.css('height', '70%');
                chart.render(div[0]);

                let handle = st.data.set()
                    .x('peaks.mz')
                    .y('peaks.intensity')
                    .title('name');

                chart.load(handle);
                query = resp1[0];
                query['name'] = 'Query';
                match = resp2[0];
                match['name'] = 'Match';
                handle.add([query, match]);

                $(".st-xaxis, .st-yaxis, .st-options, .st-legend").css("font-size","80%");
                onComplete(true);
            }
            else
            {

                onComplete(false);

            }




        });




}