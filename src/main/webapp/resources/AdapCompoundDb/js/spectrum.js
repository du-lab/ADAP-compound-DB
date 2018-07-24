function addPlot(idName, jsonString) {

    var chartData = {
        type: 'bar',
        scaleX: {
            label: {text: 'M/z'},
            zooming: true
        },
        scaleY: {
            values: '0:100:20'
        },
        series: [
            {values: JSON.parse(jsonString)}
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