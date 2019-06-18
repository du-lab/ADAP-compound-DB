function addHistogram(idName, dataset) {

    var svgWidth = 500;
    var svgHeight = 300;
    var barPadding = 5;
    var barWidth = (svgWidth / dataset.length);
    dataset = [10,20,30,40,50,60];

    var svg = d3.select('svg')
        .attr("width", svgWidth)
        .attr("height", svgHeight);

    var histogram = svg.selectAll("rect")
        .data(dataset)
        .enter()
        .append("rect")
        .attr("y",function(d){
        return d;
        })
        .attr("height", function(d){
            return d;
        })
        .attr("width", barWidth - barpadding)
        .attr()


}