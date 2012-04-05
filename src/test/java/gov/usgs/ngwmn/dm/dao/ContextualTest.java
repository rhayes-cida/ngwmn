package gov.usgs.ngwmn.dm.dao;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContextTest.xml"})
// Cannot use this, because it overrides system property spring.profiles.active; use that instead.
// Or set environment SPRING_PROFILES_ACTIVE or SPRING_PROFILES_DEFAULT
// @ActiveProfiles("local")
public abstract class ContextualTest {
	
	@Autowired
	protected ApplicationContext ctx;

	@BeforeClass
	public static void setupNaming() throws Exception {
		final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		try {
			builder.activate();
		} catch (IllegalStateException ise) {
			// already had a naming provider; ignore
		}
	}

}
