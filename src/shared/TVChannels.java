package shared;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;




public class TVChannels {
	/**
	 * required
	 */
	public static final String CHANNELID = "channelId";
	
	/**
	 * required
	 */
	public static final String CHANNELNUMBER = "channelNumber";
	
	/**
	 * required
	 */
	public static final String CHANNELNAME = "channelName";
	
	/**
	 * optional
	 */
	public static final String CHANNELICON = "channelIcon";
	
	/**
	 * optional
	 */
	public static final String EVENTID = "eventId";
	
	/**
	 * optional
	 */
	public static final String NEXTEVENTID = "nextEventId";
	
	/**
	 * optional
	 */
	public static final String TAGS = "tags";
	
	/**
	 * optional
	 */
	public static final String SERVICES = "services";
	
	public static final String[] HTSMsgFields = {CHANNELID,CHANNELNUMBER,CHANNELNAME,CHANNELICON,EVENTID,NEXTEVENTID,TAGS,SERVICES};

	private HashMap<Long,HTSMsg> channels;
	
	public TVChannels(){
		channels = new HashMap<Long,HTSMsg>();
	}
	
	public synchronized void add(HTSMsg msg) {
		channels.put(((Number)msg.get(CHANNELID)).longValue(), msg);
	}

	public synchronized void update(HTSMsg msg) {
		HTSMsg chann = channels.get(((Number)msg.get(CHANNELID)).longValue());			
		
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

	public synchronized void remove(HTSMsg msg) {
		channels.remove(((Number)msg.get(CHANNELID)).intValue());		
	}
	
	public synchronized HTSMsg get(long channelId){
		return channels.get(channelId);
	}
	
	public synchronized Collection<HTSMsg> getAll(){
		return channels.values();
	}
	
	public TVChannels clone(){
		TVChannels ret =  new TVChannels();
		ret.channels = (HashMap<Long, HTSMsg>) channels.clone();
		return ret;
	}
	

}
