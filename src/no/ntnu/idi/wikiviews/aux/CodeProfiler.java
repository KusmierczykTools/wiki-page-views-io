package no.ntnu.idi.wikiviews.aux;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.idi.wikiviews.base.PageDisplaysHistory;

public class CodeProfiler {
	
	protected static final Logger LOGGER = Logger.getLogger(PageDisplaysHistory.class.getName());
	{
		LOGGER.setLevel(Level.ALL);
	}
	
	private CodeProfiler(){
		operationToCount = new HashMap<String, Integer>();
	}
	
	protected HashMap<String, Integer> operationToCount;
	

	public void register(String operationName, int count) {
		Integer prevCount = operationToCount.get(operationName);
		if (prevCount==null) {
			operationToCount.put(operationName, count);
		} else {
			operationToCount.put(operationName, count+prevCount);
		}
	}
	
	public void register(String operationName) {
		this.register(operationName, 1);
	}

	
	public void clear() {
		LOGGER.info("[STAT] [CodeProfiler] Reset");
		operationToCount.clear();
	}

	public void printStats(String header, Iterable<String> keys) {
		StringBuilder report = new StringBuilder();		
		for (String key: keys) {
			report.append(" ");
			if (operationToCount.containsKey(key)) {
				report.append(key+": "+operationToCount.get(key));
			} else {
				report.append(key +": 0");
			}
		}
		LOGGER.info("[STAT] ["+header+"] time: "+GlobalTime.getInstance().getTime()+report.toString());
	}
	
	public void printStats(String header) {
		this.printStats(header, this.operationToCount.keySet());
	}
	
	/************************************************************************/

	private static CodeProfiler instance = null;

	public static synchronized CodeProfiler getInstance() {
		if (instance == null) {
			instance = new CodeProfiler();
		}
		return instance;
	}
}
