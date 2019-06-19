function addHistogram(idName, dataSet) {

    var width = 500;
    var height = 800;

    var widthScale = d3.scale.linear()
        .domain([0,d3.max(dataSet) + 5])
        .range([0,width]);

    var color = d3.scale.linear()
        .domain([0,82])
        .range(["yellow","teal"]);

    var axis = d3.svg.axis()
    /*.ticks(5)*/
        .scale(widthScale);

    var svg = d3.select("section")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform","translate(5, 10)");

    var bars = svg.selectAll("rect")
        .data(dataSet)
        .enter()
        .append("rect")
        .attr("width",function (d) { return widthScale(d); })
        .attr("height",40)
        .attr("fill", function (d) { return color(d); })
        .attr("y", function(d,i){ return i * 60; });

    svg.selectAll("text")
        .data(dataSet)
        .enter()
        .append("text")
        .attr("dx",function(d){return widthScale(d) / 2;})
        .attr("dy","1.5em")
        .attr("y",function(d,i){return i * 60;})
        .attr("stroke","black")
        .attr("stroke-width","2")
        .attr("font-size",15)
        .text(function(d){return d;});

    svg.append("g")
        .attr("transform", "translate(0, "+ (dataSet.length) * 57 +")")
        .call(axis);


}
