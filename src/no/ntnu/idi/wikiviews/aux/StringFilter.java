/**
 * 
 */
package no.ntnu.idi.wikiviews.aux;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tkusm
 * 
 */
public class StringFilter {

	public final static String ILLEGAL_CHARACTERS = " \t“”‘\"`'!#$&()*+,./:;<=>?@[\\]^{|}~\n";
	private Map<Character, String> illegalToLegal;

	private static String doubleDigitString(int i) {
		Integer j = i;
		if (j.toString().length()<2) {
			return "0"+j.toString();
		}
		return j.toString();
	}
	
	/**
	 * 
	 */
	private StringFilter(String illegalChars) {
		illegalToLegal = new HashMap<Character, String>();
		for (int i = 0; i < illegalChars.length(); ++i) {
			Character c = illegalChars.charAt(i);
			illegalToLegal.put(c, doubleDigitString(i));
		}
	}

	/**
	 * 
	 */
	private StringFilter() {
		this(ILLEGAL_CHARACTERS);
	}

	public String filter(String text) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < text.length(); ++i) {
			Character c = text.charAt(i);
			if (illegalToLegal.containsKey(c)) {
				out.append(illegalToLegal.get(c));
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}
	
	public static String stringOrds(String text) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < text.length(); ++i) {
			Character c = text.charAt(i);
			Integer code = Character.getNumericValue(c);
			out.append(code);
			out.append(" ");
		}
		return out.toString();
	}

	/************************************************************************/

	private static StringFilter instance = null;

	public static synchronized StringFilter getInstance() {
		if (instance == null) {
			instance = new StringFilter();
		}
		return instance;
	}

}
