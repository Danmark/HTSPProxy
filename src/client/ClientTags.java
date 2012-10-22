package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import shared.HTSMsg;

public class ClientTags {
	/**
	 * required
	 */
	public final static String TAGID = "tagId";
	
	/**
	 * required
	 */
	public final static String TAGNAME = "tagName";
	
	/**
	 * optional
	 */
	public final static String TAGICON = "tagIcon";
	
	/**
	 * optional
	 */
	public final static String TAGTITLEICON = "tagTitledIcon";
	
	/**
	 * optional
	 */
	public final static String MEMBERS = "members";
	
	public static final String[] HTSMsgFields = {TAGID,TAGNAME,TAGICON,TAGTITLEICON,MEMBERS};
	
	private Map<Long, HTSMsg> tags;
	
	public ClientTags() {
		this.tags = new HashMap<Long, HTSMsg>();
	}
	
	/**
	 * Adds a channel.
	 * @param msg
	 * @return 
	 */
	public long add(HTSMsg msg){
		tags.put(((Number)msg.get(TAGID)).longValue(), msg);
		return ((Number)msg.get(TAGID)).longValue();
	}
	
	/**
	 * Updates a channel.
	 * @param reply
	 */
	public long update(HTSMsg msg) {
		HTSMsg chann = tags.get(((Number)msg.get(TAGID)).longValue());			
		
		for (String name : msg.keySet()){
			if (Arrays.asList(HTSMsgFields).contains(name)){
				chann.put(name, msg.get(name));
			}
			else if (name.equals("method")){
				//This is normal, do nothing...
			}
			else{
				System.out.println("N.B. Unrecognized field: " + name);
			}
		}
		return ((Number)msg.get(TAGID)).longValue();
	}
	
	public long remove(HTSMsg msg){
		tags.remove(((Number)msg.get(TAGID)).longValue());
		return ((Number)msg.get(TAGID)).longValue();
	}
	
	public synchronized HTSMsg getTag(long tagId){
		HTSMsg ret = tags.get(tagId);
		if (ret != null){
			return ret.clone();		
		} else {
			return ret;
		}
		
	}
}
