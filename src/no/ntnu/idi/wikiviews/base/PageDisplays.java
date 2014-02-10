/**
 * 
 */
package no.ntnu.idi.wikiviews.base;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.UnsupportedDataTypeException;

import no.ntnu.idi.wikiviews.exceptions.TooMuchValuesException;

/**
 * @author tkusm
 * 
 */
public final class PageDisplays {

	/**
	 * If there is sequence of the same views starting with what number they
	 * should be grouped.
	 */
	public static final int GROUP_NUM_VIEWS = 2;

	final int numViews;
	final int numTimes;

	protected static final Logger LOGGER = Logger.getLogger(PageDisplaysHistory.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
	}

	/**
	 * @param numViews
	 * @param numTimes
	 */
	public PageDisplays(int numViews, int numTimes) {
		this.numViews = numViews;
		this.numTimes = numTimes;
	}

	/**
	 * @return the numViews
	 */
	public int getNumViews() {
		return numViews;
	}

	/**
	 * @return the numTimes
	 */
	public int getNumTimes() {
		return numTimes;
	}

	public PageDisplays(PageDisplays other) {
		this.numTimes = other.numTimes;
		this.numViews = other.numViews;
	}

	private String toStorageString(String separator) {
		if (numTimes < GROUP_NUM_VIEWS) {
			StringBuilder out = new StringBuilder();
			for (int i = 0; i < numTimes - 1; ++i) {
				out.append(numViews);
				out.append(separator);
			}
			out.append(numViews);
			return out.toString();
		}

		return String.format("%dx%d", numViews, numTimes);
	}

	@Override
	public String toString() {
		return this.toStorageString(" ");
	}

	public static PageDisplays parseString(String line) throws TooMuchValuesException {
		if (line.split(" ").length > 1) {
			throw new TooMuchValuesException("[PageViews] There is too much values to be interpreted!");
		}

		if (line.contains("x")) {
			String[] tokens = line.split("x");
			int numViews = Integer.parseInt(tokens[0]);
			int numTimes = Integer.parseInt(tokens[1]);
			return new PageDisplays(numViews, numTimes);
		} else {
			int numViews = Integer.parseInt(line);
			return new PageDisplays(numViews, 1);
		}
	}

	public static LinkedList<PageDisplays> parseManyValuesString(String line) throws UnsupportedDataTypeException {
		if (line.trim().contains("\n")) {
			throw new UnsupportedDataTypeException("[parseManyValuesString] Input string must be single line!");
		}

		LinkedList<PageDisplays> listOfExtractedValues = new LinkedList<PageDisplays>();
		String[] tokens = line.trim().split(" ");
		try {

			for (String token : tokens) {
				PageDisplays v = PageDisplays.parseString(token);
				listOfExtractedValues.add(v);
			}

		} catch (TooMuchValuesException e) {
			LOGGER.warning("[STAT] Error: strange thing happened while parsing line:" + line
			        + " TooMuchValuesException:" + e.getMessage());
			e.printStackTrace();
		}
		return listOfExtractedValues;
	}

}
