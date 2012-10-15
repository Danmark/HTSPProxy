package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of the 'seq' field in requests and respones to/from the server.
 *
 */
public class MagicSequence {

	final private List<Long> freeSequences = new ArrayList<Long>();
	final private Map<Long, String> currentSequences = new HashMap<Long, String>();
	
	private long latest=0;
	
	public MagicSequence() {
	}
	
	/**
	 * Returnes the first free Long in the "sequence".
	 * @param method
	 * @return
	 */
	synchronized public Long pop(String method) {
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
	
	/**
	 * Returns the method name associated with the sequence-number i.
	 * @param i
	 * @return
	 */
	synchronized public String giveBack(Long i) {
		if (i.longValue() == latest) {
			shrinkLatest();
		}
		
		freeSequences.add(i);
		String s = currentSequences.get(i);
		currentSequences.remove(i);
		if (currentSequences.size() == 0) {
			latest = 0;
		}
		return s;
	}
	
	private void shrinkLatest() {
		if (! currentSequences.containsKey(latest)) {
			latest--;
			shrinkLatest();
		}
	}
}
