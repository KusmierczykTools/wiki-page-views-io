/**
 * 
 */
package no.ntnu.idi.wikiviews.base;

/**
 * @author tkusm
 *
 */
public final class PageMetadata {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " " + startDate + " " + startTime;
	}

	/**
	 * @return the id
	 */
	public PageId getId() {
		return id;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	
	
}
