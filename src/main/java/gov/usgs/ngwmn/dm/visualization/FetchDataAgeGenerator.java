package gov.usgs.ngwmn.dm.visualization;

import gov.usgs.ngwmn.dm.dao.FetchStatsDAO;

import java.sql.SQLException;

import com.google.visualization.datasource.datatable.DataTable;

public class FetchDataAgeGenerator extends SQLDataTableGenerator {

	private FetchStatsDAO dao;
	public FetchDataAgeGenerator(FetchStatsDAO dao) {
		this.dao = dao;		
	}

	@Override
	protected DataTable generateDT() throws SQLException, RuntimeException {
		return dao.welldataAgeData(rs2dt);
	}

}
