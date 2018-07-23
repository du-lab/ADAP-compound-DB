function multiply(spectrum, factor) {
    for (var i = 0; i < spectrum.length; ++i) {
        spectrum[i][1] *= factor;
    }
    return spectrum;
}

function addPlot(idName, jsonQueryPeaks, queryName, jsonHitPeaks, hitName, score) {

    var chartData = {
        type: 'bar',
        stacked: true,
        title: {text: 'Match: ' + score},
        legend: {
            align: 'center',
            verticalAlign: 'bottom',
            layout: '1x'
        },
        scaleX: {
            label: {text: 'M/z'},
            zooming: true
        },
        scaleY: {
            values: '-100:100:20'
        },
        series: [
            {
                values: JSON.parse(jsonQueryPeaks),
                text: queryName
            },
            {
                values: multiply(JSON.parse(jsonHitPeaks), -1),
                text: hitName
            }
        ],
        plot: {
            tooltip: {
                text: 'm/z: %k<br>int: %v',
                decimals: 4,
                fontColor: 'black',
                backgroundColor: 'white',
                borderWidth: 1,
                borderColor: 'grey'
            }
        }
    };

    zingchart.render({
        id: idName,
        data: chartData,
        height: 400,
        width: 600
    });
}