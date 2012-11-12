package gov.usgs.ngwmn;

public class WaterlevelMediator {

	public static String mediate(String value, String agency, String site, String direction) {
		System.err.printf("mediate(%s,%s,%s,%s)\n", value, agency, site, direction);
		
		// TODO Look up datum from well registry
		// TODO Parse number
		if ("up".equals(direction)) {
			return "-" + value;
		} else  {
			return value;
		}
	}
}
