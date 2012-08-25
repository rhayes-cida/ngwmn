
// munge a Google vis data table to a 2-dimensional array of (x,y) tuples
function dt2xy(dt) {
	var dataT = [];
	var i,j;

	// first column is x, assumed to be date
	var xx = [];
	for (i = 0; i < dt.getNumberOfRows(); i++) {
		xx[i] = (dt.getValue(i,0) || new Date(0));
	}
	
	for (j = 1; j < dt.getNumberOfColumns(); j++) {
		var vv = [];
		for (i = 0; i < dt.getNumberOfRows(); i++) {
			var tuple = {x: xx[i], y: (dt.getValue(i,j) || 0)};
			vv[i] = tuple;
		}
		dataT.push(vv);
	}
	
	return dataT;
}

function plotFetchOutcomes(dataTable,id) {
	
var dataT = dt2xy(dataTable);

var layout = d3.layout.stack().order('inside-out').offset("wiggle")(dataT);

var n = dataTable.getNumberOfColumns()-1,
	m = dataTable.getNumberOfRows(),
    color = d3.interpolateRgb("#aad", "#556");

var width = 960,
    height = 200,
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

// apply scaling to display area
var area = d3.svg.area()
.x(function(d) { return scale(d.x, minx, maxx, 0, width); })
.y0(function(d) { return height - d.y0 * height / my; })
.y1(function(d) { return height - (d.y + d.y0) * height / my; });

var vis = d3.select("#"+id)
  	.append("svg")
    .attr("width", width)
    .attr("height", height);

vis.selectAll("path")
    .data(layout)
    .enter().append("path")
    .style("fill", function() { return color(Math.random()); })
    .attr("d", area);

}


