function addHistogram(idName, dataset) {

    var svgWidth = 280;
    var svgHeight = 280;
    var barPadding = 5;
    var barWidth = (svgWidth / dataset.length);

    var svg = d3.select('histogram')
        .attr("width", svgWidth)
        .attr("height", svgHeight);

    var histogram = svg.selectAll("rect")
        .data(dataset)
        .enter()
        .append("rect")
        .attr("y",function(d){
        return svgHeight - d;
        })
        .attr("height", function(d){
            return d;
        })
        .attr("width", barWidth - barPadding)
        .attr("transform", function(d,i){
        var translate = [barWidth * i, 0];
        return "translate("+translate+")";
    });
}
