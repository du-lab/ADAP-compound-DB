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
                //custom plugin
                const customText = {
                    id: 'customText',
                    afterDraw: (chart, args, options) => {
                        const {
                            ctx,
                            canvas
                        } = chart;
                        textObjects = options.text;

                        if (textObjects.length === 0) {
                            return;
                        }

                        textObjects.forEach((textObj) => {
                            ctx.save();

                            ctx.textAlign = textObj.textAlign;
                            ctx.font = `${textObj.size } ${textObj.font || 'Arial'}`;
                            ctx.fillStyle = textObj.color;
                            ctx.fillText(textObj.text, textObj.x, textObj.y)

                            ctx.restore();
                        })
                    }
                }
                Chart.register(customText);
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
                            label: 'Unmatched/Filtered out',
                            data: unmatchedQueryPeaks.concat(unmatchedLibraryPeaks),
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
                        animation: {duration: 500},
                        plugins: {
                            customText: {
                                text: [{
                                    text: 'Double-click to Zoom out',
                                    x: 100,
                                    y: 50,
                                    textAlign: 'center',
                                    size: '15px',
                                    color: '#808080'
                                }]
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