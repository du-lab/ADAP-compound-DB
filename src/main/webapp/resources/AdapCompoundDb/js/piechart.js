function addPieChart(idName, dataset) {

    var width = 360;
    var height = 360;
    var radius = Math.min(width, height) / 2;
    var donutWidth = 75;
    var legendRectSize = 18;
    var legendSpacing = 4;

    var total = d3.sum(dataset.map(function (d) {return parseInt(d.count);}));

    var tooltip = d3.select('#' + idName)
        .append('div')
        .attr('class', 'tooltip');

    tooltip.append('div').attr('class', 'label');
    tooltip.append('div').attr('class', 'count');
    tooltip.append('div').attr('class', 'percent');

    var color = d3.scaleOrdinal(d3.schemeCategory10);

    var svg = d3.select('#' + idName)
        .append('svg')
        .attr('width', width)
        .attr('height', height)
        .append('g')
        .attr('transform', 'translate(' + (width / 2) + ', ' + (height / 2) + ')');

    var arc = d3.arc()
        .innerRadius(radius - donutWidth)
        .outerRadius(radius);

    var pie = d3.pie()
        .value(function (d) {return d.count})
        .sort(null);

    var g = svg.selectAll('arc')
        .data(pie(dataset))
        .enter()
        .append('g')
        .attr('class', 'arc');

    var path = g.append('path')
        .attr('d', arc)
        .attr('fill', function (d) {return color(d.data.label);});

    g.filter(function(d) {return d.data.count > 0;})
        .append('text')
        .attr('transform', function(d) {return 'translate(' + arc.centroid(d) + ')';})
        .attr('dy', '0.5em')
        .text(function(d) {return (Math.round(1000 * d.data.count / total) / 10) + '%';})
        .style('fill', '#fff')
        .style('text-anchor', 'middle');

    path.on('mouseover', function (d) {
        var percent = Math.round(1000 * d.data.count / total) / 10;
        tooltip.select('.label').html(d.data.label);
        tooltip.select('.count').html(d.data.count);
        tooltip.select('.percent').html(percent + '%');
        tooltip.style('display', 'block');
    });

    path.on('mouseout', function () {
        tooltip.style('display', 'none');
    });

    path.on('mousemove', function () {
        tooltip.style('top', (d3.event.layerY + 10) + 'px')
            .style('left', (d3.event.layerX + 10) + 'px');
    });

    var legend = svg.selectAll('.legend')
        .data(color.domain())
        .enter()
        .append('g')
        .attr('class', 'legend')
        .attr('transform', function (d, i) {
            var height = legendRectSize + legendSpacing;
            var offset = height * color.domain().length / 2;
            var horz = -4 * legendRectSize;
            var vert = i * height - offset;
            return 'translate(' + horz + ', ' + vert + ')';
        });

    legend.append('rect')
        .attr('width', legendRectSize)
        .attr('height', legendRectSize)
        .style('fill', color)
        .style('stroke', color);

    legend.append('text')
        .attr('x', legendRectSize + legendSpacing)
        .attr('y', legendRectSize - legendSpacing)
        .text(function (d) {return d;});
}