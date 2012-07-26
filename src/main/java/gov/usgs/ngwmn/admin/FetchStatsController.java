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
	
	@RequestMapping("agency/{agency}")
	public void agencyData(
			@PathVariable String agency,
			Writer writer)
	throws IOException
	{
		PrintWriter pw = new PrintWriter(writer);
		pw.printf("Hello there, Mr. %s!", agency);
	}
	
	@RequestMapping("hello")
	public String sayHello() {
		return "sayhello";
	}
	
	@RequestMapping("wombat")
	public String wombat() {
		return "redirect:agency/Wombat";
	}
	
	@RequestMapping("chart")
	public String showChart() {
		return "chart";
	}
	
	@RequestMapping("table")
	public void generateTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DataSourceHelper.executeDataSourceServletFlow(request, response, gen, false);
	}

	public void setGenerator(FetchStatsGenerator gen) {
		this.gen = gen;
	}
	
}
