package gov.usgs.ngwmn.dm.visualization;

import java.sql.SQLException;

import gov.usgs.ngwmn.dm.dao.FetchStatsDAO;



import com.google.visualization.datasource.DataTableGenerator;
import com.google.visualization.datasource.datatable.DataTable;

public class FetchStatsAgencyGenerator extends SQLDataTableGenerator implements DataTableGenerator {

	private FetchStatsDAO dao;
	private String agency;
	public FetchStatsAgencyGenerator(FetchStatsDAO dao) {
		super();
		this.dao = dao;		
	}

	public String getAgency() {
		return agency;
	}
	public void setAgency(String agency) {
		this.agency = agency;
	}

	@Override
	protected DataTable generateDT() throws SQLException, RuntimeException {
		if (agency == null) {
			return dao.timeSeriesData(rs2dt);
		} else {
			return dao.timeSeriesAgencyData(agency, rs2dt);
		}
	}

}
