package gov.usgs.ngwmn.admin;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;

import javax.sql.DataSource;

import gov.usgs.ngwmn.dm.dao.ContextualTest;

import org.junit.Before;
import org.junit.Test;

public class CacheSizeControllerIntegrationTest extends ContextualTest {

	private CacheSizeController victim;
	
	@Before
	public void setup() {
		DataSource ds = getDataSource();
		victim = new CacheSizeController(ds);
	}
	
	@Test
	public void test() throws Exception {
		Writer w = new StringWriter();
		victim.generateTable(w);
		
		String v = w.toString();
		assertFalse("empty", v.isEmpty());
		assertTrue("has header", v.startsWith("time,quality,log,waterlevel\n"));
		
		assertTrue("has data", v.contains("2012"));
	}

}
