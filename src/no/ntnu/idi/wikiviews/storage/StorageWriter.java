/**
 * 
 */
package no.ntnu.idi.wikiviews.storage;

import java.util.List;
import java.io.IOException;

import no.ntnu.idi.wikiviews.base.PageId;
import no.ntnu.idi.wikiviews.base.PageDisplays;


/**
 * @author tkusm
 *
 */
public interface StorageWriter {
	
	/**
	 * Memorizes that page was viewed.
	 * 
	 * @param page Wikipedia page identifier 
	 * @param views description
	 * @return 
	 * @throws IOException 
	 */
	public void write(PageId page, PageDisplays views) throws IOException;
	
	
	/**
	 * Memorizes some history of page views.
	 * 
	 * @param page Wikipedia page identifier 
	 * @param list of views
	 * @return 
	 * @throws IOException 
	 */	
	public void write(PageId page, List<PageDisplays> views) throws IOException; 		
}
