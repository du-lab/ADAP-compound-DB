function TwoSpectraPlot(divId, topSpectrum) {

    var width = 600;
    var height = 400;
    var padding = 40;

    var xScale = d3.scaleLinear()
        .domain([
            d3.min(topSpectrum.peaks, function (d) {
                return d.mz
            }),
            d3.max(topSpectrum.peaks, function (d) {
                return d.mz
            })
        ])
        .range([padding, width - padding]);

    var yScale = d3.scaleLinear()
        .domain([-100, 100])
        .range([height - padding, padding]);

    // var svg = d3.select('#' + divId)
    //     .select('svg');
    //
    //
    // if (svg.empty())
    var svg = d3.select('#' + divId)
        .append('svg')
        .attr('width', width)
        .attr('height', height);

    // svg.selectAll('*')
    //     .remove();

    svg.selectAll('line.top')
        .data(topSpectrum.peaks)
        .enter()
        .append('line')
        .attr('class', 'top')
        .attr('x1', function (d) {
            return xScale(d.mz);
        })
        .attr('x2', function (d) {
            return xScale(d.mz);
        })
        .attr('y1', yScale(0))
        .attr('y2', function (d) {
            return yScale(d.intensity);
        })
        .attr('stroke', 'blue')
        .attr('stroke-width', 1);

    var xAxis = d3.axisBottom()
        .scale(xScale);

    svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(0, ' + (height - padding) + ')')
        .call(xAxis);

    svg.append('text')
        .attr('class', 'label')
        .attr('x', width / 2)
        .attr('y', height)
        .style('text-anchor', 'middle')
        .text('m/z');

    var yAxis = d3.axisLeft()
        .scale(yScale);

    svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + padding + ', 0)')
        .call(yAxis);

    svg.append('text')
        .attr('class', 'label')
        .attr('transform', 'rotate(-90)')
        .attr('x', -height / 2)
        .attr('y', 0)
        .attr('dy', '1em')
        .style('text-anchor', 'middle')
        .text('intensity');

    this.update = function (bottomSpectrum) {

        svg.selectAll('line.bottom').remove();

        svg.selectAll('line.bottom')
            .data(bottomSpectrum.peaks)
            .enter()
            .append('line')
            .attr('class', 'bottom')
            .attr('x1', function (d) {
                return xScale(d.mz);
            })
            .attr('x2', function (d) {
                return xScale(d.mz);
            })
            .attr('y1', yScale(0))
            .attr('y2', function (d) {
                return yScale(-d.intensity);
            })
            .attr('stroke', 'red')
            .attr('stroke-width', 1);
    }
}