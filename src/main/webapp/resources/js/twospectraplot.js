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

    var svg = d3.select('#' + divId)
        .append('svg')
        .attr('width', width)
        .attr('height', height);

    var key = function (d) {
        return Math.round(d.mz);
    };

    var addPeaks = function (peaks, top) {

        if (top === undefined)
            top = true;

        return peaks.append('line')
            .attr('class', top ? 'top' : 'bottom')
            .attr('x1', function (d) {
                return xScale(d.mz);
            })
            .attr('x2', function (d) {
                return xScale(d.mz);
            })
            .attr('y1', yScale(0))
            .attr('y2', yScale(0))
            .attr('stroke', top ? 'blue' : 'red')
            .attr('stroke-width', 1)
            .on('mouseover', function () {
                d3.select(this)
                    .attr('stroke', 'orange');
            })
            .on('mouseout', function () {
                d3.select(this)
                    .transition()
                    .duration(250)
                    .attr('stroke', top ? 'blue' : 'red');
            })
            .transition()
            .attr('y2', function (d) {
                return yScale(d.intensity * (top ? 1 : -1));
            });
    };

    addPeaks(svg.selectAll('line.top')
            .data(topSpectrum.peaks, key)
            .enter(),
        true);

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

        var peaks = svg.selectAll('line.bottom')
            .data(bottomSpectrum.peaks, key);

        addPeaks(peaks.enter(), false);

        peaks.transition()
            .attr('x1', function (d) {
                return xScale(d.mz);
            })
            .attr('x2', function (d) {
                return xScale(d.mz);
            })
            .attr('y1', yScale(0))
            .attr('y2', function (d) {
                return yScale(-d.intensity);
            });

        peaks.exit()
            .transition()
            .attr('y2', yScale(0))
            .remove();
    }
}