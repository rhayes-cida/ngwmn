package gov.usgs.ngwmn.dm.visualization;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.DataTable;

public class DataTableExtractor implements
		ResultSetExtractor<DataTable> {
	
	@Override
	public DataTable extractData(ResultSet rs) 
			throws SQLException, DataAccessException
	{
		try {
			return new ResultSetDataTable(rs);
		} catch (DataSourceException e) {
			throw new RuntimeException(e);
		}
	}
}