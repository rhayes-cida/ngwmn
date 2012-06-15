package gov.usgs;

import gov.usgs.ngwmn.NotImplementedException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MockFilterChain implements FilterChain {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1)
			throws IOException, ServletException {
		throw new NotImplementedException();
	}

}
