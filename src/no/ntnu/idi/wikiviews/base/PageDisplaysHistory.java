package no.ntnu.idi.wikiviews.base;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.exceptions.InconsistentTimeline;
import no.ntnu.idi.wikiviews.exceptions.TooMuchValuesException;

/**
 * 
 * 
 * @author tkusm
 * 
 */
public class PageDisplaysHistory {
	final private PageId id;

	final private String startDate;
	final private String startTime;

	private int previousAdditionTime;
	private LinkedList<PageDisplays> history;

	protected static final Logger LOGGER = Logger.getLogger(PageDisplaysHistory.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
	}

	/**
	 * @param startDate
	 * @param startTime
	 * @param id
	 */
	public PageDisplaysHistory(PageId id, String startDate, String startTime, int previousAdditionTime) {
		this.id = id;
		this.startDate = startDate;
		this.startTime = startTime;
		this.previousAdditionTime = previousAdditionTime;
		this.history = new LinkedList<PageDisplays>();
	}

	public PageDisplaysHistory(PageId id, String startDate, String startTime, int previousAdditionTime,
			List<PageDisplays> history) {
		this.id = id;
		this.startDate = startDate;
		this.startTime = startTime;
		this.previousAdditionTime = previousAdditionTime;
		this.history = new LinkedList<PageDisplays>(history);
	}

	public int getSize() {
		return history.size();
	}

	public LinkedList<PageDisplays> getHistory() {
		return history;
	}

	public PageMetadata getMetadata() {
		return new PageMetadata(id, startDate, startTime);
	}

	/**
	 * @return the id
	 */
	public PageId getId() {
		return id;
	}

	private void add(PageDisplays newViews) {
		if (history.isEmpty()) {
			history.add(newViews);
			return;
		}

		PageDisplays last = history.getLast();
		if (last.getNumViews() == newViews.getNumViews()) {
			history.pollLast();
			history.add(new PageDisplays(last.getNumViews(), last.getNumTimes() + newViews.getNumTimes()));
		} else {
			history.add(newViews);
		}
	}

	public void add(PageDisplays newViews, int currentTime) throws InconsistentTimeline {
		if (previousAdditionTime != Integer.MIN_VALUE) {

			if (currentTime <= previousAdditionTime) {
				throw new InconsistentTimeline("Previous addition time was:" + previousAdditionTime
						+ " and current is:" + currentTime + " [id:" + id + "]");
			}
			int gap = currentTime - previousAdditionTime - 1;
			if (gap > 0) {
				this.add(new PageDisplays(0, gap));
				CodeProfiler.getInstance().register("FixGapWithZeros");
			}
		}
		this.add(newViews);
		this.previousAdditionTime = currentTime;
	}

	public boolean fillTimeGapWithZeros(int currentTime) {
		int gap = currentTime - previousAdditionTime;
		if (gap > 0) {
			this.add(new PageDisplays(0, gap));
		}
		this.previousAdditionTime = currentTime;
		CodeProfiler.getInstance().register("AdjustWithZeros");
		return (gap > 0);
	}

	public List<PageDisplays> retrieveAllApartFromLast() {
		LOGGER.fine("partial flush of " + id);
		LinkedList<PageDisplays> retrieved = new LinkedList<PageDisplays>();

		while (history.size() > 1) { // leave the last one!
			PageDisplays v = history.pollFirst();
			retrieved.add(v);
		}

		return retrieved;
	}

	public List<PageDisplays> retrieveAll() {
		LOGGER.fine("full flush of " + id);
		List<PageDisplays> retrieved = history;
		history = new LinkedList<PageDisplays>();
		return retrieved;
	}

	public static String toString(List<PageDisplays> history) {
		StringBuilder out = new StringBuilder();
		for (PageDisplays v : history) {
			out.append(" ");
			out.append(v.toString());
		}
		return out.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(id.project + " " + id.name + " " + startDate + " " + startTime + " " + previousAdditionTime);
		return out.toString()+toString(history);
	}

	public static PageDisplaysHistory parseString(String line) {
		String[] tokens = line.split(" ");
		String project = tokens[0].trim();
		String name = tokens[1].trim();
		String date = tokens[2].trim();
		String time = tokens[3].trim();
		int previousAdditionTime = Integer.parseInt(tokens[4].trim());

		PageDisplaysHistory extracted = new PageDisplaysHistory(new PageId(project, name), date, time,
				previousAdditionTime);
		// TODO should be replaced with PageDisplays.parseManyValuesString but I
		// don't touch working things
		for (int tokenNo = 5; tokenNo < tokens.length; ++tokenNo) {
			try {
				PageDisplays newViews = PageDisplays.parseString(tokens[tokenNo].trim());
				extracted.add(newViews);
			} catch (TooMuchValuesException e) {
				LOGGER.severe("Error (" + e.getMessage() + ")in parsing line=" + line);
				e.printStackTrace();
			}
		}

		return extracted;
	}

	/**
	 * Converts list where displays are grouped (compressed with RLE) to list
	 * where each views number is stored separately.
	 * 
	 * @param history
	 * @return
	 */
	public static LinkedList<PageDisplays> unwrap(final List<PageDisplays> history) {
		LinkedList<PageDisplays> unwrapped = new LinkedList<PageDisplays>();

		for (PageDisplays p : history) {
			for (int i = 0; i < p.getNumTimes(); ++i) {
				unwrapped.add(new PageDisplays(p.getNumViews(), 1));
			}
		}

		return unwrapped;
	}

	public static int unwrappedLength(final List<PageDisplays> history) {
		int length = 0;
		for (PageDisplays p : history) {
			length += p.getNumTimes();
		}
		return length;
	}

	/**
	 * Converts list where displays are stored separately to list where are
	 * grouped (compressed with RLE).
	 * 
	 * @param history
	 * @return
	 */
	public static LinkedList<PageDisplays> wrap(final List<PageDisplays> history) {
		LinkedList<PageDisplays> wrapped = new LinkedList<PageDisplays>();

		PageDisplays previous = null;
		int numTimes = 0;
		for (PageDisplays p : history) {
			if (previous == null || previous.getNumViews() == p.getNumViews()) {
				numTimes += p.getNumTimes();
			} else {
				wrapped.add(new PageDisplays(previous.getNumViews(), numTimes));
				numTimes = p.getNumTimes();
			}
			previous = p;
		}

		if (numTimes > 0) {
			wrapped.add(new PageDisplays(previous.getNumViews(), numTimes));
		}

		return wrapped;
	}

	public static boolean equals(List<PageDisplays> h1, List<PageDisplays> h2) {
		if (h1 == null || h2 == null) {
			return false;
		}

		h1 = wrap(h1);
		h2 = wrap(h2);

		return h1.equals(h2);
	}

}
