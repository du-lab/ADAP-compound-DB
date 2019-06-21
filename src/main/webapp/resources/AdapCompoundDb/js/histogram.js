function addHistogram(idName,tagKey, dataSet) {

    var tag= d3.keys(JSON.parse(dataSet));
    var tagDistribution = d3.values(JSON.parse(dataSet));

    // // to display tag value as "tagValue:numbers"
    // var histogramTag = tag + ":" + tagDistribution;

    var width = 400;
    var height = (tagDistribution.length * 60 + 150);
    var padding = {top: 20, right: 20, bottom: 20, left: 20};

    var xScale = d3.scaleLinear()
        .domain([0,d3.max(tagDistribution)*1.2])
        .range([0,width]);

    var yScale = d3.scaleLinear()
        .domain([0,tagDistribution.length * 60])
        .range([(tagDistribution.length * 60),-5]);

    // color range of bar
    // var color = d3.scaleLinear()
    //     .domain([0,d3.max(tagDistribution)])
    //     .range(["rgb(51,231,240)","rgb(51,231,240)"]);

    var xAxis = d3.axisBottom()
        .ticks(5)
        .tickSize(2)
        .scale(xScale);

    var yAxis = d3.axisRight()
        .ticks(0)
        .tickSize(0)
        .scale(yScale);

    var svg = d3.select("#"+idName)
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform","translate(5, 5)");

    // for grid in x axis
    var grid = svg.append('g')
        .attr('class', 'grid')
        .attr("transform", "translate("+padding.left+", "+ ((tagDistribution.length) * 60 + padding.left )+")");

    // plot bar chart
    svg.selectAll("rect")
        .data(tagDistribution)
        .enter()
        .append("rect")
        .attr("width",function (d) { return xScale(d); })
        .attr("height",40)
        .attr("fill", "#b47cff")
        .attr("x",padding.right)
        .attr("y", function(d,i){ return i * 60 + padding.bottom });

    // display tag value
    svg.selectAll("text")
        .data(tag)
        .enter()
        .append("text")
        .attr("dx","20")
        .attr("dy","1.5em")
        .attr("y",function(d,i){return i * 60 + padding.bottom;})
        .attr("x",padding.right)
        .attr("stroke","#000000")
        .attr("stroke-width","1")
        .text(function(d){return d;});

    // text label for the x axis
    svg.append("text")
        .data(tagKey)
        .attr("transform",
            "translate(" + (width/2) + " ," +
            ((tagDistribution.length * 60) + 35 +padding.bottom) + ")")
        .style("text-anchor", "middle")
        .attr("stroke","#000000")
        .attr("stroke-width","1.5")
        .text(tagKey);

    // plot x axis
    svg.append("g")
        .attr("transform", "translate("+padding.left+", "+ ((tagDistribution.length) * 60 + padding.left )+")")
        .call(xAxis);

    // plot y axis
    svg.append("g")
        .attr("transform", "translate(0"+padding.left+","+ padding.bottom +")")
        .call(yAxis);

    // plot grid
    grid.call(d3.axisBottom(xScale)
        .ticks(5)
        .tickSize(-(tagDistribution.length * 60),0)
        .tickFormat(''));
}
