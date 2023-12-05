/**
 * @requires jQuery
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

                let matchedQueryPeaks = resp1[0].peaks.filter(peak => queryPeakMzs.includes(peak.mz));
                let unmatchedQueryPeaks = resp1[0].peaks.filter(peak => !queryPeakMzs.includes(peak.mz));

                let matchedLibraryPeaks = resp2[0].peaks.filter(peak => libraryPeakMzs.includes(peak.mz));
                let unmatchedLibraryPeaks = resp2[0].peaks.filter(peak => !libraryPeakMzs.includes(peak.mz));

                chart = new Chart(canvas, {
                    type: 'bar',
                    data: {
                        datasets: [{
                            label: 'Signal',
                            data: matchedQueryPeaks,
                            barThickness: 2,
                            backgroundColor: '#FF0000',
                            grouped: false
                        }, {
                            label: 'Match',
                            data: matchedLibraryPeaks,
                            barThickness: 2,
                            backgroundColor: '#0000FF',
                            grouped: false
                        }, {
                            label: 'Unmatched',
                            data: unmatchedQueryPeaks,
                            barThickness: 2,
                            backgroundColor: '#808080',
                            grouped: false
                        }, {
                            label: 'Unmatched',
                            data: unmatchedLibraryPeaks,
                            barThickness: 2,
                            backgroundColor: '#808080',
                            grouped: false
                        },  {
                            label: 'Score = ' + score.toFixed(3) * 1000
                        }]
                    },
                    options: {
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
                            legend: {
                                labels: {
                                    filter: function(item, chart) {
                                        return  item.text == null || !item.text.includes('Unmatched');
                                    }
                                }
                            },
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