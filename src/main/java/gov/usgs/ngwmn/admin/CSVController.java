package gov.usgs.ngwmn.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/csv")
public class CSVController {

	private DataSource datasource;

	public CSVController(DataSource datasource) {
		this.datasource = datasource;
	}

	private long getCacheID(JdbcTemplate t, String agency, String site) {
		String query = "SELECT cachetable.WATERLEVEL_CACHE_ID " +
				"FROM GW_DATA_PORTAL.WATERLEVEL_CACHE cachetable " +
				"WHERE cachetable.agency_cd = ? and cachetable.site_no = ? " +
				"AND cachetable.published = 'Y' " +
				"AND cachetable.xml IS NOT NULL " +
				"ORDER BY cachetable.fetch_date DESC ";
		
		long val;
		
		t.setMaxRows(1);
		try {
			val = t.queryForLong(query, agency, site);
		}
		catch (EmptyResultDataAccessException ee) {
			val = -1;
		} finally {
			t.setMaxRows(0);
		}
		return val;
	}
	
	/** Produce waterlevels for the given site, in format expected by Dygraphs:
	 * first line has headers: time,value
	 * then data as comma-separated values

	 * @param agency
	 * @param site
	 * @throws IOException
	 */
	@RequestMapping("waterlevel/{agency}/{site}")
	public void generateTable(
			@PathVariable String agency,
			@PathVariable String site,
			Writer writer) 
	throws IOException {
		
		// extract the data as a CSV
		String query = 
				"	select" +
				" 	  xq.*	from " +
				"		gw_data_portal.waterlevel_cache qc," +
				"		XMLTable(" +
				"		XMLNAMESPACES(" +
				"		  'http://www.wron.net.au/waterml2' AS \"wml2\"," +
				"		  'http://www.opengis.net/om/2.0' AS \"om\"," +
				"		  'http://www.opengis.net/swe/2.0' AS \"swe\"" +
				"		)," +
				"		'for $r in //wml2:WaterMonitoringObservation/om:result/wml2:TimeSeries/wml2:element" +
				"		let " +
				"		$p := $r/wml2:TimeValuePair" +
				"		return $p" +
				"		'" +
				"		passing qc.xml" +
				"		columns " +
				"		\"DT\" varchar2(12) path 'ora:replace(substring(wml2:time,1,10),\"-00\",\"-01\")'," +
				"    	\"FULLDATE\" varchar2(40) path 'wml2:time'," +
				"		\"VAL\" number path 'wml2:value/swe:Quantity/swe:value'," +
				"		\"UNITS\" varchar(40) path 'wml2:value/swe:Quantity/swe:uom/@code'," +
				"    	\"COMMENT\" varchar(80) path 'wml2:comment'" +
				"		) xq" +
				"	where qc.waterlevel_cache_id = ? " +
				"	order by xq.FULLDATE asc ";
		
		JdbcTemplate t = new JdbcTemplate(datasource);
		
		long id = getCacheID(t, agency, site);
		
		final PrintWriter pw = new PrintWriter(writer);
		pw.println("time,value");

		t.query(query, new RowCallbackHandler() {
			
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				// Process the current row in the result set
				
				// TODO clean up date (using Joda time?) or use truncated date column?
				String time = rs.getString("FULLDATE");
				String value = rs.getString("VAL");
				
				pw.append(time).append(",").append(value);
				pw.println();
				
			}
		}, id);
	}
}
