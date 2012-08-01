package gov.usgs.ngwmn.dm.visualization;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataTableGenerator;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;

/** Intended to bounce down to a Spring JDBC DAO that uses the DataTableExtractor to turn a resultSet into a dataTable.
 * @author rhayes
 *
 */
public abstract class SQLDataTableGenerator
implements DataTableGenerator
{

	protected final ResultSetExtractor<DataTable> rs2dt = new DataTableExtractor();

	public SQLDataTableGenerator() {
		super();
	}

	protected abstract DataTable generateDT() throws SQLException,
			RuntimeException;

	@Override
	public DataTable generateDataTable(Query query, HttpServletRequest request)
			throws DataSourceException {
				
				try {
					return generateDT();
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