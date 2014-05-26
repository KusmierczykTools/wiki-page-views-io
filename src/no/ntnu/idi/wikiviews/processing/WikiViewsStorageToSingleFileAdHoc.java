package no.ntnu.idi.wikiviews.processing;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.aux.GlobalTime;
import no.ntnu.idi.wikiviews.aux.IntervalDate;
import no.ntnu.idi.wikiviews.base.PageDisplays;
import no.ntnu.idi.wikiviews.base.PageDisplaysHistory;
import no.ntnu.idi.wikiviews.base.PageId;
import no.ntnu.idi.wikiviews.base.PageMetadata;
import no.ntnu.idi.wikiviews.exceptions.BadDateTimeFormat;
import no.ntnu.idi.wikiviews.storage.CacheStorage;
import no.ntnu.idi.wikiviews.storage.DiskStorage;
import no.ntnu.idi.wikiviews.storage.SingleFileStorageWriter;

public class WikiViewsStorageToSingleFileAdHoc {

	protected static final Logger LOGGER = Logger.getLogger(PageDisplaysHistory.class.getName());
	static {
		LOGGER.setLevel(Level.ALL);
	}

	public static void main(String args[]) throws BadDateTimeFormat, ParseException, IOException {
		LOGGER.info("[AdHoc] The program reads from storage and writes to single output file.");
		final String baseDir, outPath;
		try {
			baseDir = args[0];
			outPath = args[1];
		} catch (Exception e) {
			LOGGER.severe("Failed parsing arguments! Arguments expected: single shard directory and output file path.");
			System.exit(-1);
			return;
		}
		LOGGER.info("baseDir = " + baseDir);
		String metaPath = baseDir + java.io.File.separator + CacheStorage.META_FILE_NAME;
		LOGGER.info("metaPath = " + metaPath);

		DiskStorage disk = new DiskStorage(baseDir, null, null);
		SingleFileStorageWriter out = new SingleFileStorageWriter(outPath);
		Scanner input = new Scanner(new FileInputStream(metaPath));

		long timelineEndingMsec = -1;
		int counter = 0, validCounter = 0, failuresCounter = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			counter++;

			try {
				PageDisplaysHistory cacheData = PageDisplaysHistory.parseString(line);
				PageId pageId = cacheData.getId();

				List<PageDisplays> history = null;
				if (disk.contains(pageId)) {
					// first part is on the disk and the second in the memory
					// we do copy not to destroy underlying things
					history = new LinkedList<PageDisplays>(disk.read(pageId));
					history.addAll(cacheData.getHistory());
				} else {
					history = cacheData.getHistory();
				}

				PageMetadata meta = cacheData.getMetadata();
				long startTime = GlobalTime.parseDate(meta.getStartDate(), meta.getStartTime()).getTime();
				long thisEndingMsec = startTime + PageDisplaysHistory.unwrappedLength(history) * 3600000L;

				if (thisEndingMsec != timelineEndingMsec) {
					List<PageDisplays> unwrapHistory = PageDisplaysHistory.unwrap(history);
					LOGGER.severe("[ERROR] Wrong timeline for: page = " + meta + " history.size() = " + history.size()
							+ " history => " + PageDisplaysHistory.toString(history) + " UnwrappedLength = "
							+ PageDisplaysHistory.unwrappedLength(history) + " unwrapHistory.size() = "
							+ unwrapHistory.size() + " equals = "
							+ PageDisplaysHistory.equals(history, unwrapHistory) + " startTime =" + startTime + " ("
							+ (new IntervalDate(startTime)) + ") thisEndingMsec = " + thisEndingMsec + " ("
							+ (new IntervalDate(thisEndingMsec)) + ") timelineEndingMsec = " + timelineEndingMsec
							+ " (" + (new IntervalDate(timelineEndingMsec)) + ")");
					failuresCounter++;
				} else {
					out.store(meta, history);
					validCounter++;
				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "[ERROR] Failed parsing line:" + line + " Exception:" + e.getMessage());
				e.printStackTrace();
				failuresCounter++;
			} //try
			
			if (counter % 100000 == 0) {
				LOGGER.info(String.format("%d loaded ", counter));
				CodeProfiler.getInstance().set("ValidEntries", validCounter);
				CodeProfiler.getInstance().set("Failures", failuresCounter);
				CodeProfiler.getInstance().printStats("StorageValidation");
			}
		} //while
		LOGGER.info(String.format("[STAT] %d processed in total", counter));

		CodeProfiler.getInstance().set("ValidEntries", validCounter);
		CodeProfiler.getInstance().set("Failures", failuresCounter);
		CodeProfiler.getInstance().printStats("StorageValidation");
		out.close();

	}

}
