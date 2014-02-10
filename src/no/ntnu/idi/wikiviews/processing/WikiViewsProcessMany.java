package no.ntnu.idi.wikiviews.processing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.AuxIO;
import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.aux.GlobalTime;

/**
 * @author tkusm
 * 
 */
public class WikiViewsProcessMany {

	protected static final Logger LOGGER = Logger.getLogger(WikiViewsProcessMany.class.getName());

	public static void main(String args[]) throws IOException {

		LOGGER.setLevel(Level.ALL);
		LOGGER.info("LOADS WIKI PAGEVIEWS AND UPDATES STORAGE.");
		LOGGER.info("WARNING: SOURCE FILES SHOULD FOLLOW TIME ORDER!");
		if (args.length < 2) {
			LOGGER.info("Arguments expected: file-with-list-of-input-files storage-path");
			LOGGER.info("Optional argument: cache-history-length");
			System.exit(-1);
		}

		String listOfFilesPath = args[0];
		String storageDir = args[1];
		int cacheHistoryLength = (args.length > 2) ? Integer.parseInt(args[2]) : 1000;
		LOGGER.info(String.format("[STAT] listOfFilesPath=%s storage=%s cacheHistoryLength=%d", listOfFilesPath,
		        storageDir, cacheHistoryLength));

		/*********************************************************************/

		try {
			GlobalTime.getInstance().restoreTime(storageDir + java.io.File.separator + "time.txt");
		} catch (FileNotFoundException e) {
			LOGGER.warning("[STAT] File containing time info not found. Starting with time="
			        + GlobalTime.getInstance().getTime());
		}
		PageViewsProcessor processor = new PageViewsProcessor(storageDir, null, null, cacheHistoryLength);
		long cacheLoadingTime = processor.startProcessing();
		final List<String> listOfInputFiles = AuxIO.readFileLines(listOfFilesPath);
		long totalProcessingTime = 0;
		for (String inputFilePath : listOfInputFiles) {
			if (inputFilePath.trim().length() <= 0) {
				LOGGER.warning("[STAT] Empty line found in source file! Skipping!");
				continue;
			}
			long processingTime = processNextFile(processor, inputFilePath);
			totalProcessingTime += processingTime;
		}
		CodeProfiler.getInstance().clear();
		long cacheStoringTime = processor.finishProcessing();
		GlobalTime.getInstance().storeTime(storageDir + java.io.File.separator + "time.txt");
		CodeProfiler.getInstance().printStats();		

		LOGGER.info(String.format(
		        "[STAT] Done. Cache loading time:%dms, Processing of %d files time:%dms, Cache storing time:%dms",
		        cacheLoadingTime, listOfInputFiles.size(), totalProcessingTime, cacheStoringTime));
	}

	private static long processNextFile(PageViewsProcessor processor, String inputFilePath)
	        throws FileNotFoundException, IOException {
		String[] parts = inputFilePath.split("-");
		String date = parts[parts.length - 2];
		String time = parts[parts.length - 1].split("\\.")[0];

		LOGGER.info("---------------------------------------------------------------");
		LOGGER.info("[STAT] Processing file: " + inputFilePath + " date=" + date + " time=" + time);

		CodeProfiler.getInstance().clear();
		processor.setDateTime(date, time);
		GlobalTime.getInstance().increaseTime();
		FileInputStream inputFileStream = new FileInputStream(inputFilePath);
		long processingTime = processor.processSingleDataStream(inputFileStream);
		inputFileStream.close();
		CodeProfiler.getInstance().printStats();		
		
		LOGGER.info("[STAT] File: " + inputFilePath + " processed in " + processingTime + "ms");
		LOGGER.info("---------------------------------------------------------------");


		return processingTime;
	}

}
