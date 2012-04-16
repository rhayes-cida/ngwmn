package gov.usgs.ngwmn;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Objects;

/**
 * Class to consolidate/uniformize servlet parameter handling across servlets
 * @author ilinkuo
 *
 */
public enum ServiceParameters {
	SERVLET_PATH("servlet", "data");
	
	private String _defaultValue;
	private String name;

	private ServiceParameters(String paramName, String defaultValue) {
		this.name = paramName;
		this._defaultValue = defaultValue;
	}
	
	public String get(HttpServletRequest req) {
		return Objects.firstNonNull(req.getParameter(name), this._defaultValue);
	}
	
	public String get(HttpServletRequest req, String defaultValue) {
		return Objects.firstNonNull(req.getParameter(name), defaultValue);
	}
	
	public String[] getAll(HttpServletRequest req) {
		return req.getParameterValues(name);
	}

	public boolean isValid(String value) {
		switch(this) {
			case SERVLET_PATH:
				return value.matches("[A-Za-z]+");
			default: return true;
		}
	}
}
