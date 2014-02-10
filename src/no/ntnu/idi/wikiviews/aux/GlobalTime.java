package no.ntnu.idi.wikiviews.aux;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.storage.CacheStorage;

public class GlobalTime {

	final private static Logger LOGGER = Logger.getLogger(CacheStorage.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
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

	/************************************************************************/

	private static GlobalTime instance = null;

	public static synchronized GlobalTime getInstance() {
		if (instance == null) {
			instance = new GlobalTime(0);
		}
		return instance;
	}

}
