package no.ntnu.idi.wikiviews.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.aux.StringFilter;
import no.ntnu.idi.wikiviews.base.PageId;
import no.ntnu.idi.wikiviews.base.PageDisplays;
import no.ntnu.idi.wikiviews.storage.CacheStorage;
import no.ntnu.idi.wikiviews.storage.DiskStorage;
import no.ntnu.idi.wikiviews.storage.StorageWriter;

public class PageViewsProcessor {

	static class Entry {

		PageId id;
		PageDisplays views;

		/**
		 * @param project
		 * @param name
		 * @param views
		 * @param returned
		 */
		public Entry(String project, String name, int views, long returned) {
			this.id = new PageId(project, name);
			this.views = new PageDisplays(views, 1); // one time unit per run
		}

		public static Entry parseString(String line) {
			String[] tokens = line.split(" ");
			String project = tokens[0];
			String name = tokens[1];
			int views = Integer.parseInt(tokens[2]);
			long returned = Long.parseLong(tokens[3]);
			return new Entry(project, name, views, returned);
		}

	}

	/*********************************************************************/
	final private String storageDir;
	final private DiskStorage storage;
	final private CacheStorage cache;

	public static final String META_FILE_NAME = "meta.txt";

	protected static final Logger LOGGER = Logger.getLogger(PageViewsProcessor.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
	}

	public PageViewsProcessor(String storageDir, String date, String time, int cacheHistoryLength) {
		this.storageDir = storageDir;
		this.storage = new DiskStorage(storageDir, date, time);
		this.cache = new CacheStorage(storage, cacheHistoryLength, date, time);
	}

	public void setDateTime(String date, String time) {
		cache.setDate(date);
		cache.setTime(time);
	}

	public long finishProcessing() throws IOException {
		long startTime = System.currentTimeMillis();
		cache.fillTimeGapsWithZeros(); // adjust timeline
		new File(storageDir).mkdirs();
		cache.storeToFile(storageDir + java.io.File.separator + META_FILE_NAME);
		long estimatedTime = System.currentTimeMillis() - startTime;
		return estimatedTime;
	}

	public long startProcessing() {
		long startTime = System.currentTimeMillis();
		try {
			cache.restoreFromFile(storageDir + java.io.File.separator + META_FILE_NAME);
		} catch (FileNotFoundException e) {
			LOGGER.info("Storage cache not found! Staring with empty cache!");
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		return estimatedTime;
	}

	public long processSingleDataStream(InputStream inputStream) {
		long startTime = System.currentTimeMillis();

		Scanner input = new Scanner(inputStream);		
		while(input.hasNextLine()) {
			String line = input.nextLine();
			processNextLine(cache, line);
			CodeProfiler.getInstance().register("NewPage");
		}
				
		long estimatedTime = System.currentTimeMillis() - startTime;
		return estimatedTime;
	}

	private static void processNextLine(StorageWriter storage, String line) {
		try {
			Entry e = Entry.parseString(line);
			storage.write(e.id, e.views);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "[STAT] Error in line=[" + line + "][ords: " + StringFilter.stringOrds(line)
			        + "] Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}

}