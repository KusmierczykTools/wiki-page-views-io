/**
 * 
 */
package no.ntnu.idi.wikiviews.base;

import no.ntnu.idi.wikiviews.aux.StringFilter;

/**
 * @author tkusm
 * 
 */
public final class PageId implements Comparable<PageId> {

	final String project;
	final String name;

	/**
	 * @param project
	 *            Wikipedia project identifier
	 * @param name
	 *            Wikipedia page name
	 */
	public PageId(String project, String name) {
		this.project = project;
		this.name = name;
	}

	public String getFileName() {
		String filteredName = StringFilter.getInstance().filter(name);
		filteredName = filteredName.substring(0, Math.min(filteredName.length(), 200));
		return project + "_" + filteredName;
	}

	public String getPrefix() {
		String filteredName = StringFilter.getInstance().filter(name);
		String prefix = filteredName.substring(0, Math.min(filteredName.length(), 2));
		return prefix;
	}

	public String getDirectory(String baseDirectory) {
		String directory = baseDirectory + java.io.File.separator + project + "_" + getPrefix();
		return directory;
	}

	public String getPath(String baseDirectory) {
		return getDirectory(baseDirectory) + java.io.File.separator + getFileName();
	}

	public String getProject() {
		return project;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return project + " " + name;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof PageId) {
			PageId otherPage = (PageId) other;
			return this.name.equals(otherPage.name) && this.project.equals(otherPage.project);
		}
		return false;
	}

	@Override
	public int compareTo(PageId other) {
		if (this == other) {
			return 0;
		}
		int prjComparison = other.project.compareTo(this.project);
		if (prjComparison != 0) {
			return prjComparison;
		}
		return other.name.compareTo(this.name);
	}

}
