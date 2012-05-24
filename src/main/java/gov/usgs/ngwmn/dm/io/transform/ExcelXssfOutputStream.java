package gov.usgs.ngwmn.dm.io.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.io.ByteStreams;

/**
 * Stream an Excel 2007 (xlsx) file.  These look a lot like a zip file.  The process works
 * by creating a template, streaming it's parts (minus the sheet) to a zip stream, then 
 * streaming the sheet data, and finishing the zip stream (but not closing the underlying stream - 
 * that is done by the calling action).  This is all accomplished by calling the write method once for/with
 * each row of data and calling the finishWorkbook method after the last data row is written.  
 * @author drsteini
 * 
 */
public class ExcelXssfOutputStream extends MapOutputStream {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private static final String STRING_DATA = "inlineStr";
	private static final String NUMBER_DATA = "n";
	

	/** Is this the first write to the stream. */
	private boolean firstTime = true;

	/** The work book to which we will add a sheet for this export. */
	private XSSFWorkbook workbook = null;

	/** The sheet that we are working on. */
	private XSSFSheet sheet = null;

	/** Within the workbook, the name of the spreadsheet tab. */
	private String sheetName = "report";

	/** Name of the zip entry holding sheet data, e.g. /xl/worksheets/report.xml */
	private String sheetRef = "";

	/** Has this been initialized. */
	private boolean initialized = false;

	/** Keep track of the row we are writing. 0-based. */
	private int rowCount = 0;

	/** Zip output stream for the spreadsheet. */
	private ZipOutputStream zos;
	
//	private List<String> columnHeaders;
	
	public ExcelXssfOutputStream(OutputStream out) {
		super(out);
	}

	public ExcelXssfOutputStream(OutputStream out, XSSFWorkbook targetWB) {
		this(out);
		this.workbook = targetWB;
	}

	public ExcelXssfOutputStream(OutputStream out, String sheetName) {
		this(out);
		this.sheetName = sheetName;
	}


	/**
	 * Passes to parent to extract object. If object is available, process.
	 * This expects a serialized map of string:data pairs where the strings are also used column header labels
	 * {@inheritDoc}
	 */
	public void write(final byte[] b, final int off, final int len) throws IOException {
		
		super.write(b, off, len); // sets up a result map object from bytes

		if (null == dataMap) return;

		init();

		if (firstTime) {
			firstTime = false;
			createHeader();
		}

		// TODO how is this reliably ordered?
		createRow( dataMap.values() );
	}

	/**
	 * Initialize a workbook if needed, a sheet to work on and write the
	 * template to the stream.
	 * 
	 * @throws IOException when issues with the streaming.
	 */
	private void init() throws IOException {
		if (initialized) return;
			
		if (null == workbook) {
			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet(sheetName);
		}

		sheetRef = sheet.getPackagePart().getPartName().getName().substring(1);
		zos = new ZipOutputStream(out);

		// stream the template - don't include the empty sheet (sheetRef).
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		workbook.write(os);
		ZipInputStream strm = new ZipInputStream(new ByteArrayInputStream(os.toByteArray()));
		ZipEntry ze;
		while (null != (ze = strm.getNextEntry())) {
			if ( ! ze.getName().equals(sheetRef) ) {
				zos.putNextEntry(new ZipEntry(ze.getName()));
				ByteStreams.copy(strm, zos);
			}
		}

		initialized = true;
	}

	/**
	 * Write the header.
	 * 
	 * @throws IOException when issues with the streaming.
	 */
	private void createHeader() throws IOException {
		zos.putNextEntry(new ZipEntry(sheetRef));
		beginSheet();

//		columnHeaders = new ArrayList<String>( dataMap.keySet() );
		// TODO how is this reliably ordered?
		createRow( dataMap.keySet() );
	}

	/**
	 * Write the data.  Null cells are skipped to cut some of the bloat out of the file.
	 * 
	 * @throws IOException when issues with the streaming.
	 */
	private void createRow(Iterable<? extends Object> values) throws IOException {
		beginRow();
		int cellCount = 0;
		for (Object obj : values) {
			if (null != obj) {
				if (obj instanceof BigDecimal) {
					createCell(cellCount, ((BigDecimal) obj).doubleValue());
				} else {
					createCell(cellCount, obj.toString());
				}
			}
			cellCount++;
		}
		endRow();
	}

	/**
	 * Once we are done with the filter, the sheet needs it's ending tag.
	 */
	public void finishWorkbook() {
		try {
			endSheet();
			zos.finish();
		} catch (Exception e) {
			logger.error("Problem encountered finishing the workbook.", e);
			throw new RuntimeException(e);
		}
	}

	/** 
	 * Converts a string to a byte array and stream it.
	 * @param in the string to be streamed.
	 * @param out the stream to write it to.
	 * @throws IOException when issues with the streaming.
	 */
	private void copyString(String data, OutputStream out) throws IOException {
		// TODO this is not the most efficient way to write strings to a stream
		ByteStreams.copy(new ByteArrayInputStream(data.getBytes()), out);
	}



	/** 
	 * Output the xml required at the beginning of a sheet.
	 * @throws IOException  when issues with the streaming.
	 */
	public void beginSheet() throws IOException {
		copyString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">",
				zos);
		copyString("<sheetData>\n", zos);
	}

	/** 
	 * Output the xml required at the end of sheet.
	 * @throws IOException when issues with the streaming.
	 */
	public void endSheet() throws IOException {
		copyString("</sheetData>", zos);
		copyString("</worksheet>", zos);
	}

	/**
	 * Output the xml required at the beginning of a row. 
	 * @throws IOException when issues with the streaming.
	 */
	public void beginRow() throws IOException {
		copyString("<row r=\"" + (rowCount + 1) + "\">\n", zos);
	}

	/**
	 * Output the xml required at the end of a row and increment the counter.
	 * @throws IOException when issues with the streaming.
	 */
	public void endRow() throws IOException {
		copyString("</row>\n", zos);
		rowCount++;
	}

	/** 
	 * Output the xml for a string cell at the given index in the current row. 
	 * @param columnIndex - 0-based index of the cell within the current row.
	 * @param value - the string value to populate the column with.  The method handles escaping necessary characters.
	 * @throws IOException when issues with the streaming.
	 */
	public void createCell(final int columnIndex, final String value) throws IOException {
		beginCell(columnIndex, STRING_DATA);
		createCellValue(value);
		endCell();
	}

	/** 
	 * Output the xml for a numeric cell at the given index in the current row. 
	 * @param columnIndex - 0-based index of the cell within the current row.
	 * @param value - the numeric value to populate the column with.
	 * @throws IOException when issues with the streaming.
	 */
	public void createCell(final int columnIndex, final double value) throws IOException {
		beginCell(columnIndex, NUMBER_DATA);
		createCellValue(value);
		endCell();
	}
	
	private void beginCell(int columnIndex, String type) throws IOException {
		String ref = new CellReference(rowCount, columnIndex).formatAsString();
		
		StringBuilder buff = new StringBuilder();
		
		buff.append("<c r=\"")
		    .append(ref)
		    .append("\" t=\"")
		    .append(type)
		    .append("\"")
			.append(">");
		copyString(buff.toString(), zos); // TODO toString then back to bytes
	}
	private void endCell() throws IOException {
		copyString("</c>", zos);
	}
	
	private void createCellValue(String value) throws IOException {
		copyString("<is><t>" + StringEscapeUtils.escapeXml(value) + "</t></is>", zos);
	}
	private void createCellValue(double value) throws IOException {
		copyString("<v>" + value + "</v>", zos);
	}
}