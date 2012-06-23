package gov.usgs.ngwmn.dm.harvest;

import static org.junit.Assert.*;
import gov.usgs.ngwmn.WellDataType;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:urlFactoryContext.xml"})
public class UrlFactoryTest {

	@Autowired
	protected ApplicationContext ctx;

	private BetterUrlFactory victim;
	
	@BeforeClass
	public static void setSystemProperty() {
		System.setProperty("cocoon", "http://cida-wiwsc-javadevp.er.usgs.gov:8080/cocoon");		
	}
	
	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean(BetterUrlFactory.class);
	}

	@Test
	public void testALL() {
		WellDataType t = WellDataType.ALL;
		
		String u = victim.resolve(t, "MBMG", "1388");
		
		assertEquals("http://cida-wiwsc-javadevp.er.usgs.gov:8080/cocoon/gin/gwdp/cache/download/xls/MBMG?featureId=1388", u);
	}

	@Test
	public void testWQ() {
		WellDataType t = WellDataType.QUALITY;
		
		String u = victim.resolve(t, "MBMG", "1388");
		
		assertEquals("http://cida-wiwsc-javadevp.er.usgs.gov:8080/cocoon/gin/gwdp/agency/MBMG/qw?mimeType=xml&siteid=1388", u);
	}

}
