package gov.usgs.ngwmn.admin;

import gov.usgs.ngwmn.dm.visualization.FetchStatsGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.visualization.datasource.DataSourceHelper;

@Controller
@RequestMapping("/fetchlog")
public class FetchStatsController {

	private FetchStatsGenerator gen;
	
	@RequestMapping("chart")
	public String showChart() {
		return "chart";
	}
	
	@RequestMapping("table")
	public void generateTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Could do this much more efficiently using Jackson streaming
		DataSourceHelper.executeDataSourceServletFlow(request, response, gen, false);
	}

	public void setGenerator(FetchStatsGenerator gen) {
		this.gen = gen;
	}
	
}
