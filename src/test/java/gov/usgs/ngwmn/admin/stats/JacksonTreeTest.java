package gov.usgs.ngwmn.admin.stats;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.junit.Test;

public class JacksonTreeTest {

	@Test
	public void test() throws Exception {
		JitTree base = new JitTree("Name 1", 11);
		
		JitTree k1 = new JitTree("Kid 1", 3);
		
		base.addChild(k1);
		
		StringWriter sw = new StringWriter();

		ObjectMapper mapper = new ObjectMapper();

		mapper.writeValue(sw, base);
		
		System.out.println(sw.toString());
		
		assertTrue(sw.toString().contains("Kid 1"));
		assertFalse(sw.toString().contains("null"));
	}

}
