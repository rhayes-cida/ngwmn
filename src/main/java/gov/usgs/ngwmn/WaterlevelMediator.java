package gov.usgs.ngwmn;

import java.math.BigDecimal;

public class WaterlevelMediator {

	public static String mediate(String value, String offset, String direction) {
		// System.err.printf("mediate(%s,%s,%s)\n", value, offset, direction);
		
		String wl;
		// TODO Accept number as string, to preserve input precision
		// TODO Is offset applied exactly when direction == up?
		// TODO Take units input as well?
		
		if ("up".equals(direction)) {
			wl = negateAndApplyOffset(value, offset);
		} else  {
			wl = value;
		}
		
		return wl;
			
	}

	public static String csv_escape(String v) {
		if (v == null) {
			return v;
		}
		if ( ! v.contains(",")) {
			return v;
		}
		v = v.replace("\"", "\"\"");
		v = "\"" + v + "\"";
		return v;
	}
	
	private static String negateAndApplyOffset(String value, String offset) {
		BigDecimal v = new BigDecimal(value);
		
		v = v.negate();
		if (offset != null && ! offset.isEmpty()) {
			BigDecimal os = new BigDecimal(offset);
			v = v.add(os);
		}
		return v.toString();
	}

	public static double applyOffset(double wl, String offset) {
		if (offset != null && ! offset.isEmpty()) {
			try {
				double os = Double.parseDouble(offset);
				wl += os;
			} catch (Exception x) {
				System.err.println("bad double " + offset);
			}
		}
		return wl;
	}
}
