package no.ntnu.idi.wikiviews.aux;

import java.util.HashMap;
import java.util.Map;
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
	
	public void register(String operationName) {		
		Integer count = operationToCount.get(operationName);
		if (count==null) {
			operationToCount.put(operationName, 1);
		} else {
			operationToCount.put(operationName, 1+count);
		}		
	}

	
	public void clear() {
		LOGGER.info("[STAT] [CodeProfiler] Reset");
		operationToCount.clear();
	}
	
	public void printStats() {
		for (Map.Entry<String, Integer> e: operationToCount.entrySet()) {
			LOGGER.info("[STAT] [CodeProfiler] "+e.getKey()+": "+e.getValue()+" times executed");
		}
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
