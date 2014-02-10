/**
 * 
 */
package no.ntnu.idi.wikiviews.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.storage.CacheStorage;
import no.ntnu.idi.wikiviews.storage.DiskStorage;

/**
 * @author tkusm
 * 
 */
public class WikiViewsFlushCache {

	private final static Logger LOGGER = Logger.getLogger(WikiViewsFlushCache.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.setLevel(Level.ALL);
		LOGGER.info("Updates wikiviews storage by flushing cache.");
		if (args.length < 1) {
			LOGGER.info("Argument expected: storage-path. Optional argument: flush type: full/partial.");
			System.exit(-1);
		}

		String storageDir = args[0];
		final boolean fullFlush;
		if (args.length > 1) {
			fullFlush = (args[1].toLowerCase().startsWith("full"));
		} else {
			fullFlush = false;
		}
		LOGGER.info(String.format("storage=%s fullFlush=%b", storageDir, fullFlush));

		DiskStorage storage = new DiskStorage(storageDir, null, null);
		CacheStorage cache = new CacheStorage(storage, 0, null, null);
		loadCache(storageDir, cache);
		flushCache(storageDir, cache, fullFlush);
		storeCache(storageDir, cache);

		LOGGER.info("Done.");
	}

	private static void flushCache(String storageDir, CacheStorage cache, final boolean fullFlush) {
		try {
			if (fullFlush) {
				cache.flushAll();
			} else {
				cache.flushExisting(storageDir);
			}
		} catch (Exception e) {
			LOGGER.severe("Failure while flushing:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void storeCache(String storageDir, CacheStorage cache) {
		try {
			cache.storeToFile(storageDir + java.io.File.separator + "meta.txt");
		} catch (Exception e) {
			LOGGER.severe("Failure while serializing:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void loadCache(String storageDir, CacheStorage cache) {
		new File(storageDir).mkdirs();
		try {
			cache.restoreFromFile(storageDir + java.io.File.separator + "meta.txt");
		} catch (FileNotFoundException e) {
			LOGGER.info("Storage cache not found! Staring with empty cache!");
		}
	}
}
