package gov.usgs.ngwmn.dm.visualization;

import java.sql.SQLException;

import gov.usgs.ngwmn.dm.dao.FetchStatsDAO;

import javax.servlet.http.HttpServletRequest;
import javax.sql.RowSet;

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataTableGenerator;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;

public class FetchStatsGenerator implements DataTableGenerator {

	private FetchStatsDAO dao;
	
	public FetchStatsGenerator(FetchStatsDAO dao) {
		super();
		this.dao = dao;		
	}

	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request)
			throws DataSourceException {
		
		try {
			RowSet rs = dao.overallTimeSeries();
			
			return new ResultSetDataTable(rs);
		} catch (SQLException e) {
			DataSourceException dse = new DataSourceException(ReasonType.OTHER, "Hmmm");
			dse.initCause(e);
			throw dse;
		}
	}

	@Override
	public Capabilities getCapabilities() {
		return Capabilities.NONE;
	}

}
