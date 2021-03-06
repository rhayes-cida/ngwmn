package gov.usgs.ngwmn.ogc;

import java.util.Arrays;

/**
 *
 * @author Bill Blondeau <wblondeau@usgs.gov>
 */
public class BoundingBox {
	private String srsName;
	private String[] coordinates = new String[4];
	
	public BoundingBox (String srsName, String[] coordinates) {
		if (srsName == null || srsName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Parameter 'srsName' not permitted to be null, "
					+ "empty, or blank.");
		}
		
		if (coordinates == null || coordinates.length != 4) {
			throw new IllegalArgumentException(
					"Parameter 'coordinates' must contain exactly 4 values.");
		}
		
		for (String coord : coordinates) {
			try {
				Double.parseDouble(coord);
			}
			catch (NumberFormatException nfe) {
				throw new IllegalArgumentException (
						"Coordinates must be non-null numeric strings. "
						+ "Passed: " + coord);
			}
			catch (NullPointerException npe) {
				throw new IllegalArgumentException (
						"Coordinates must be non-null numeric strings. "
						+ "Passed: " + coord);
			}
		}
		
		this.srsName = srsName;
		this.coordinates = Arrays.copyOf(coordinates, 4);
	}

	public String getSrsName() {
		return srsName;
	}

	public String[] getCoordinates() {
		return Arrays.copyOf(coordinates, 4);
	}
}
