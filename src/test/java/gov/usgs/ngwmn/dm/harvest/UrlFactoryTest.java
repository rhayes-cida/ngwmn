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
		System.setProperty("cocoon", "http://javadevp/cocoon");		
	}
	
	@Before
	public void setUp() throws Exception {
		victim = ctx.getBean(BetterUrlFactory.class);
	}

	@Test
	public void testResolve() {
		WellDataType t = WellDataType.ALL;
		
		String u = victim.resolve(t, "MBMG", "3445");
		
		assertEquals("http://javadevp/cocoon/gin/gwdp/MBMG?featureId=3445", u);
	}

}
