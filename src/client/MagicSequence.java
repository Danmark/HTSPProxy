package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MagicSequence {

	final private List<Long> freeSequences = new ArrayList<Long>();
	final private Map<Long, String> currentSequences = new HashMap<Long, String>();
	
	private int latest=0;
	
	public MagicSequence() {
	}
	
	public Long pop(String method) {
		final Long r;
		if (freeSequences.size() > 0) {
			r = freeSequences.get(0);
			freeSequences.remove(0);
		} else {
			r = new Long(latest++);
		}
		
		currentSequences.put(r, method);
		
		return r;
	}
	
	public String giveBack(Long i) {
		freeSequences.add(i);
		String s = currentSequences.get(i);
		currentSequences.remove(i);
		return s;
	}
}
