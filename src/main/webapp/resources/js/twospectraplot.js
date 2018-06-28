function TwoSpectraPlot(divId, topSpectrum) {

    var width = 600;
    var height = 600;
    var label_offset = 40;
    var padding = {'top': 40, 'right': 40, 'bottom': 240, 'left': 40};

    var plotWidth = width - padding['left'] - padding['right'];
    var plotHeight = height - padding['top'] - padding['bottom'];

    var tooltip = d3.select('#' + divId)
        .append('div')
        .attr('class', 'tooltip');

    tooltip.append('div').attr('class', 'mz');
    tooltip.append('div').attr('class', 'intensity');

    var xScale = d3.scaleLinear()
        .domain([
            d3.min(topSpectrum.peaks, function (d) {return d.mz}),
            d3.max(topSpectrum.peaks, function (d) {return d.mz})
        ])
        .range([padding['left'], width - padding['right']]);

    var yScale = d3.scaleLinear()
        .domain([-100, 100])
        .range([height - padding['bottom'], padding['top']]);

    var svg = d3.select('#' + divId)
        .append('svg')
        .attr('width', width)
        .attr('height', height);

    var key = function (d) {return Math.round(d.mz);};

    var addPeaks = function (peaks, top) {

        if (top === undefined)
            top = true;

        return peaks.append('line')
            .attr('class', top ? 'top' : 'bottom')
            .attr('data-mz', function(d) {return Math.round(100 * d.mz) / 100;})
            .attr('data-intensity', function(d) {return Math.round(100 * d.intensity) / 100;})
            .attr('x1', function (d) {return xScale(d.mz);})
            .attr('x2', function (d) {return xScale(d.mz);})
            .attr('y1', yScale(0))
            .attr('y2', yScale(0))
            .attr('stroke', top ? 'blue' : 'red')
            .attr('stroke-width', 1)
            .on('mouseover', function () {
                var peak = d3.select(this);
                peak.attr('stroke', 'orange');
                tooltip.select('.mz').html('M/z = ' + peak.attr('data-mz'));
                tooltip.select('.intensity').html('Int = ' + peak.attr('data-intensity'));
                tooltip.style('display', 'block');
            })
            .on('mouseout', function () {
                d3.select(this)
                    .transition()
                    .duration(250)
                    .attr('stroke', top ? 'blue' : 'red');
                tooltip.style('display', 'none');
            })
            .on('mousemove', function() {
                tooltip.style('top', (d3.event.layerY + 10) + 'px')
                    .style('left', (d3.event.layerX + 10) + 'px');
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

    svg.append('text')
        .attr('id', 'tooltip')
        .attr('x', width - padding['right'])
        .attr('y', padding['top'])
        .style('text-anchor', 'middle')
        .text('');

    // ---------------------
    // ----- Plot axes -----
    // ---------------------

    var xAxis = d3.axisBottom()
        .scale(xScale);

    svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(0, ' + (height - padding['bottom']) + ')')
        .call(xAxis);

    svg.append('g')
        .attr('class', 'grid')
        .attr('transform', 'translate(0, ' + (height - padding['bottom']) + ')')
        .call(d3.axisBottom(xScale)
            .ticks(5)
            .tickSize(-plotHeight)
            .tickFormat(''));

    svg.append('text')
        .attr('class', 'label')
        .attr('x', (padding['left'] + width - padding['right']) / 2)
        .attr('y', height - padding['bottom'] + label_offset)
        .style('text-anchor', 'middle')
        .text('m/z');

    var yAxis = d3.axisLeft()
        .scale(yScale);

    svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + padding['left'] + ', 0)')
        .call(yAxis);

    svg.append('g')
        .attr('class', 'grid')
        .attr('transform', 'translate(' + padding['left'] + ', 0)')
        .call(d3.axisLeft(yScale)
            .ticks(5)
            .tickSize(-plotWidth)
            .tickFormat(''));

    svg.append('text')
        .attr('class', 'label')
        .attr('transform', 'rotate(-90)')
        .attr('x', -(padding['top'] + height - padding['bottom']) / 2)
        .attr('y', padding['left'] - label_offset)
        .attr('dy', '1em')
        .style('text-anchor', 'middle')
        .text('intensity');

    // -----------------------
    // ----- Plot legend -----
    // -----------------------

    var legendRectSize = 18;
    var legendSpacing = 6;

    var legend = svg.selectAll('.legend')
        .data(['topSpectrumName', 'bottomSpectrumName'])
        .enter()
        .append('g')
        .attr('class', 'legend')
        .attr('transform', function (d, i) {
            var horz = padding['left'];
            var vert = height - padding['bottom'] / 2 + i * (legendRectSize + legendSpacing);
            return 'translate(' + horz + ', ' + vert + ')';
        });

    legend.append('rect')
        .attr('width', legendRectSize)
        .attr('height', legendRectSize)
        .style('fill', function(d, i) {return i === 0 ? 'blue' : 'red';})
        .style('stroke', function(d, i) {return i === 0 ? 'blue' : 'red';});

    legend.append('text')
        .attr('id', function(d) {return d;})
        .attr('x', legendRectSize + legendSpacing)
        .attr('y', legendRectSize - legendSpacing)
        .text('');

    svg.select('#topSpectrumName')
        .text(topSpectrum.name);

    // -------------------------
    // ----- Update method -----
    // -------------------------

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

        svg.select('#bottomSpectrumName')
            .text(bottomSpectrum.name);
    }
}