package gov.usgs.ngwmn.dm.visualization;

import java.util.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;

public class ResultSetDataTable extends DataTable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public ResultSetDataTable(ResultSet data) throws DataSourceException {
		super();
		
		try {
			// first make the columns
			populateColumns(data);
			
			// then add all the data as rows
			populateData(data);
		} 
		catch (SQLException sqle) {
			DataSourceException dse = new DataSourceException(ReasonType.INTERNAL_ERROR, sqle.getMessage());
			dse.initCause(sqle);
			throw dse;
		}
	}

	private int colct;
	
	private void populateColumns(ResultSet data) throws SQLException {
		ResultSetMetaData rsmd = data.getMetaData();
		
		colct = rsmd.getColumnCount();
		// beware, JDBC column numbering convention is 1-based
		for (int c = 1; c <= colct; c++) {
			String id = rsmd.getColumnName(c);
			ValueType type = sqlTypeToVType(rsmd.getColumnType(c));
			String label = rsmd.getColumnLabel(c);
			ColumnDescription cd = new ColumnDescription(id, type, label); 
			
			super.addColumn(cd);
		}
	}

	private ValueType sqlTypeToVType(int columnType) {
		switch (columnType) {
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.VARCHAR:
			return ValueType.TEXT;
			
		case java.sql.Types.INTEGER:
		case java.sql.Types.NUMERIC:
		case java.sql.Types.DECIMAL:
			return ValueType.NUMBER;
			
		case java.sql.Types.DATE:
			return ValueType.DATE;
			
		}
		throw new RuntimeException(("Unmapped data type " + columnType));
	}
	
	// This is similar to ValueType.createValue(Object), without the dependency on IBM's Calendar
	private Value objectToValue(Object v, ValueType t) {
		Value val = null;
		
		switch (t) {
		case DATE:
			Date d = (Date)v;
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(d);
			val = new DateValue(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DATE)
			);
			break;
			
		case NUMBER:
			Number n = (Number)v;
			val = new NumberValue(n.doubleValue());
			break;
			
		case TEXT:
			val = new TextValue(v.toString());
			break;
			
		default:
			// TODO more data types...
			throw new RuntimeException("Unmapped data type " + t);
		}
		
		logger.trace("converted {} to {}", v, val);
		
		return val;
	}

	private void populateData(ResultSet data) throws SQLException, DataSourceException {
		while (data.next()) {
			TableRow tr = new TableRow();
			for (int i = 1; i <= colct; i++) {
				Object value = data.getObject(i);
				
				tr.addCell(objectToValue(value, super.getColumnDescription(i-1).getType()));
			}
			
			super.addRow(tr);
		}
	}
	
}
