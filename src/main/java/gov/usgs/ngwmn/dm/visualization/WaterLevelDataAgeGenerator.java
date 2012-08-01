package gov.usgs.ngwmn.dm.visualization;

import gov.usgs.ngwmn.dm.dao.FetchStatsDAO;

import java.sql.SQLException;

import com.google.visualization.datasource.datatable.DataTable;

public class WaterLevelDataAgeGenerator extends SQLDataTableGenerator {

	private FetchStatsDAO dao;
	public WaterLevelDataAgeGenerator(FetchStatsDAO dao) {
		this.dao = dao;		
	}

	@Override
	protected DataTable generateDT() throws SQLException, RuntimeException {
		return dao.welldataAgeData(rs2dt);
	}

}
