package gov.usgs.ngwmn.dm.io.transform;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stream an Excel 2007 (xlsx) file.  These are xml documents inside a zip file.
 */
public class XlsxOutputStream extends SXSSFWorkbook implements Closeable {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String DATE_FORMAT = "m/d/yy h:mm";

	/** The sheet that we are working on. */
	private Sheet sheet;
	
	/** The current row reference **/
	private Row row;

	/** Keep track of the row we are writing. 0-based. */
	private int rowCount;

	private CellStyle dateStyle;
	
	private OutputStream out;
	
	
	// TODO this has a problem!!!! The SXSSFWorkbook caches to a temp file rather than streaming to the given out
	// TODO also, after writing to the out it will close it disallowing further streaming
	
	public XlsxOutputStream(OutputStream os) {
		super(10);
		out = os;
	}
	
	/**
	 * Write the data.  Null cells are skipped to cut some of the bloat out of the file.
	 * 
	 * @throws IOException when issues with the streaming.
	 */
	public void createRow(Iterable<?> values) throws IOException {
		logger.debug("creating row of values");
		beginRow();
		int column = 0;
		for (Object obj : values) {
			if (obj instanceof BigDecimal) {
				createCell(column, ((BigDecimal) obj).doubleValue());
			} else if (obj instanceof Date) {
				createCell(column, (Date) obj);
			} else if (null != obj) {
				createCell(column, obj.toString());
			}
			column++;
		}
		endRow();
	}

	/**
	 * Once we are done with the filter, the sheet needs it's ending tag.
	 */
	public void close() throws IOException {
		endSheet();
		write(out);
		out.close(); // TODO this might not be appropriate here
	}

	/** 
	 * Output the xml required at the beginning of a sheet.
	 * @throws IOException  when issues with the streaming.
	 */
	public void beginSheet(String name) throws IOException {
		logger.debug("begin sheet {}", name);
		sheet = createSheet(name);
	}

	/** 
	 * Output the xml required at the end of sheet.
	 * @throws IOException when issues with the streaming.
	 */
	public void endSheet() throws IOException {
		logger.debug("end   sheet {}", sheet.getSheetName());
		row = null;
		sheet = null;
	}

	/**
	 * Output the xml required at the beginning of a row. 
	 * @throws IOException when issues with the streaming.
	 */
	public void beginRow() throws IOException {
		logger.debug("begin row {}", rowCount);
		row = sheet.createRow(rowCount);
	}

	/**
	 * Output the xml required at the end of a row and increment the counter.
	 * @throws IOException when issues with the streaming.
	 */
	public void endRow() throws IOException {
		logger.debug("end   row {}", rowCount);
		rowCount++;
		row = null;
	}

	/** 
	 * Output the xml for a string cell at the given index in the current row. 
	 * @param columnIndex - 0-based index of the cell within the current row.
	 * @param value - the string value to populate the column with.  The method handles escaping necessary characters.
	 * @throws IOException when issues with the streaming.
	 */
	public void createCell(final int columnIndex, final String value) throws IOException {
		row.createCell(columnIndex).setCellValue(value);
	}

	/** 
	 * Output the xml for a numeric cell at the given index in the current row. 
	 * @param columnIndex - 0-based index of the cell within the current row.
	 * @param value - the numeric value to populate the column with.
	 * @throws IOException when issues with the streaming.
	 */
	public void createCell(final int columnIndex, final double value) throws IOException {
		row.createCell(columnIndex).setCellValue(value);
	}
	
	/** 
	 * Output the xml for a numeric cell at the given index in the current row. 
	 * @param columnIndex - 0-based index of the cell within the current row.
	 * @param value - the numeric value to populate the column with.
	 * @throws IOException when issues with the streaming.
	 */
	public void createCell(final int columnIndex, final Date value) throws IOException {
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(value);
	    cell.setCellStyle( getDateStyle() );
	}
	
	protected CellStyle getDateStyle() {
		if (dateStyle == null) {
			CreationHelper createHelper = getCreationHelper();
		    dateStyle = createCellStyle();
		    dateStyle.setDataFormat( createHelper.createDataFormat().getFormat(DATE_FORMAT) );
		}
		return dateStyle;
	}
	
	
	
}