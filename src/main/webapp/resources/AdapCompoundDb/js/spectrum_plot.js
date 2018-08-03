function SpectrumPlot(divId, spectrum) {

    var width = 600;
    var height = 400;
    var label_offset = 40;
    var padding = {'top': 40, 'right': 40, 'bottom': 40, 'left': 40};

    var plotWidth = width - padding['left'] - padding['right'];
    var plotHeight = height - padding['top'] - padding['bottom'];

    var tooltip = d3.select('#' + divId)
        .append('div')
        .attr('class', 'tooltip');

    tooltip.append('div').attr('class', 'mz');
    tooltip.append('div').attr('class', 'intensity');

    var minMz = d3.min(spectrum.peaks, function (d) {return d.mz});
    var maxMz = d3.max(spectrum.peaks, function (d) {return d.mz});
    var mzRange = maxMz - minMz;
    var intensityMax = d3.max(spectrum.peaks, function (d) {return d.intensity});

    var xScale = d3.scaleLinear()
        .domain([minMz - 0.05 * mzRange, maxMz + 0.05 * mzRange])
        .range([padding['left'], width - padding['right']]);

    var yScale = d3.scaleLinear()
        .domain([0, intensityMax * 2])
        .range([height - padding['bottom'], padding['top']]);
    yScale.clamp(true);
    // // -----------------------
    // // ----- Zoom -----
    // // -----------------------

    var currentTransform = null;

    var zoomGraph = function(xZ, yZ) {
        //var xZ = arguments[0], yZ = arguments[1];
        if(d3.event == null) {
            return;
        }
        // if(currentTransform == null) {
            currentTransform = d3.event.transform;
        // }
        var spectra = d3.select("svg").selectAll("line.spectrum");

        if(xZ) {
            var newXscale = currentTransform.rescaleX(xScale);
            gx.call(xAxis.scale(newXscale));
            spectra
                .attr('x1', function (d) {
                    return Math.max(padding['left'], newXscale(d.mz));
                })
                .attr('x2', function (d) {
                    return Math.max(padding['left'], newXscale(d.mz));
                });
        }

        if(yZ) {
            var newYscale = currentTransform.rescaleY(yScale);
            gy.call(yAxis.scale(newYscale));

            spectra
                .attr('y2', function (d) {
                    return Math.min($(this).attr("y1"), newYscale(d.intensity));
                });
        }
    }

    var zoom = d3.zoom()
        .on("zoom", zoomGraph.bind(this, true, true));
    var zoomX = d3.zoom()
        .on("zoom", zoomGraph.bind(this, true, false));
    var zoomY = d3.zoom()
        .on("zoom", zoomGraph.bind(this, false, true));

    var svg = d3.select('#' + divId)
        .append('svg')
        .attr('width', width)
        .attr('height', height);

    var key = function (d) {return Math.round(d.mz);};

    var addPeaks = function (peaks) {

        return peaks.append('line')
            .attr('class', 'spectrum')
            .attr('data-mz', function(d) {return Math.round(100 * d.mz) / 100;})
            .attr('data-intensity', function(d) {return Math.round(100 * d.intensity) / 100;})
            .attr('x1', function (d) {return xScale(d.mz);})
            .attr('x2', function (d) {return xScale(d.mz);})
            .attr('y1', yScale(0))
            .attr('stroke', 'blue')
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
                    .attr('stroke', 'blue');
                tooltip.style('display', 'none');
            })
            .on('mousemove', function() {
                tooltip.style('top', (d3.event.layerY + 10) + 'px')
                    .style('left', (d3.event.layerX + 10) + 'px');
            })
            .transition()
            .attr('y2', function (d) {
                return yScale(d.intensity);
            });
    };

    addPeaks(svg.selectAll('line.spectrum')
            .data(spectrum.peaks, key)
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

    var gx = svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(0, ' + (height - padding['bottom']) + ')')
        .call(xAxis);

    var graph1 = svg.append('g')
        .attr('class', 'grid')
        .attr('transform', 'translate(0, ' + (height - padding['bottom']) + ')');
    graph1.call(d3.axisBottom(xScale)
            .ticks(5)
            .tickSize(-plotHeight)
            .tickFormat(''))

    svg.append('text')
        .attr('class', 'label')
        .attr('x', (padding['left'] + width - padding['right']) / 2)
        .attr('y', height - padding['bottom'] + label_offset)
        .style('text-anchor', 'middle')
        .text('m/z');

    var yAxis = d3.axisLeft()
        .scale(yScale);

    var gy = svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + padding['left'] + ', 0)')
        .call(yAxis);

    var graph2 = svg.append('g')
        .attr('class', 'grid')
        .attr('transform', 'translate(' + padding['left'] + ', 0)');
    graph2.call(d3.axisLeft(yScale)
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


    svg.append('svg:rect')
        .attr('class', 'zoom x box')
        .attr("width", width - padding.left)
        .attr("height", padding.bottom)
        .attr('transform', 'translate(' + padding['left'] + ', ' + (height - padding['bottom']) + ')')
        .style("visibility", "hidden")
        .attr("pointer-events", "all")
        .call(zoomX);
    svg.append('svg:rect')
        .attr('class', 'zoom y box')
        .attr("width", padding.left)
        .attr("height", height - padding.bottom)
        .attr('transform', 'translate(0, 0)')
        .style("visibility", "hidden")
        .attr("pointer-events", "all")
        .call(zoomY);
    var gridArea = svg.append('svg:rect')
        .attr('class', 'zoom xy box')
        .attr("width", width - padding.left)
        .attr("height", height - padding.bottom)
        .attr('transform', 'translate(' + padding.left + ', 0)')
        .style("visibility", "hidden")
        .attr("pointer-events", "all");

    gridArea
        .on( "mousedown", function() {
            var p = d3.mouse( this);

            gridArea.append( "rect")
                .attr({
                    rx      : 6,
                    ry      : 6,
                    class   : "spectrum",
                    x       : p[0],
                    y       : p[1],
                    width   : 0,
                    height  : 0
                })
        })
        .on( "mousemove", function() {
            var s = svg.select( "rect.spectrum");

            if( !s.empty()) {
                var p = d3.mouse( this),

                    d = {
                        x       : parseInt( s.attr( "x"), 10),
                        y       : parseInt( s.attr( "y"), 10),
                        width   : parseInt( s.attr( "width"), 10),
                        height  : parseInt( s.attr( "height"), 10)
                    },
                    move = {
                        x : p[0] - d.x,
                        y : p[1] - d.y
                    }
                ;

                if( move.x < 1 || (move.x*2<d.width)) {
                    d.x = p[0];
                    d.width -= move.x;
                } else {
                    d.width = move.x;
                }

                if( move.y < 1 || (move.y*2<d.height)) {
                    d.y = p[1];
                    d.height -= move.y;
                } else {
                    d.height = move.y;
                }

                s.attr( d);
                //console.log( d);
            }
        });
    // // -----------------------
    // // ----- Plot legend -----
    // // -----------------------
    //
    // var legendRectSize = 18;
    // var legendSpacing = 6;
    //
    // var legend = svg.selectAll('.legend')
    //     .data(['topSpectrumName', 'bottomSpectrumName'])
    //     .enter()
    //     .append('g')
    //     .attr('class', 'legend')
    //     .attr('transform', function (d, i) {
    //         var horz = padding['left'];
    //         var vert = height - padding['bottom'] / 2 + i * (legendRectSize + legendSpacing);
    //         return 'translate(' + horz + ', ' + vert + ')';
    //     });
    //
    // legend.append('rect')
    //     .attr('width', legendRectSize)
    //     .attr('height', legendRectSize)
    //     .style('fill', function(d, i) {return i === 0 ? 'blue' : 'red';})
    //     .style('stroke', function(d, i) {return i === 0 ? 'blue' : 'red';});
    //
    // legend.append('text')
    //     .attr('id', function(d) {return d;})
    //     .attr('x', legendRectSize + legendSpacing)
    //     .attr('y', legendRectSize - legendSpacing)
    //     .text('');
    //
    // svg.select('#topSpectrumName')
    //     .text(spectrum.name);

    // // -------------------------
    // // ----- Update method -----
    // // -------------------------
    //
    // this.update = function (bottomSpectrum) {
    //
    //     var peaks = svg.selectAll('line.bottom')
    //         .data(bottomSpectrum.peaks, key);
    //
    //     addPeaks(peaks.enter(), false);
    //
    //     peaks.transition()
    //         .attr('x1', function (d) {
    //             return xScale(d.mz);
    //         })
    //         .attr('x2', function (d) {
    //             return xScale(d.mz);
    //         })
    //         .attr('y1', yScale(0))
    //         .attr('y2', function (d) {
    //             return yScale(-d.intensity);
    //         });
    //
    //     peaks.exit()
    //         .transition()
    //         .attr('y2', yScale(0))
    //         .remove();
    //
    //     svg.select('#bottomSpectrumName')
    //         .text(bottomSpectrum.name);
    // }
}