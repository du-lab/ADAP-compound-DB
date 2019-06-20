function addHistogram(tagKey, dataSet) {

    var tag= d3.keys(JSON.parse(dataSet));
    var tagDistribution = d3.values(JSON.parse(dataSet));
    var histogramTag = tag + ":" + tagDistribution;
    var width = 300;
    var height = 500;

    var xScale = d3.scaleLinear()
        .domain([0,d3.max(tagDistribution)*1.5])
        .range([0,width]);

    var yScale = d3.scaleLinear()
        .domain([0,tagDistribution.length * 60])
        .range([(tagDistribution.length * 60),0]);

    var color = d3.scaleLinear()
        .domain([0,d3.max(tagDistribution)])
        .range(["rgb(51,231,240)","rgb(51,231,240)"]);

    var xAxis = d3.axisBottom()
        .ticks(5)
        .tickSize(2, 6)
        .scale(xScale);

    var yAxis = d3.axisRight()
        .ticks(0)
        .scale(yScale);

    var svg = d3.select("section")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform","translate(5, 5)");

    svg.selectAll("rect")
        .data(tagDistribution)
        .enter()
        .append("rect")
        .attr("width",function (d) { return xScale(d); })
        .attr("height",40)
        .attr("fill", function (d) { return color(d); })
        .attr("y", function(d,i){ return i * 60; });

    svg.selectAll("text")
        .data(tag)
        .enter()
        .append("text")
        .attr("dx","20")
        .attr("dy","1.5em")
        .attr("y",function(d,i){return i * 60;})
        .attr("stroke","black")
        .attr("stroke-width","2")
        .text(function(d){return d;});

    // x axis
    svg.append("g")
        .attr("transform", "translate(0, "+ (tagDistribution.length) * 60 +")")
        .call(xAxis);

    // text label for the x axis
    svg.append("text")
        .data(tagKey)
        .attr("transform",
            "translate(" + (width/2) + " ," +
            ((tagDistribution.length * 60) + 35) + ")")
        .style("text-anchor", "middle")
        .text(tagKey);

    //y axis
    svg.append("g")
        .attr("transform", "translate(0,0)")
        .call(yAxis);

}
