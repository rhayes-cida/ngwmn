package gov.usgs.ngwmn.admin;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.ibm.icu.text.SimpleDateFormat;

import gov.usgs.ngwmn.admin.stats.JitTree;
import gov.usgs.ngwmn.dm.dao.ContextualTest;

@ContextConfiguration({"classpath:applicationContextTest.xml","file:src/main/webapp/WEB-INF/SpringMVC-servlet.xml"})
public class JITDataControllerIntegrationTest extends ContextualTest {

	private JITDataController victim;
	
	@Autowired
	protected ApplicationContext ctx;

	@Before
	public void setup() {
		victim = ctx.getBean(JITDataController.class);
	}
	
	@Test
	public void test1() throws Exception {
		Date d;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		d = sdf.parse("2012-12-29");
		
		JitTree result = victim.getTree(d);
		
		assertNotNull("result tree", result);
	}

}
