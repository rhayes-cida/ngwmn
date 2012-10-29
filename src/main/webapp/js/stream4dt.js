
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
			var tuple = {x: xx[i], y: (dt.getValue(i,j) || 0), row: i, column: j, v:  (dt.getValue(i,j) || 0), label: dt.getColumnLabel(j)};
			vv[i] = tuple;
		}
		dataT.push(vv);
	}
	
	return dataT;
}

function plotFetchOutcomes(dataTable,id) {
	
var dataT = dt2xy(dataTable);

// var layout = d3.layout.stack().order('inside-out').offset("wiggle")(dataT);
var layout = d3.layout.stack()(dataT);

var n = dataTable.getNumberOfColumns()-1,
	m = dataTable.getNumberOfRows(),
    color = d3.interpolateRgb("#ff0000", "#0000ff");

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

var knownValues = ['DONE','EMPY','FAIL','null','SKIP'];
var clr = d3.scale.category10().domain(knownValues);

vis.selectAll("path")
    .data(layout)
    .enter().append("path")
    .style("fill", 
    		function(d,i) { 
    			// alert("d[i] is " + d[i]);
    			// alert("d.label is ", d.label);
    			// alert("d[i].label is ", d[i].label);
    			return clr(knownValues[i]);
    		})
    .attr("d", area);

vis.selectAll("path")
	.append("title")
	.text(function(d,i) { 
			//  "label for " + d + "," +i; 
			// d is entire vv array 
			return dataTable.getColumnLabel(i+1);
			});

var format8601 = d3.time.format("%Y-%m-%d");

/*vis.selectAll("path")
	.on('click', function(d,i) {
		// this shows wrong values:
		alert(dataTable.getColumnLabel(i+1) + " " + format8601(d[i].x) + ": " + d[i].v);
	});
*/
}


