package gov.usgs.ngwmn.dm.visualization;

import gov.usgs.ngwmn.dm.dao.FetchStatsDAO;

import java.sql.SQLException;

import com.google.visualization.datasource.datatable.DataTable;

public class StatsTableGenerator extends SQLDataTableGenerator {

	private FetchStatsDAO dao;
	public StatsTableGenerator(FetchStatsDAO dao) {
		this.dao = dao;		
	}

	@Override
	protected DataTable generateDT() throws SQLException, RuntimeException {
		return dao.viewData(null, rs2dt);
	}

}
