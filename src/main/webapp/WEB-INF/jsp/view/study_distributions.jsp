
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>



<section>
    <svg class="histogram" width="500" height="800"></svg>

    <script src="/resources/AdapCompoundDb/js/histogram.js"></script>
    <%--<script>addHistogram("hist",[10,20,30,40,50,60]) </script>--%>

    <script>
        var dataSet = [40,25,61,82];
        var width = 500;
        var height = 800;

        var widthScale = d3.scale.linear()
            .domain([0,100])
            .range([0,width]);

        var color = d3.scale.linear()
            .domain([0,82])
            .range(["orange","green"])

        var axis = d3.svg.axis()
            .ticks(6)
            .scale(widthScale);

        var canvas = d3.select("section")
            .append("svg")
            .attr("width", width)
            .attr("height", height)
            .append("g")
            .attr("transform","translate(-50, 10)");

        var bars = canvas.selectAll("rect")
            .data(dataSet)
            .enter()
                .append("rect")
                .attr("width",function(d){ return widthScale(d);})
                .attr("height",25)
                .attr("fill", function (d){return color(d)})
                .attr("y", function(d,i){ return i * 40;});

        canvas.append("g")
            .attr("transform", "translate(0,150)")
            .call(axis);

    </script>

</section>