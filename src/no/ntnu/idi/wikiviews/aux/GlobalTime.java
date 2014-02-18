package no.ntnu.idi.wikiviews.aux;

import java.io.IOException;
import java.text.ParseException;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.idi.wikiviews.exceptions.BadDateTimeFormat;
import no.ntnu.idi.wikiviews.storage.CacheStorage;

public class GlobalTime {

	final private static Logger LOGGER = Logger.getLogger(CacheStorage.class.getName());
	static {
		LOGGER.setLevel(Level.ALL);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private int timeCounter;

	private GlobalTime(int initialTimerValue) {
		this.timeCounter = initialTimerValue;
	}

	public int getTime() {
		return timeCounter;
	}

	public void increaseTime() {
		++this.timeCounter;
		LOGGER.info("[STAT] timeCounter is now " + timeCounter);
	}

	public void storeTime(String path) throws IOException {
		LOGGER.info("[STAT] Storing to " + path + " time=" + timeCounter);
		AuxIO.writeInt(path, timeCounter);
	}

	public void restoreTime(String path) throws IOException {
		this.timeCounter = AuxIO.readInt(path);
		LOGGER.info("[STAT] Restored from " + path + " time=" + timeCounter);
	}

	

	public static IntervalDate parseDate(String dateString, String timeString) throws BadDateTimeFormat, ParseException {
		if (dateString.length()<8 || !dateString.matches("\\d\\d\\d\\d\\d\\d\\d\\d")) {
			throw new BadDateTimeFormat("String "+dateString+" is not a proper date!");
		}	
		if (timeString.length()<2 || !timeString.substring(0,2).matches("\\d\\d")) {
			throw new BadDateTimeFormat("String "+timeString+" is not a proper time!");
		}
		
		return 	IntervalDate.parseDate(dateString+" "+timeString.substring(0,2));
	}

	public static int numberOfHours(IntervalDate startTime, IntervalDate endTime) {
		return (int) ((startTime.getTime()-endTime.getTime()) / 3600000L);
	}
	
	/************************************************************************/

	private static GlobalTime instance = null;

	public static synchronized GlobalTime getInstance() {
		if (instance == null) {
			instance = new GlobalTime(0);
		}
		return instance;
	}
	
	public static void main(String args[]) throws ParseException, BadDateTimeFormat {
		String dateString = "20080227";
		String timeString = "230000";
		  	System.out.println(parseDate(dateString, timeString));
	}

}
