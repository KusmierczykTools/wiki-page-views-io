package no.ntnu.idi.wikiviews.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.Sharding;
import no.ntnu.idi.wikiviews.base.PageDisplays;
import no.ntnu.idi.wikiviews.base.PageId;
import no.ntnu.idi.wikiviews.base.PageMetadata;
import no.ntnu.idi.wikiviews.processing.PageViewsProcessor;

public class ShardedStorageReader implements StorageReader {

	protected static final Logger LOGGER = Logger.getLogger(PageViewsProcessor.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
	}

	private final ArrayList<CacheStorage> shardToStorage = new ArrayList<CacheStorage>();
	private final int numShards;

	/**
	 * @param baseDir
	 * @throws FileNotFoundException
	 */
	public ShardedStorageReader(String baseDir, int numShards) {
		super();
		this.numShards = numShards;
		for (int shardNo = 0; shardNo < numShards; ++shardNo) {
			String shardName = Sharding.getShardName(shardNo);
			String shardDir = baseDir + java.io.File.separator + shardName;

			DiskStorage disk = new DiskStorage(shardDir, null, null);
			CacheStorage cache = new CacheStorage(disk, Integer.MAX_VALUE, null, null);
			try {
				cache.restoreFromFile(shardDir + java.io.File.separator + CacheStorage.META_FILE_NAME);
			} catch (FileNotFoundException e) {
				LOGGER.severe("Error: Failure while restoring shard " + shardNo + " cache: " + e.getMessage());
			}
			shardToStorage.add(cache);
		}
	}

	@Override
	public List<PageDisplays> read(PageId page) throws IOException {
		int shardNo = Sharding.getShardNo(page.getName(), numShards);
		return shardToStorage.get(shardNo).read(page);
	}

	@Override
	public Set<PageId> getKeys() {
		Set<PageId> keys = new TreeSet<PageId>();
		for (CacheStorage storage : shardToStorage) {
			keys.addAll(storage.getKeys());
		}
		return keys;
	}

	@Override
	public boolean contains(PageId page) {
		int shardNo = Sharding.getShardNo(page.getName(), numShards);
		return shardToStorage.get(shardNo).contains(page);
		/*
		 * for (CacheStorage storage : shardToStorage) { if
		 * (storage.contains(page)) { return true; } } return false;
		 */
	}

	@Override
	public PageMetadata getPageMetadata(PageId page) {
		int shardNo = Sharding.getShardNo(page.getName(), numShards);
		return shardToStorage.get(shardNo).getPageMetadata(page);
	}

}
