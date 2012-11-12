package gov.usgs.ngwmn;

public class WaterlevelMediator {

	public static Double mediate(Double value, String agency, String site, String direction) {
		System.err.printf("mediate(%s,%s,%s,%s)\n", value, agency, site, direction);
		
		// TODO Look up datum from well registry
		// TODO Accept number as string, to preserve input precision
		if ("up".equals(direction)) {
			return -value;
		} else  {
			return value;
		}
	}
}
