package gov.usgs.ngwmn.dm.visualization;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Table.Cell;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;

public class FetchStatsGeneratorTest extends ContextualTest {

	private FetchStatsGenerator victim;
	
	@Before
	public void init() {
		victim = ctx.getBean(FetchStatsGenerator.class);
	}
	
	@Test
	public void testGenerateDataTable() throws Exception {
		DataTable dt = victim.generateDataTable(null, null);
		
		assertTrue(dt.containsColumn("FETCH_DATE"));
		assertTrue(dt.containsColumn("SUCCESS"));
		assertTrue(dt.containsColumn("EMPTY"));
		assertTrue(dt.containsColumn("FAIL"));
		assertTrue(dt.containsColumn("ATTEMPTS"));
		
		int nr = dt.getNumberOfRows();
		assertTrue("some rows", nr > 0);
		
		for (int r = 0; r < nr; r++) {
			for (int c = 0; c < 3; c++) {
				TableCell cell = dt.getCell(r, c);
				assertNotNull("cell",cell);
				assertNotNull("value",cell.getValue());				
			}
		}
	}

}
