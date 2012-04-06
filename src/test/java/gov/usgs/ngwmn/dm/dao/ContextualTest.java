package gov.usgs.ngwmn.dm.dao;

import gov.usgs.ngwmn.dm.cache.fs.FileCache;

import javax.naming.InitialContext;

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

	private static String basedir = "/tmp/gwdp-cache";

	@BeforeClass
	public static void setupNaming() throws Exception {
		final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.bind(FileCache.BASEDIR_JNDI_NAME, basedir);

		try {
			builder.activate();
		} catch (IllegalStateException ise) {
			// Set the required value into the existing context instead
			InitialContext ctx = new InitialContext();
			ctx.bind(FileCache.BASEDIR_JNDI_NAME, basedir);
		}
	}

	public static void setBasedir(String basedir) {
		ContextualTest.basedir = basedir;
	}

}
