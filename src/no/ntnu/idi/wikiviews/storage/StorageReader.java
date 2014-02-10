/**
 * 
 */
package no.ntnu.idi.wikiviews.storage;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import no.ntnu.idi.wikiviews.base.PageDisplays;
import no.ntnu.idi.wikiviews.base.PageId;


/**
 * @author tkusm
 *
 */
public interface StorageReader {
	/**
	 * Returns history of page views.
	 * 
	 * @param page identifier of a page
	 * @return list of views of the page
	 * @throws IOException page entry not found
	 */
	public List<PageDisplays> read(PageId page) throws IOException;
	
	/**
	 * Returns set of known pages.
	 * 
	 * @return set of page available in the storage. 
	 */
	public Set<PageId> getKeys();
	
	/**
	 * Checks if page is stored in the storage.
	 * 
	 * @param page page to be checked
	 * @return true if page is in the set of known pages, false otherwise
	 */
	public boolean contains(PageId page);
}
