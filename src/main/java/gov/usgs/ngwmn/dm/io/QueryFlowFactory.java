package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import au.com.bytecode.opencsv.CSVWriter;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.aggregate.Flow;
import gov.usgs.ngwmn.dm.io.aggregate.FlowFactory;
import gov.usgs.ngwmn.dm.spec.Specifier;

/** Produce the results of SQL query as a CSV stream.
 * 
 * @author rhayes
 *
 */
public abstract class QueryFlowFactory implements FlowFactory {

	private DataSource datasource;
	private Specifier spec;
	private Supplier<OutputStream> sos;
	
	abstract String getQuery();
	
	/** Throw an exception if the type is not as expected
	 * 
	 * @param wellDataType
	 */
	abstract void checkType(WellDataType wellDataType);
	
	private class QueryFlow implements Flow {

		private String agency;
		private String site;
		private Date beginDate;
		private Date endDate;
		
		QueryFlow() {
			agency = spec.getAgencyID();
			site = spec.getFeatureID();
			beginDate = spec.getBeginDate();
			endDate = spec.getEndDate();
		}
		
		
		@Override
		public Void call() throws Exception {
			
			Void valu;
			OutputStream os = sos.begin();
			
			final Writer writer = new OutputStreamWriter(os);
			try {
				ResultSetExtractor<Void> rse = new ResultSetExtractor<Void>() {
	
					@Override
					public Void extractData(ResultSet rs) throws SQLException,
							DataAccessException {
						CSVWriter cw = new CSVWriter(writer);
						try {
							cw.writeAll(rs, true);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						
						return null;
					}
				};
				
				String query = getQuery();
	
				JdbcTemplate t = new JdbcTemplate(datasource);
				valu = t.query(query, rse, agency, site, beginDate, endDate);
			} finally {
				writer.close();
			}
			
			return valu;
		}
		
	}
	
	@Override
	public Flow makeFlow(Specifier spec, Supplier<OutputStream> out)
			throws IOException {
		
		checkType(spec.getTypeID());
		
		this.spec = spec;
		this.sos = out;
		
		return new QueryFlow();
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

}
