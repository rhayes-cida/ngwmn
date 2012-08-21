package gov.usgs.ngwmn.admin;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.text.SimpleDateFormat;

import gov.usgs.ngwmn.admin.stats.JitTree;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

public class JITDataControllerIntegrationTest extends ContextualTest {

	private JITDataController victim;
	
	@Before
	public void setup() {
		victim = ctx.getBean(JITDataController.class);
	}
	
	@Test
	public void test1() throws Exception {
		Date d;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		d = sdf.parse("2012-07-12");
		
		JitTree result = victim.getTree(d);
		
		assertNotNull("result tree", result);
	}

}
