package no.ntnu.idi.wikiviews.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.base.PageDisplaysHistory;

public class SingleFileStorageReader implements Iterable<PageDisplaysHistory> {

	final private static Logger LOGGER = Logger.getLogger(CacheStorage.class.getName());
	static {
		LOGGER.setLevel(Level.ALL);
	}

	final Scanner input;

	public SingleFileStorageReader(String path) throws IOException {
		input = new Scanner(new FileInputStream(path));
	}

	public SingleFileStorageReader(InputStream stream) {
		input = new Scanner(stream);
	}
	
	public PageDisplaysHistory read() throws IOException {
		String line = input.nextLine();	
		//System.out.println("[SingleFileStorageReader] line [length"+line.length()+"] = "+line);
		PageDisplaysHistory history = PageDisplaysHistory.parseString(line);
		CodeProfiler.getInstance().register("SingleFileRead");
		return history;
	}
	
	
	public void close() throws IOException {
		input.close();
	}

	@Override
	public Iterator<PageDisplaysHistory> iterator() {
		return new Iterator<PageDisplaysHistory>() {

			@Override
			public boolean hasNext() {
				return input.hasNext();
			}

			@Override
			public PageDisplaysHistory next() {
				try {
					return read();
				} catch (IOException e) {
					LOGGER.severe("Failed reading from file:"+e.getMessage());
				}
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

}
