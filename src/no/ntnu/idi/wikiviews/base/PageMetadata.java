/**
 * 
 */
package no.ntnu.idi.wikiviews.base;

/**
 * @author tkusm
 *
 */
public class PageMetadata {
	final PageId id;
	final String startDate;
	final String startTime;
	
	/**
     * @param id
     * @param startDate
     * @param startTime
     */
    public PageMetadata(PageId id, String startDate, String startTime) {
	    this.id = id;
	    this.startDate = startDate;
	    this.startTime = startTime;
    }
	
	
}
