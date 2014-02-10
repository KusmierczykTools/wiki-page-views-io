package no.ntnu.idi.wikiviews.storage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.aux.GlobalTime;
import no.ntnu.idi.wikiviews.base.PageId;
import no.ntnu.idi.wikiviews.base.PageDisplays;
import no.ntnu.idi.wikiviews.base.PageDisplaysHistory;
import no.ntnu.idi.wikiviews.base.PageMetadata;
import no.ntnu.idi.wikiviews.exceptions.InconsistentTimeline;

/**
 * @author tkusm
 * 
 */
public class CacheStorage implements StorageReaderWriter {

	final private static Logger LOGGER = Logger.getLogger(CacheStorage.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
	}

	private String date, time;

	final private Map<PageId, PageDisplaysHistory> pageToHistory;
	final private StorageReaderWriter underlyingStorage;
	final private int maxHistoryLenghtPerPage;

	/**
	 * @param underlyingStorage
	 */
	public CacheStorage(StorageReaderWriter underlyingStorage, int maxCacheLenghtPerPage, String date, String time) {
		LOGGER.info("[STAT] Starting with date=" + date + " time=" + time + " maxHistoryLenghtPerPage="
		        + maxCacheLenghtPerPage);
		this.date = date;
		this.time = time;
		this.pageToHistory = new TreeMap<PageId, PageDisplaysHistory>();
		this.underlyingStorage = underlyingStorage;
		this.maxHistoryLenghtPerPage = maxCacheLenghtPerPage;
	}

	@Override
	public void write(PageId pageId, PageDisplays views) throws IOException {
		PageDisplaysHistory history = pageToHistory.get(pageId);
		if (history == null) {
			history = new PageDisplaysHistory(pageId, getDate(), getTime(), GlobalTime.getInstance().getTime() - 1);
			pageToHistory.put(pageId, history);
			CodeProfiler.getInstance().register("AddNewHistoryEntry");
		}

		try {
			history.add(views, GlobalTime.getInstance().getTime());
		} catch (InconsistentTimeline e) {
			LOGGER.severe("Error: InconsitentTimeline exception:" + e.getMessage());
			e.printStackTrace();
			throw new IOException("Error: InconsitentTimeline exception:" + e.getMessage());
		}

		if (history.getSize() > maxHistoryLenghtPerPage) {
			underlyingStorage.write(pageId, history.retrieveAllApartFromLast());
		}
	}

	@Override
	public void write(PageId page, List<PageDisplays> views) throws IOException {
		for (PageDisplays v : views) {
			this.write(page, v);
		}
	}

	public void flushExisting(String storageDir) throws IOException {
		int flushed = 0;
		for (PageId p : this.pageToHistory.keySet()) {			
			if (underlyingStorage.contains(p)) {
				underlyingStorage.write(p, this.pageToHistory.get(p).retrieveAll());
				++flushed;
			}
		}
		LOGGER.info(String.format("[STAT] %d flushed out of %d", flushed, this.pageToHistory.size()));
	}

	public void flushAll() throws IOException {
		for (PageId p : this.pageToHistory.keySet()) {
			underlyingStorage.write(p, this.pageToHistory.get(p).retrieveAll());
		}
	}

	public void fillTimeGapsWithZeros() throws IOException {
		int updatedCount = 0;
		for (PageDisplaysHistory history : pageToHistory.values()) {
			boolean updated = history.fillTimeGapWithZeros(GlobalTime.getInstance().getTime());
			updatedCount += (updated) ? 1 : 0;

			if (history.getSize() > maxHistoryLenghtPerPage) {
				underlyingStorage.write(history.getId(), history.retrieveAllApartFromLast());
			}
		}
		LOGGER.info("[STAT] " + updatedCount + " pages updated with zeros to adjust their timelines");
		CodeProfiler.getInstance().register("FillTimeGapsWithZeros");
	}

	public void storeToFile(String path) throws IOException {
		LOGGER.info("[STAT] Serializing cache (" + pageToHistory.size() + " entries) to " + path);
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		for (PageDisplaysHistory history : pageToHistory.values()) {
			out.write(history.toString().replace("\n", ""));
			out.write("\n");
		}
		out.close();
	}

	public void restoreFromFile(String path) throws FileNotFoundException {
		LOGGER.info("Loading cache from to " + path);

		InputStream inputStream = new FileInputStream(path);
		Scanner input = new Scanner(inputStream);

		pageToHistory.clear();
		int counter = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			try {
				PageDisplaysHistory history = PageDisplaysHistory.parseString(line);
				PageId pageId = history.getId();
				pageToHistory.put(pageId, history);
				counter++;
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error: failed parsing line:" + line + " Exception:" + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.info(String.format("[STAT] %d loaded to cache in total", counter));
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
		LOGGER.info("[STAT] date is now " + date);
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
		LOGGER.info("[STAT] time is now " + date);
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	@Override
	public List<PageDisplays> read(PageId page) throws IOException {
		if (!pageToHistory.containsKey(page)) {
			throw new IOException(page.toString()+" is not stored in the storage!");
		}
		
		if (underlyingStorage.contains(page)) { // we need to consider part
			                                    // from the disk
			if (pageToHistory.get(page).getHistory().size() <= 0) {
				// everything is on the this and nothing in the memory
				return underlyingStorage.read(page);
			}

			// first part is on the disk and the second in the memory
			// we do copy not to destroy underlying things
			List<PageDisplays> firstPart = new LinkedList<PageDisplays>(underlyingStorage.read(page));
			List<PageDisplays> secondPart = pageToHistory.get(page).getHistory();
			firstPart.addAll(secondPart);
			return firstPart;
		}

		return pageToHistory.get(page).getHistory();
	}

	@Override
	public Set<PageId> getKeys() {
		return this.pageToHistory.keySet();
	}
	
	public void print(OutputStream o) throws IOException {		
		for (PageDisplaysHistory h: this.pageToHistory.values()) {
			o.write( (h.toString()+"\n").getBytes(Charset.forName("UTF-8")) );			
		}
	}

	@Override
	public boolean contains(PageId page) {
		return this.pageToHistory.containsKey(page);
	}

	public PageMetadata getPageMetadata(PageId page) {
		return pageToHistory.get(page).getMetadata();
	}
}
