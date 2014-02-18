/**
 * 
 */
package no.ntnu.idi.wikiviews.aux;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tkusm
 * 
 */
public class IntervalDate extends Date {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IntervalDate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IntervalDate(long date) {
		super(date);
		// TODO Auto-generated constructor stub
	}

	public static IntervalDate parseDate(String text) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH");
		long time = sdf.parse(text).getTime();
		return new IntervalDate(time);
	}

}
