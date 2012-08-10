package gov.usgs.ngwmn.admin;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;

import javax.sql.DataSource;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.Before;
import org.junit.Test;

public class CSVControllerIntegrationTest extends ContextualTest {

	private CSVController victim;
	
	@Before
	public void setup() {
		DataSource ds = getDataSource();
		victim = new CSVController(ds);
	}
	
	@Test
	public void test() throws Exception {
		Writer w = new StringWriter();
		victim.generateTable("USGS", "402734087033401", w);
		
		assertFalse("empty", w.toString().isEmpty());
	}

}
