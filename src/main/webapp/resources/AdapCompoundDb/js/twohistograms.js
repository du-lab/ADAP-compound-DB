//TODO: Give consistent names to histogram.js and tag_distributions.js
// For example: histogram.js and twohistograms.js

function addClusterTagsHistogram(idName, tag, dataSet) {
    var integrationValues = JSON.parse( dataSet );
    var tagKeys = d3.keys( integrationValues );
    var values = d3.values( integrationValues );
    var clusterValuesList = [];
    var alldbValuesList =[];
    for (var m = 0; m < tagKeys.length; m++) {
            var clusterValue = values[m]["cluster"];
            var alldbValue = values[m]["alldb"];
            clusterValuesList.push(clusterValue);
            alldbValuesList.push(alldbValue);
    }

    var padding = {top: 20, right: 20, bottom: 20, left: 20};
    var width = 400 - padding.left - padding.right;
    var height = (tagKeys.length * 60 + 100);

    var xScaleToAlldb = d3.scaleLinear()
        .domain([ d3.max(alldbValuesList)*1.2,0])
        .range( [-width/2,0] );

    var xScaleToCluster = d3.scaleLinear()
        .domain( [0, d3.max(clusterValuesList)*1.2])
        .range( [ width/2,width] );

    var yScale = d3.scaleLinear()
        .domain( [0, tagKeys.length * 60] )
        .range( [(tagKeys.length * 60), -5] );


    var xAxisToCluster = d3.axisBottom()
        .ticks( 5 )
        .tickSize( 2 )
        .scale( xScaleToCluster );

    var xAxisToAlldb = d3.axisBottom()
        .ticks( 5 )
        .tickSize( 2 )
        .scale(xScaleToAlldb );

    var yAxis = d3.axisRight()
        .ticks( 0 )
        .tickSize( 0 )
        .scale( yScale );

    var svg = d3.select( "#" + idName )
        .append( "svg" )
        .attr( "width", width )
        .attr( "height", height )
        .append( "g" )
        .attr( "transform", "translate(5, 5)" );

    // for grid in x axis
    var grid1 = svg.append( 'g' )
        .attr( 'class', 'grid' )
        .attr( "transform", "translate("+ 0 +", " + ((tagKeys.length) * 60 + padding.left) + ")" );

    var grid2 = svg.append( 'g' )
        .attr( 'class', 'grid' )
        //TODO: too long line. See Du-Lab's Source Code Standards
        .attr( "transform", "translate("+ xScaleToAlldb(-d3.max(alldbValuesList)) * 1.2 +", " + ((tagKeys.length) * 60 + padding.left) + ")" );

    // plot cluster bar chart
    svg.selectAll( "rect.right" )
        .data(clusterValuesList)
        .enter()
        .append( "rect" )
        .attr( "width", function (d) {
            return (xScaleToCluster(d) - xScaleToCluster(0));
            // return (xScaleToCluster(d)/2);
        } )
        .attr( "height", 40 )
        .attr( "fill", "#ffb47c" )
        .attr( "x", width/2 )
        .attr( "y", function (d, i) {
            return i * 60 + padding.bottom
        } );

    // plot Alldb bar chart
    svg.selectAll( "rect.left" )
        .data(alldbValuesList)
        .enter()
        .append( "rect" )
        .attr( "width", function (d) {
            return (xScaleToAlldb(-d)) ;
        } )
        .attr( "height", 40 )
        .attr( "fill", "#b47cff" )
        .attr( "x",function (d) { return (xScaleToAlldb(d-d3.max(alldbValuesList)*1.2));
        })
        .attr( "y", function (d, i) {
            return i * 60 + padding.bottom;
        } );

    // display tag value
    svg.selectAll( "text" )
        .data( tagKeys )
        .enter()
        .append( "text" )
        .attr( "dx", "20" )
        .attr( "dy", "1.5em" )
        .attr( "y", function (d, i) {
            return i * 60 + padding.bottom;
        } )
        .attr( "x", width/2-padding.right *2 )
        .attr( "stroke", "#000000" )
        .attr( "stroke-width", "1" )
        .text( function (d) {
            return d;
        } );

    // text label for the x axis
    svg.append( "text" )
        .data( tag )
        .attr( "transform",
            "translate(" + (width / 2) + " ," +
            ((tagKeys.length * 60) + 35 + padding.bottom) + ")" )
        .style( "text-anchor", "middle" )
        .attr( "stroke", "#000000" )
        .attr( "stroke-width", "1.5" )
        .text( tag );

    // plot x axis to cluster
    svg.append( "g" )
        .attr( "transform", "translate("+ 0 +" , " + ((tagKeys.length) * 60 + padding.left) + ")" )
        .call( xAxisToCluster );

    // plot x axis to alldb
    svg.append( "g" )
    //TODO: too long line. See Du-Lab's Source Code Standards
        .attr( "transform", "translate(" + xScaleToAlldb(-d3.max(alldbValuesList)) * 1.2  +", " + ((tagKeys.length) * 60 + padding.left) + ")" )
        .call( xAxisToAlldb );

    // plot y axis
    svg.append( "g" )
        .attr( "transform", "translate(  "+ width/2  +", " + padding.bottom +")" )
        .call( yAxis );

    // plot grid
    grid1.call( d3.axisBottom( xScaleToCluster )
        .ticks( 5 )
        .tickSize( -(tagKeys.length * 60), 0 )
        .tickFormat( '' ) );

    grid2.call( d3.axisBottom( xScaleToAlldb )
        .ticks( 5 )
        .tickSize( -(tagKeys.length * 60), 0 )
        .tickFormat( '' ) );
}
