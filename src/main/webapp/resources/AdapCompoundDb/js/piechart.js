function addPieChart(idName, dataset) {

    /**
     * Draws a pie
     */
    function drawPie(svg, radius, dataset, color) {

        let donutWidth = 75;

        let arc = d3.arc()
            .innerRadius(radius - donutWidth)
            .outerRadius(radius);

        let pie = d3.pie()
            .value(function (d) {return d.count})
            .sort(null);

        let container = svg.append('g')
            .attr('transform', 'translate(' + radius + ', ' + radius + ')');

        let g = container
            .selectAll('arc')
            .data(pie(dataset))
            .enter()
            .append('g')
            .attr('class', 'arc');

        let path = g.append('path')
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
            let percent = Math.round(1000 * d.data.count / total) / 10;
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

        return container;
    }

    /**
     * Draws a legend
     */
    function drawLegend(svg, color) {

        let legendRectSize = 18;
        let legendSpacing = 10;

        let legend = svg.append('g')
            .attr('overflow', 'scroll')
            .attr('transform', 'translate(' + (2 * radius) + ', 0)')
            .selectAll('.legend')
            .data(color.domain())
            .enter()
            .append('g')
            .attr('class', 'legend')
            .attr('transform', function (d, i) {
                let height = legendRectSize + legendSpacing;
                let vert = i * height;
                return 'translate(' + legendSpacing + ', ' + vert + ')';
            });

        legend.append('rect')
            .attr('width', legendRectSize)
            .attr('height', legendRectSize)
            .style('fill', color)
            .style('stroke', color);

        legend.append('text')
            .attr('x', legendRectSize + legendSpacing)
            .attr('y', '1em')  // legendRectSize - legendSpacing
            .text(function (d) {return d;});

        return legend;
    }

    // let dataCount = length(dataset);


    let width = 560;
    let height = 280;
    let radius = Math.min(width, height) / 2;

    let total = d3.sum(dataset.map(function (d) {return d.count;}));

    let tooltip = d3.select('#' + idName)
        .append('div')
        .attr('class', 'tooltip');

    tooltip.append('div').attr('class', 'label');
    tooltip.append('div').attr('class', 'count');
    tooltip.append('div').attr('class', 'percent');

    let color = d3.scaleOrdinal(d3.schemeCategory10);

    let svg = d3.select('#' + idName)
        .append('svg')
        .attr('overflow', 'hidden')
        .attr('width', width)
        .attr('height', height);


    let pieContainer = drawPie(svg, radius, dataset, color);
    drawLegend(svg, color);
}