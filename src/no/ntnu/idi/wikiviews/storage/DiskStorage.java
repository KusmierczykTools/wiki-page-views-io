package no.ntnu.idi.wikiviews.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.ntnu.idi.wikiviews.aux.AuxIO;
import no.ntnu.idi.wikiviews.aux.CodeProfiler;
import no.ntnu.idi.wikiviews.base.PageId;
import no.ntnu.idi.wikiviews.base.PageDisplays;
import no.ntnu.idi.wikiviews.base.PageMetadata;

/**
 * @author tkusm
 * 
 */
public class DiskStorage implements StorageReaderWriter {

	final private String storagePath;

	/**
	 * @param storagePath
	 */
	public DiskStorage(String storagePath, String date, String time) {
		// super(date, time);
		this.storagePath = storagePath;
	}

	@Override
	public void write(PageId page, PageDisplays views) throws IOException {
		new File(page.getDirectory(storagePath)).mkdirs();
		String path = page.getPath(storagePath);

		FileWriter fw = new FileWriter(path, true);
		fw.write(views.toString() + " ");// appends the string to the file
		fw.close();
		
		CodeProfiler.getInstance().register("DiskWrite");
	}

	@Override
	public void write(PageId page, List<PageDisplays> listOfViews) throws IOException {
		new File(page.getDirectory(storagePath)).mkdirs();
		String path = page.getPath(storagePath);

		StringBuilder listStorageForm = new StringBuilder();
		for (PageDisplays v : listOfViews) {
			listStorageForm.append(v.toString() + " ");
		}

		FileWriter fw = new FileWriter(path, true);
		fw.write(listStorageForm.toString());// appends the string to the file
		fw.close();
		
		CodeProfiler.getInstance().register("DiskWrite");
	}

	public LinkedList<PageDisplays> read(PageId page) throws IOException {
		String path = page.getPath(storagePath);
		String line = AuxIO.readLine(path);
		CodeProfiler.getInstance().register("DiskRead");
		return PageDisplays.parseManyValuesString(line);
	}

	@Override
	public Set<PageId> getKeys() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(PageId page) {
		String path = page.getPath(storagePath);	
		CodeProfiler.getInstance().register("DiskContains");
		return new File(path).exists();
	}

	@Override
	public PageMetadata getPageMetadata(PageId page) {
		throw new UnsupportedOperationException();
	}

}
