package gov.usgs.ngwmn;

public class WaterlevelMediator {

	public static Double mediate(Double value, String offset, String direction) {
		System.err.printf("mediate(%s,%s,%s)\n", value, offset, direction);
		
		double wl;
		// TODO Accept number as string, to preserve input precision
		// TODO Is offset applied exactly when direction == up?
		
		if ("up".equals(direction)) {
			wl = - value;
			wl = applyOffset(wl, offset);
		} else  {
			wl = value;
		}
		
		return wl;
			
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
