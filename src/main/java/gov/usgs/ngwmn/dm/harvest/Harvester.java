package gov.usgs.ngwmn.dm.harvest;


import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Harvester {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private InputStream    is;
	private int    statusCode;
	private HttpClient client;

	
	public InputStream getInputStream() {
		return is;
	}
	public int getStatusCode() {
		return statusCode;
	}
	
	public int wget(String url) throws IOException {
		logger.info("wget from {}",url);
		
		try {
			
			client = new HttpClient();

			// pipe.getStatistics().setSource(url);
			
			HttpMethod method = new GetMethod(url);
			
			statusCode = client.executeMethod(method);
			
			if (statusCode != HttpStatus.SC_OK) {
				return statusCode;
			}
			is = method.getResponseBodyAsStream();
			// it's zero, no help here  logger.info("response stream available {}", is.available());
			return statusCode;
			
		} catch (IOException e) {
			String msg = "error wget from " + url; // need to concat for new exception msg
			logger.error(msg, e); // so I might as well use it here
			throw new IOException(msg, e);
		}
	}

}
