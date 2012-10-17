package server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shared.HTSMsg;

public class ServerTags {
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
	
	public ServerTags() {
		this.tags = new HashMap<Long, HTSMsg>();
	}
	
	/**
	 * Adds a channel.
	 * @param msg
	 */
	public synchronized void add(HTSMsg msg){
		tags.put(((Number)msg.get(TAGID)).longValue(), msg);
	}
	
	/**
	 * Updates a channel.
	 * @param reply
	 */
	public synchronized void update(HTSMsg msg) {
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
	}
	
	public synchronized void remove(HTSMsg msg){
		tags.remove(((Number)msg.get(TAGID)).longValue());
	}
	
	
	public synchronized HTSMsg get(long tagId){
		return tags.get(tagId);
	}
	
	public synchronized Collection<HTSMsg> getAll(){
		return tags.values();
	}
}
