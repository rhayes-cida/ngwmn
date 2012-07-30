package gov.usgs.ngwmn.dm.visualization;

import java.sql.SQLException;

import gov.usgs.ngwmn.dm.dao.FetchStatsDAO;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataTableGenerator;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;

public class FetchStatsAgencyGenerator implements DataTableGenerator {

	private FetchStatsDAO dao;
	private String agency;
	private final ResultSetExtractor<DataTable> rs2dt = new DataTableExtractor();
	
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
	public DataTable generateDataTable(Query query, HttpServletRequest request)
			throws DataSourceException {
		
		try {
			if (agency == null) {
				return dao.timeSeriesData(rs2dt);
			} else {
				return dao.timeSeriesAgencyData(agency, rs2dt);
			}
		} 
		catch (RuntimeException rte) {
			if (rte.getCause() instanceof DataSourceException) {
				throw (DataSourceException)rte.getCause();
			}
			else throw rte;
		}
		catch (SQLException e) {
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
