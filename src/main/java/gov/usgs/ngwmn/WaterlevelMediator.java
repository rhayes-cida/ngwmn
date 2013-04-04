package gov.usgs.ngwmn;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaterlevelMediator {

	private static Logger logger = LoggerFactory.getLogger(WaterlevelMediator.class);
	
	public static String mediate(String value, String offset, String direction) {
		// System.err.printf("mediate(%s,%s,%s)\n", value, offset, direction);
		
		String wl;
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
	
    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

	/**
	 * Is med lexically in the half-open interval bounded by left and right?
	 * Special case: null or "" is treated as unbounded on that side.
	 * 
	 * @param left
	 * @param med
	 * @param right
	 * @return
	 */
	public static boolean between(String left, String med, String right) {
		logger.trace("between({},{},{})", new Object[]{left, med, right});
		if ( ! isEmpty(left)) {
			if (left.compareTo(med) > 0) {
				logger.trace("->false l");
				return false;
			}
		}
		if ( ! isEmpty(right)) {
			if (med.compareTo(right) >= 0) {
				logger.trace("->false r");
				return false;
			}
		}
		logger.trace("->true");
		return true;
	}
}
