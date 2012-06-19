package gov.usgs;

import gov.usgs.ngwmn.NotImplementedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockResponse implements HttpServletResponse {

	@Override
	public void flushBuffer() throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public int getBufferSize() {
		throw new NotImplementedException();
	}

	@Override
	public String getCharacterEncoding() {
		throw new NotImplementedException();
	}

	@Override
	public Locale getLocale() {
		throw new NotImplementedException();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public boolean isCommitted() {
		throw new NotImplementedException();
	}

	@Override
	public void reset() {
		throw new NotImplementedException();
	}

	@Override
	public void resetBuffer() {
		throw new NotImplementedException();
	}

	@Override
	public void setBufferSize(int arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void setContentLength(int arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void setContentType(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void setLocale(Locale arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void addCookie(Cookie arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		throw new NotImplementedException();
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		throw new NotImplementedException();
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		throw new NotImplementedException();
	}

	@Override
	public boolean containsHeader(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public String encodeURL(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public String encodeUrl(String arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void sendError(int arg0) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		throw new NotImplementedException();
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		throw new NotImplementedException();
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		throw new NotImplementedException();
	}

	@Override
	public void setStatus(int arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void setStatus(int arg0, String arg1) {
		throw new NotImplementedException();
	}

	public String getContentType() {
		throw new NotImplementedException();
	}

	public void setCharacterEncoding(String arg0) {
		throw new NotImplementedException();
	}

}
