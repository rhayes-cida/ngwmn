package gov.usgs.ngwmn.dm.io.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class XMLUtils {
	public static String nonQnameSeparators = "[()/@]";
	public static String nonQnameChars = "[^-_.:0-9A-Za-z]";
	public static String allNonQnameChars = nonQnameSeparators + nonQnameChars;
	public static String nonQnameReplacements = "[()/@]" + "[^-_.:0-9A-Za-z]";
	
	public static Pattern replaceNonQnameSeparators = Pattern.compile(nonQnameSeparators);
	public static Pattern removeNonQname = Pattern.compile(nonQnameChars);
	public static Pattern removeLeftAngleBracket = Pattern.compile("<");
	public static Pattern removeRightAngleBracket = Pattern.compile(">");
	public static Pattern removeAmpersand = Pattern.compile("&");
	/**
	 * Performs a quick, possibly incomplete removal or replacement of illegal
	 * characters in an element name. ".", "-", and "_" are legal in a name, and
	 * ":" is part of a namespace
	 * 
	 * @param aName
	 * @return
	 */
	public static String xmlQuickSanitize(String aName) {
		if (aName.indexOf('/') >= 0 || aName.indexOf('@') >= 0) {
			Matcher matcher = replaceNonQnameSeparators.matcher(aName);
			aName = matcher.replaceAll("_");
		}
		return aName;
	}
	
	/**
	 * Removes or replaces illegal characters in an element name. ".", "-", and
	 * "_" are legal in a name, and ":" is part of a namespace
	 * 
	 * @param aName
	 * @return
	 */
	public static String xmlFullSanitize(String aName) {
		Matcher matcher = replaceNonQnameSeparators.matcher(aName);
		matcher = removeNonQname.matcher(matcher.replaceAll("_"));
		return matcher.replaceAll("");
	}
	
	public static String escapeAngleBrackets(String aString) {
		if (aString.indexOf('<') >= 0) {
			aString = removeLeftAngleBracket.matcher(aString).replaceAll("&lt;");
		}
		if (aString.indexOf('>') >= 0) {
			aString = removeRightAngleBracket.matcher(aString).replaceAll("&gt;");
		}
		return aString;
	}
	
	public static String quickTagContentEscape(String aString) {
		if (aString.indexOf('&') >= 0 
				&& aString.indexOf("&amp;") < 0 
				&& aString.indexOf("&quot;") < 0
				&& aString.indexOf("&lt;") < 0
				&& aString.indexOf("&gt;") < 0
				&& aString.indexOf("&apos;") < 0) {
			aString = removeAmpersand.matcher(aString).replaceAll("&amp;");
		}
		return escapeAngleBrackets(aString);
	}
	
	public static final Pattern lessThan= Pattern.compile("&lt;");
	public static final Pattern greaterThan= Pattern.compile("&gt;");
	public static final Pattern quote= Pattern.compile("&quot;");
	public static final Pattern apostrophe= Pattern.compile("&apos;");
	public static final Pattern ampersand= Pattern.compile("&amp;");
	/**
	 * Replace the five standard xml entities: &lt; &gt; &quot; &apos; &amp;
	 * @param aString
	 * @return
	 */
	public static String unEscapeXMLEntities(String aString) {
		//
		if (aString.indexOf('&') >= 0) {
			Matcher matcher = lessThan.matcher(aString);
			String result = matcher.replaceAll("<");
			matcher = greaterThan.matcher(result);
			result = matcher.replaceAll(">");
			matcher = quote.matcher(result);
			result = matcher.replaceAll("\"");
			matcher = apostrophe.matcher(result);
			result = matcher.replaceAll("'");
			matcher = ampersand.matcher(result);
			result = matcher.replaceAll("&");
			return result;
		}
		return aString;
	}

}
