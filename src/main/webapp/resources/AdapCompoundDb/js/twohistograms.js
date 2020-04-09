function addClusterTagsHistogram(idName, tag, dataSet, pValue) {

    // Draws a single histogram: left or right
    function drawHistogram(svg, xScale, yScale, values, color) {

        svg.append('g')
            .attr('class', 'histogram')
            .selectAll(".rect")
            .data(values)
            .enter()
            .append("rect")
            .attr("width", function (d) {
                return Math.abs(xScale(d) - xScale(0));
            })
            .attr("height", barHeight)
            .attr("fill", color)
            .attr("x", function (d) {
                return width / 2 + Math.min(0, xScale(d) - xScale(0));
            })
            .attr("y", function (d, i) {
                return yScale(i + 1);
            });
    }

    // Draws three axes: left X, right X, and Y
    function drawAxes(svg, leftXScale, rightXScale, yScale) {

        function drawXAxis(svg, xScale) {
            let xAxis = d3.axisBottom()
                .ticks(5)
                .tickSize(2)
                // .tickFormat(d3.format(".1f"))
                .scale(xScale);

            svg.append("g")
                .attr('class', 'xAxis')
                .attr("transform", "translate(0, " + yScale(0) + ")")
                .call(xAxis);
        }

        function drawYAxis(svg, yScale) {
            let yAxis = d3.axisRight()
                .ticks(0)
                .tickSize(0)
                .scale(yScale);

            // plot y axis
            svg.append("g")
                .attr('class', 'yAxis')
                .attr("transform", "translate(  " + width / 2 + ", 0)")
                .call(yAxis);
        }

        let axes = svg.append('g')
            .attr('class', 'axes');

        drawXAxis(axes, leftXScale);
        drawXAxis(axes, rightXScale);
        drawYAxis(axes, yScale);
    }

    // Draws a single grid: left or right
    function drawGrid(svg, xScale, yScale) {
        svg.append('g')
            .attr('class', 'grid')
            .attr("transform", "translate(0, " + yScale(0) + ")")
            .call(d3.axisBottom(xScale)
                .ticks(5)
                .tickSize(yScale(tagKeys.length) - yScale(0), 0)
                .tickFormat('')
            );
    }

    // Draws labels on histograms
    function drawText(svg, labels, yScale) {
        svg.append('g')
            .attr('class', 'labels')
            .selectAll(".text")
            .data(labels)
            .enter()
            .append("text")
            .attr("dx", 0)
            .attr("dy", "0.25em")
            .attr("y", function (d, i) {
                return yScale(i + 0.5);
            })
            .attr("x", width / 2)
            .attr("stroke", "#000000")
            .attr("stroke-width", "1")
            .style("text-anchor", "middle")
            .text(function (d) {
                return d;
            });
    }

    let tagKeys = d3.keys(JSON.parse(dataSet));
    let values = d3.values(JSON.parse(dataSet));
    let clusterValues = [];
    let allDbValues = [];
    let roundPValue = Math.round(pValue * 1000) / 1000;

    for (let m = 0; m < tagKeys.length; m++) {
        let clusterValue = values[m]["clusterValue"];
        let allDbValue = values[m]["dbValue"];
        clusterValues.push(clusterValue);
        allDbValues.push(allDbValue);
    }

    const padding = {top: 20, right: 20, bottom: 40, left: 20, spacing: 2};
    // const width = 400 - padding.left - padding.right;
    const width = 400;
    const height = 400;
    // const height = (tagKeys.length * 60 + 100);
    // const height = 400 - padding.top - padding.bottom;
    const barHeight = 0.9 * (height - padding.top - padding.bottom) / tagKeys.length;

    let allDbXScale = d3.scaleLinear()
        .domain([0, d3.max(allDbValues) * 1.2])
        .range([width / 2, 0]);

    let clusterXScale = d3.scaleLinear()
        .domain([0, d3.max(clusterValues) * 1.2])
        .range([width / 2, width]);

    let yScale = d3.scaleLinear()
        .domain([0, tagKeys.length])
        .range([height - padding.bottom, padding.top]);

    let svg = d3.select("#" + idName)
        .append("svg")
        .attr("width", width)
        .attr("height", height);

    drawGrid(svg, allDbXScale, yScale);
    drawGrid(svg, clusterXScale, yScale);
    drawAxes(svg, allDbXScale, clusterXScale, yScale);
    drawHistogram(svg, allDbXScale, yScale, allDbValues, "#b47cff");
    drawHistogram(svg, clusterXScale, yScale, clusterValues, "#ffb47c");
    drawText(svg, tagKeys, yScale);

    // text label for the x axis
    svg.append("text")
        .attr("transform", "translate(" + (width / 2) + " ," + (height - padding.bottom / 2) + ")")
        .attr('dy', '1em')
        .style("text-anchor", "middle")
        .attr("stroke", "#000000")
        .attr("stroke-width", "1.5")
        .text(tag);

    // text label for the pValue
    svg.append("text")
        .attr("transform", "translate(" + (width / 2) + " ," + (padding.top / 2) + " )")
        .style("text-anchor", "middle")
        .attr("stroke", "#000000")
        .attr("stroke-width", "1.5")
        .text("Chi-squared test p-value: " + roundPValue);
}
