function TwoSpectraPlot(divId, topSpectrum) {

	// var width = $('#' + divId).width();
    // var height = $('#' + divId).height();
    var width = 400;
    var height = 400;
    /*if(width > 600) {
    	width = $('#' + divId).width();
    	height = $('#' + divId).height();
    }*/
    var label_offset = 40;
    var padding = {'top': 40, 'right': 40, 'bottom': 40, 'left': 40};

    var plotWidth = width - padding['left'] - padding['right'];
    var plotHeight = height - padding['top'] - padding['bottom'];

    var tooltip = d3.select('#' + divId)
        .append('div')
        .attr('class', 'tooltip');

    tooltip.append('div').attr('class', 'mz');
    tooltip.append('div').attr('class', 'intensity');

    var minMz = d3.min(topSpectrum.peaks, function (d) {return d.mz});
    var maxMz = d3.max(topSpectrum.peaks, function (d) {return d.mz});
    var mzRange = maxMz - minMz;

    var xScale = d3.scaleLinear()
        .domain([minMz - 0.05 * mzRange, maxMz + 0.05 * mzRange])
        .range([padding['left'], width - padding['right']]);

    var yScale = d3.scaleLinear()
        .domain([-100, 100])
        .range([height - padding['bottom'], padding['top']]);
    yScale.clamp(true);

    var resetXScale = xScale;
    var resetYScale = yScale;

    var svg = d3.select('#' + divId)
        .append('svg')
        .attr('viewBox', '0 0 400 400');
        // .attr('width', width)
        // .attr('height', height);
    var gButton = svg.append("g")
        .attr('class', 'button_g desktop')
        .attr("transform", "translate(" + padding.left + ", 0)")
        .attr('width', width/10)
        .attr('height', height/16)
        .style("cursor", "pointer");
    var button = gButton.append("svg:rect")
        .attr('width', width/10)
        .attr('height', height/16)
        .attr('rx', 10)
        .attr('ry', 10)
        .style("fill", "#f2f2f2");
    var buttonText = gButton.append("svg:text")
        .attr('class', 'label')
        .attr('x', width/60)
        .attr('y', height/20)
        .text("Reset");
    var key = function (d) {return Math.round(d.mz);};

    var addPeaks = function (peaks, top) {

        if (top === undefined){top = true;}

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

    var gx = svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(0, ' + (height - padding['bottom']) + ')')
        .call(xAxis);

    var gridLinesX = svg.append('g')
        .attr('class', 'grid')
        .attr('transform', 'translate(0, ' + (height - padding['bottom']) + ')');
    gridLinesX.call(d3.axisBottom(xScale)
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

    var gy = svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + padding['left'] + ', 0)')
        .call(yAxis);

    var gridLinesY = svg.append('g')
        .attr('class', 'grid')
        .attr('transform', 'translate(' + padding['left'] + ', 0)');
    gridLinesY.call(d3.axisLeft(yScale)
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
                return Math.max(padding['left'], xScale(d.mz));
            })
            .attr('x2', function (d) {
                return Math.max(padding['left'], xScale(d.mz));
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
    };

    var gridArea = svg.append('svg:rect')
        .attr('class', 'zoom xy box')
        .attr("width", width - padding.left)
        .attr("height", height - padding.bottom)
        .attr('transform', 'translate(' + padding.left + ', ' + padding.top + ')')
        .style("visibility", "hidden")
        .style("cursor", "crosshair")
        .attr("pointer-events", "all");
    var rect = function(x, y, w, h) {
        return "M"+x+" "+y+" l"+w+" "+0+" l"+0+" "+h+" l"+(-w)+" "+0+"z";
    };
    var selection = svg.append("path")
        .attr("class", "selection desktop")
        .attr("visibility", "hidden");

    var startSelection = function(start) {
        selection.attr("d", rect(start[0], start[0], 0, 0))
            .attr("visibility", "visible");
    };

    var moveSelection = function(start, moved) {
        selection.attr("d", rect(start[0], start[1], moved[0]-start[0], moved[1]-start[1]));
    };

    var endSelection = function(start, end) {
        selection.attr("visibility", "hidden");
    };
    var zoomselection = function() {
        var value = selection.attr("d");
        var segments = value.replace(/[A-Z]|[a-z]/g,'').split(" "); // M291.046875,136 l146,0 l0,149 l-146,0z;
        var x = parseFloat(segments[0]) - parseFloat(padding['left']);
        var y = parseFloat(segments[1]) - parseFloat(padding['top']);
        var w = parseFloat(segments[2]);
        var h = parseFloat(segments[5]);

        if(x + w < 0) {
            w = -x;
        }
        if(y + h < 0) {
            h = -y;
        }
        if(y + h > plotHeight) {
            h = plotHeight - y;
        }
        if(x + w > plotWidth) {
            w = plotWidth - x;
        }

        if(Math.abs(w) > 5 && Math.abs(h) > 5) {
            interpolateZoom(x, y, w, h);
        }
    };

    var scaleGraph = function(newScaleX, newScaleY) {
        gx.call(xAxis.scale(newScaleX));
        gy.call(yAxis.scale(newScaleY));
        xScale = newScaleX;
        yScale = newScaleY;
        gridLinesX.call(d3.axisBottom(newScaleX)
            .ticks(5)
            .tickSize(-plotHeight)
            .tickFormat(''));
        gridLinesY.call(d3.axisLeft(newScaleY)
            .ticks(5)
            .tickSize(-plotWidth)
            .tickFormat(''));

        var spectra = d3.select("svg").selectAll("line.top,.bottom");
        spectra.transition()
            .attr('x1', function (d) {
                return Math.max(padding['left'], newScaleX(d.mz));
            })
            .attr('x2', function (d) {
                return Math.max(padding['left'], newScaleX(d.mz));
            })
            .attr('y1', function (d) {
                return Math.max(padding['top'], Math.min(newScaleY(0), height - padding['bottom']));
            })
            .attr('y2', function (d) {
                var tempIntensity = d.intensity;
                if(d3.select(this).classed("bottom")) {
                    tempIntensity = -tempIntensity;
                }
                return Math.max(padding['top'], Math.min(newScaleY(tempIntensity), height - padding['bottom']));
            });
    };
    var resetGraph = function() {
        scaleGraph(resetXScale, resetYScale);
    };
    button.on("click", resetGraph);
    buttonText.on("click", resetGraph);

    var interpolateZoom = function(x, y, w, h) {
        var domainX = xAxis.scale().domain();
        var domainY = yAxis.scale().domain();

        var newXDomainStart = domainX[0] + (domainX[1] - domainX[0]) * x / plotWidth;
        var newXDomainEnd = domainX[0] + (domainX[1] - domainX[0]) * (x + w) / plotWidth;
        if(newXDomainStart > newXDomainEnd) {
            [newXDomainStart, newXDomainEnd] = [newXDomainEnd, newXDomainStart]
        }

        var newYDomainStart = domainY[1] - (domainY[1] - domainY[0]) * y / plotHeight;
        var newYDomainEnd = domainY[1] - (domainY[1] - domainY[0]) * (y + h) / plotHeight;
        if(newYDomainEnd > newYDomainStart) {
            [newYDomainStart, newYDomainEnd] = [newYDomainEnd, newYDomainStart]
        }

        var newScaleX = d3.scaleLinear()
            .domain([newXDomainStart, newXDomainEnd])
            .range([padding['left'], width - padding['right']]);
        var newScaleY = d3.scaleLinear()
            .domain([newYDomainEnd, newYDomainStart])
            .range([height - padding['bottom'], padding['top']]);
        newScaleY.clamp(true);

        scaleGraph(newScaleX, newScaleY);
    };

    gridArea.on("mousedown", function() {
        var subject = d3.select(window);
        var parent = this.parentNode;
        var start = d3.mouse(parent);
        startSelection(start);
        subject
            .on("mousemove.selection", function() {
                moveSelection(start, d3.mouse(parent));
            }).on("mouseup.selection", function() {
            endSelection(start, d3.mouse(parent));
            zoomselection();
            subject.on("mousemove.selection", null).on("mouseup.selection", null);
        });
    });

    gridArea.on("touchstart", function() {
        var subject = d3.select(this);
        var parent = this.parentNode;
        var id = d3.event.changedTouches[0].identifier;
        var start = d3.touch(parent, id);
        var pos;
        startSelection(start);
        subject
            .on("touchmove." + id, function() {
                if (pos = d3.touch(parent, id)) {
                    moveSelection(start, pos);
                }
            }).on("touchend." + id, function() {
            if (pos = d3.touch(parent, id)) {
                endSelection(start, pos);
                subject.on("touchmove." + id, null).on("touchend." + id, null);
            }
        });
    });
}