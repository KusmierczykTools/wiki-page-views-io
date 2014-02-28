package no.ntnu.idi.wikiviews.processing;

import java.io.IOException;
import java.text.ParseException;

import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.aux.GlobalTime;
import no.ntnu.idi.wikiviews.aux.IntervalDate;
import no.ntnu.idi.wikiviews.base.PageDisplaysHistory;
import no.ntnu.idi.wikiviews.base.PageMetadata;
import no.ntnu.idi.wikiviews.exceptions.BadDateTimeFormat;
import no.ntnu.idi.wikiviews.storage.SingleFileStorageReader;
import no.ntnu.idi.wikiviews.storage.SingleUnwrappedFileStorageWriter;

public class UnwrapSingleFileStorage {

	protected static final Logger LOGGER = Logger.getLogger(PageDisplaysHistory.class.getName());
	static {
		LOGGER.setLevel(Level.ALL);
	}

	public static void main(String args[]) throws BadDateTimeFormat, ParseException, IOException {
		LOGGER.info("The program reads from input file page-views entries (e.g. single file storage or meta.txt) unwraps timelines and writes to file.");
		final String outPath, inPath;
		try {
			inPath = args[0];
			outPath = args[1];
		} catch (Exception e) {
			LOGGER.severe("Failed parsing arguments! Arguments expected: input and output file path.");
			System.exit(-1);
			return;
		}
		LOGGER.info("inPath=" + inPath + " outPath=" + outPath);

		SingleFileStorageReader reader = new SingleFileStorageReader(inPath);
		SingleUnwrappedFileStorageWriter writer = new SingleUnwrappedFileStorageWriter(outPath);

		int validCounter = 0, failuresCounter = 0;
		long timelineEndingMsec = -1;
		for (PageDisplaysHistory h : reader) {
			PageMetadata meta = h.getMetadata();
			long startTime = GlobalTime.parseDate(meta.getStartDate(), meta.getStartTime()).getTime();
			long thisEndingMsec = startTime + PageDisplaysHistory.unwrappedLength(h.getHistory()) * 3600000L;
			if (timelineEndingMsec < 0) {
				LOGGER.info("Timeline end = " + new IntervalDate(thisEndingMsec));
				timelineEndingMsec = thisEndingMsec;
			} else if (thisEndingMsec != timelineEndingMsec) {
				LOGGER.warning("Wrong timeline for: page = " + meta + " UnwrappedLength = " + " equals = "
						+ " startTime =" + startTime + " (" + (new IntervalDate(startTime)) + ") thisEndingMsec = "
						+ thisEndingMsec + " (" + (new IntervalDate(thisEndingMsec)) + ") timelineEndingMsec = "
						+ timelineEndingMsec + " (" + (new IntervalDate(timelineEndingMsec)) + ")");
				failuresCounter++;
			} else {
				writer.store(h);
				validCounter++;
			}

		}

		CodeProfiler.getInstance().set("ValidEntries", validCounter);
		CodeProfiler.getInstance().set("Failures", failuresCounter);
		CodeProfiler.getInstance().printStats("StorageValidation");
	}
}
