
var dataRaw = [
             {time: new Date("2012-07-23"), v1: 10, v2:9},
             {time: new Date("2012-07-24"), v1: 11, v2: 6},
             {time: new Date("2012-07-25"), v1: 7, v2: 3},
             {time: new Date("2012-07-28"), v1: 6, v2: 4},
             {time: new Date("2012-07-29"), v1: 0, v2: 1},
             {time: new Date("2012-07-30"), v1: 0, v2: 10},
             {time: new Date("2012-08-01"), v1: 4, v2: 2},
             {time: new Date("2012-08-02"), v1: 6, v2: 3}
             ];

var startDate = new Date("2012-01-01");
var dataT = [
             dataRaw.map(function(t) {
            	 return {x: (t.time.valueOf() - startDate.valueOf())/(24*60*60*1000.0), y: t.v1}
             }),
             dataRaw.map(function(t) {
            	 return {x: (t.time.valueOf() - startDate.valueOf())/(24*60*60*1000.0), y: t.v2}
             })
             ];

var layout = d3.layout.stack().offset("wiggle")(dataT);

var n = 2,
	m = dataRaw.length,
    color = d3.interpolateRgb("#aad", "#556");

var width = 960,
    height = 500,
    minx = d3.min(layout, function(d) {
        return d3.min(d, function(d) {
            return d.x;
        });
      }),
    maxx = d3.max(layout, function(d) {
          return d3.max(d, function(d) {
              return d.x;
          });
        }),
    my = d3.max(layout, function(d) {
      return d3.max(d, function(d) {
          return d.y0 + d.y;
      });
    });

var datebounds = [new Date("2012-07-15"), new Date("2012-08-15")
                  ];


// apply scaling to display area
var area = d3.svg.area()
.x(function(d) { return scale(d.x, minx, maxx, 0, width); })
.y0(function(d) { return height - d.y0 * height / my; })
.y1(function(d) { return height - (d.y + d.y0) * height / my; });



// synthetic data is n x m arrays of {x,y} tuples.

var vis = d3.select("#chart")
  	.append("svg")
    .attr("width", width)
    .attr("height", height);

vis.selectAll("path")
    .data(layout)
    .enter().append("path")
    .style("fill", function() { return color(Math.random()); })
    .attr("d", area);

