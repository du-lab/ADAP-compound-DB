/**
 * @requires jQuery, D3, SpeckTackle
 */

let chart ;
jQuery.fn.spectrumPlot = function (id, score, restURL1, restURL2, queryPeakMzs, libraryPeakMzs, onComplete) {
    let canvas = $(this)[0];

    //clear plot
    const context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);

    if (chart) {
        chart.destroy();
    }

    $.when($.ajax({dataType: 'json', url: restURL1}), $.ajax({dataType: 'json', url: restURL2}))
        .then(function (resp1, resp2) {
            //render graph if there's both peaks
            if(resp1[0].peaks.length != 0 && resp2[0].peaks.length !=0) {
                $('#plot_content').show();
                chart = new Chart(canvas, {
                    type: 'bar',
                    data: {
                        datasets: [{
                            label: 'Signal',
                            data: resp1[0].peaks,
                            barThickness: 2,
                            //https://www.chartjs.org/docs/latest/general/options.html
                            backgroundColor: function(context, options){
                                const index = context.dataIndex;
                                const value = context.dataset.data[index]
                                //grey for unmatched peaks
                                return queryPeakMzs.includes(value.mz) ? '#FF0000' : '#808080' ;
                            },
                            grouped: false
                        }, {
                            label: 'Match',
                            data: resp2[0].peaks,
                            barThickness: 2,
                            backgroundColor: function(context, options){
                                const index = context.dataIndex;
                                const value = context.dataset.data[index]
                                //grey for unmatched peaks
                                return libraryPeakMzs.includes(value.mz) ? '#0000FF' : '#808080' ;
                            },
                            grouped: false
                        }, {
                            label: 'Score = ' + score.toFixed(3) * 1000
                        }]
                    },
                    options: {
                        legend: {
                            display: true,

                            color: 'rgb(255, 99, 132)'

                        },
                        parsing: {
                            xAxisKey: 'mz',
                            yAxisKey: 'intensity'
                        },
                        scales: {
                            x: {
                                type: 'linear',
                                position: 'bottom',
                                title: { display: true, text: 'm/z' },
                            },
                            y: {
                                ticks: { display: false },
                            }
                        },
                        animation: { duration: 500 },
                        plugins: {
                            zoom: {
                                zoom: {
                                    drag: { enabled: true },
                                    mode: 'x'
                                }
                            }
                        }
                    }
                });

                $(".st-xaxis, .st-yaxis, .st-options, .st-legend").css("font-size","80%");
                $('#resetZoom').on('click', function() {
                    if (chart) chart.resetZoom();
                });
                $('#plot').dblclick(function(){
                    if(chart) chart.resetZoom();
                });
                onComplete(true);
            }
            else
            {
                onComplete(false);
            }


        });




}